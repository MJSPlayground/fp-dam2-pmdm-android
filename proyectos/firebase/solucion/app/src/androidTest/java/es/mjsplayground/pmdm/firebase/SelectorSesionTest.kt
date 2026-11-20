package es.mjsplayground.pmdm.firebase

import android.content.ContentValues
import android.graphics.Bitmap
import android.os.Build
import android.os.SystemClock
import android.provider.MediaStore
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean
import org.junit.Assert.*
import org.junit.Assume.assumeTrue
import org.junit.Rule
import org.junit.Test

/** Emulator Suite activo. Usa una imagen sintética propia, no fotos del dispositivo. */
class SelectorSesionTest {
    @get:Rule val regla = createAndroidComposeRule<MainActivity>()
    private val dispositivo get() = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    private val repo get() = (regla.activity.application as LaboratorioApplication).repo
    private fun esperar(texto: String) {
        try {
            regla.waitUntil(20000) {
                regla.onAllNodesWithText(texto).fetchSemanticsNodes(atLeastOneRootRequired = false).isNotEmpty()
            }
        } catch (fallo: Throwable) {
            dispositivo.dumpWindowHierarchy(File(regla.activity.externalCacheDir, "firebase-espera-fallida.xml"))
            dispositivo.takeScreenshot(File(regla.activity.externalCacheDir, "firebase-espera-fallida.png"))
            throw fallo
        }
    }
    private fun cuenta(prefijo: String) {
        regla.onNodeWithText("Correo local de prueba").performScrollTo().performTextClearance()
        regla.onNodeWithText("Correo local de prueba").performTextInput("$prefijo-${System.nanoTime()}@example.test")
        regla.onNodeWithText("Contraseña local de prueba").performScrollTo().performTextInput("Solo-Emulador-84")
        regla.onNodeWithText("Crear cuenta local").performScrollTo().performClick()
        esperar("Cerrar sesión")
    }

    @Test fun lecturaDiferidaNoSubeArchivoEnLaCuentaNueva() {
        regla.runOnIdle { repo.auth.signOut() }
        esperar("Crear cuenta local")
        cuenta("diferida-a")
        val uidA = repo.auth.currentUser!!.uid
        val origen = android.net.Uri.parse("content://es.mjsplayground.pmdm.firebase.test.retardo/imagen")
        val resolver = regla.activity.contentResolver
        val bytes = ByteArrayOutputStream().also { salida ->
            Bitmap.createBitmap(8,8,Bitmap.Config.ARGB_8888).apply {
                eraseColor(android.graphics.Color.GREEN); compress(Bitmap.CompressFormat.PNG,100,salida); recycle()
            }
        }.toByteArray()
        resolver.call(origen,"preparar",null,android.os.Bundle().apply { putByteArray("bytes",bytes) })
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val intentContrato = androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia()
            .createIntent(regla.activity, androidx.activity.result.PickVisualMediaRequest(
                androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly))
        val filtro = android.content.IntentFilter(intentContrato.action).apply { intentContrato.type?.let { addDataType(it) } }
        val monitor = instrumentation.addMonitor(filtro,
            android.app.Instrumentation.ActivityResult(android.app.Activity.RESULT_OK,
                android.content.Intent().setData(origen)), true)
        try {
            // Este caso intercepta solo el selector: el otro test sí recorre PhotoPicker real.
            regla.onNodeWithText("Seleccionar y subir imagen").performScrollTo().performClick()
            regla.waitUntil(10000) { resolver.call(origen,"estado",null,null)!!.getBoolean("abierta") }
            val cambio = AtomicBoolean(false)
            regla.runOnIdle {
                repo.auth.createUserWithEmailAndPassword("diferida-b-${System.nanoTime()}@example.test", "Solo-Emulador-84")
                    .addOnCompleteListener { assertTrue(it.isSuccessful); cambio.set(true) }
            }
            regla.waitUntil(15000) { cambio.get() && repo.auth.currentUser?.uid != uidA }
            esperar("Cerrar sesión")
            resolver.call(origen,"liberar",null,null)
            regla.waitUntil(5000) { resolver.call(origen,"estado",null,null)!!.getBoolean("terminada") }
            // La lectura ya puede terminar. Repetimos consultas reales de Storage para detectar
            // la regresión que subiría los bytes de A a la ruta de B tras la suspensión.
            repeat(3) {
                val consultada = AtomicBoolean(false)
                val existe = AtomicBoolean(false)
                regla.runOnIdle { repo.descargar { existe.set(it != null); consultada.set(true) } }
                regla.waitUntil(10000) { consultada.get() }
                assertFalse("La imagen diferida de A no debe existir en B", existe.get())
            }
            regla.onNodeWithContentDescription("Imagen privada descargada").assertDoesNotExist()
            regla.onNodeWithText("Cerrar sesión").performScrollTo().performClick()
            esperar("Crear cuenta local")
        } finally {
            resolver.call(origen,"liberar",null,null)
            instrumentation.removeMonitor(monitor)
        }
    }

    @Test fun selectorRealVuelveYSubeSoloEnSuSesion() {
        assumeTrue("MediaStore sin permiso heredado requiere API29+", Build.VERSION.SDK_INT >= 29)
        regla.runOnIdle { repo.auth.signOut() }
        esperar("Crear cuenta local")
        cuenta("picker")
        val uidA = repo.auth.currentUser!!.uid
        val bytes = ByteArrayOutputStream().also { salida ->
            Bitmap.createBitmap(256, 192, Bitmap.Config.ARGB_8888).apply {
                eraseColor(android.graphics.Color.MAGENTA)
                assertTrue(compress(Bitmap.CompressFormat.PNG, 100, salida))
                recycle()
            }
        }.toByteArray()
        val resolver = regla.activity.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "pmdm-firebase-${System.nanoTime()}.png")
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/PMDM")
        })!!
        try {
            resolver.openOutputStream(uri)!!.use { it.write(bytes) }
            regla.onNodeWithText("Seleccionar y subir imagen").performScrollTo().performClick()
            val selector = By.res(java.util.regex.Pattern.compile(".*:id/icon_thumbnail"))
            // API 37 usa un Photo Picker Compose sin icon_thumbnail; la imagen
            // del aula en inglés expone la miniatura mediante su descripción.
            val plazo = SystemClock.uptimeMillis() + 15000
            var miniatura: UiObject2? = null
            while (miniatura == null && SystemClock.uptimeMillis() < plazo) {
                miniatura = dispositivo.findObject(selector)
                    ?: dispositivo.findObject(By.descStartsWith("Photo taken on"))
                if (miniatura == null) SystemClock.sleep(100)
            }
            if (miniatura == null) {
                dispositivo.dumpWindowHierarchy(File(regla.activity.externalCacheDir, "picker-sin-miniatura.xml"))
                fail("No se encontró miniatura en PhotoPicker; revisa el XML del selector")
            }
            miniatura!!.click()
            dispositivo.wait(
                Until.findObject(By.pkg("com.google.android.photopicker").text("Done")), 5000
            )?.click()
            esperar("Imagen subida al emulador.")
            assertEquals(uidA, repo.auth.currentUser?.uid)
            regla.onNodeWithText("Descargar mi imagen").performScrollTo().performClick()
            esperar("Imagen descargada.")
            regla.onNodeWithContentDescription("Imagen privada descargada").performScrollTo().assertIsDisplayed()
            dispositivo.takeScreenshot(File(regla.activity.externalCacheDir, "firebase-picker-imagen.png"))
            // Volver a la misma cuenta al salir y entrar en la Activity no invalida el turno del selector.
            regla.onNodeWithText("Título de la nota").performScrollTo().performTextInput("Borrador de A")
            regla.onNodeWithText("Cerrar sesión").performScrollTo().performClick()
            esperar("Crear cuenta local")
            cuenta("segunda-picker")
            assertNotEquals(uidA, repo.auth.currentUser?.uid)
            regla.onNodeWithText("Borrador de A").assertDoesNotExist()
            regla.onNodeWithContentDescription("Imagen privada descargada").assertDoesNotExist()
            regla.onNodeWithText("Descargar mi imagen").performScrollTo().performClick()
            esperar("No se pudo procesar la imagen. Usa PNG/JPEG de hasta 1 MiB y dimensiones máximas 4096.")
            regla.onNodeWithContentDescription("Imagen privada descargada").assertDoesNotExist()
            regla.onNodeWithText("Cerrar sesión").performScrollTo().performClick()
            esperar("Crear cuenta local")
        } finally { resolver.delete(uri, null, null) }
    }
}
