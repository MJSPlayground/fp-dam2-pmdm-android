package es.mjsplayground.pmdm.ia

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
import org.junit.Assert.*
import org.junit.Assume.assumeTrue
import org.junit.Rule
import org.junit.Test

/** Servidor local SIMULADO en8095. No se usa una foto del usuario ni un modelo remoto. */
class MultimodalUiTest {
    @get:Rule val regla = createAndroidComposeRule<MainActivity>()
    private val dispositivo get() = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    private fun esperar(texto: String) {
        try {
            regla.waitUntil(15000) {
                regla.onAllNodesWithText(texto).fetchSemanticsNodes(atLeastOneRootRequired = false).isNotEmpty()
            }
        } catch (fallo: AssertionError) {
            println(regla.onRoot().printToString())
            dispositivo.takeScreenshot(File(regla.activity.externalCacheDir,"ia-multimodal-fallo.png"))
            throw fallo
        }
    }
    @Test fun selectorRealPreparaImagenYQuitarRestauraSoloTexto() {
        assumeTrue("MediaStore sin permiso heredado requiere API29+", Build.VERSION.SDK_INT >= 29)
        val resolver = regla.activity.contentResolver
        val bytes = ByteArrayOutputStream().also { salida ->
            Bitmap.createBitmap(256,192,Bitmap.Config.ARGB_8888).apply {
                eraseColor(android.graphics.Color.CYAN)
                assertTrue(compress(Bitmap.CompressFormat.PNG,100,salida))
                recycle()
            }
        }.toByteArray()
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME,"pmdm-ia-${System.nanoTime()}.png")
            put(MediaStore.Images.Media.MIME_TYPE,"image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH,"Pictures/PMDM")
        })!!
        try {
            resolver.openOutputStream(uri)!!.use { it.write(bytes) }
            regla.onNodeWithText("Pregunta").performTextInput("Describe la imagen sintética de prueba")
            regla.onNodeWithText("Preparar una imagen propia").performScrollTo().performClick()
            // API 37: Photo Picker Compose expone descripciones y no el ID antiguo.
            val selector = By.res(java.util.regex.Pattern.compile(".*:id/icon_thumbnail"))
            val plazo = SystemClock.uptimeMillis() + 15000
            var miniatura: UiObject2? = null
            while (miniatura == null && SystemClock.uptimeMillis() < plazo) {
                miniatura = dispositivo.findObject(selector)
                    ?: dispositivo.findObject(By.descStartsWith("Photo taken on"))
                if (miniatura == null) SystemClock.sleep(100)
            }
            assertNotNull("Debe aparecer la miniatura del fixture propio en PhotoPicker",miniatura)
            miniatura!!.click()
            dispositivo.wait(
                Until.findObject(By.pkg("com.google.android.photopicker").text("Done")), 5000
            )?.click()
            esperar(regla.activity.getString(R.string.imagen_lista))
            // El selector puede devolver el foco al campo. Cerramos el IME como haría
            // el usuario antes de tocar una acción, evitando una transición de insets.
            regla.runOnIdle {
                val decor = regla.activity.window.decorView
                val teclado = regla.activity.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                teclado.hideSoftInputFromWindow(decor.windowToken,0)
                decor.clearFocus()
            }
            if (Build.VERSION.SDK_INT >= 30) regla.waitUntil(5000) {
                regla.activity.window.decorView.rootWindowInsets?.isVisible(android.view.WindowInsets.Type.ime()) != true
            }
            regla.waitForIdle()
            regla.onNodeWithText("Consultar").performScrollTo().assertIsEnabled().performClick()
            val multimodal = "Demostración local: se recibió texto e imagen."
            esperar(multimodal)
            regla.onNodeWithText(regla.activity.getString(R.string.simulada)).performScrollTo().assertIsDisplayed()
            regla.onNodeWithText(multimodal).performScrollTo().assertIsDisplayed()
            dispositivo.takeScreenshot(File(regla.activity.externalCacheDir,"ia-multimodal-oscuro.png"))
            regla.onNodeWithText("Quitar imagen").performScrollTo().performClick()
            regla.onNodeWithText(regla.activity.getString(R.string.sin_imagen)).assertExists()
            regla.onNodeWithText("Consultar").performScrollTo().performClick()
            val texto = "Demostración local: se recibió una consulta de texto."
            esperar(texto)
            regla.onNodeWithText(texto).performScrollTo().assertIsDisplayed()
            regla.onNodeWithText(multimodal).assertDoesNotExist()
            regla.onNodeWithText(regla.activity.getString(R.string.imagen_lista)).assertDoesNotExist()
            regla.onNodeWithText("Consultar").assertIsEnabled()
        } finally { resolver.delete(uri,null,null) }
    }
}
