package es.mjsplayground.pmdm.firebase

import android.graphics.Bitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import java.io.ByteArrayOutputStream
import java.util.concurrent.atomic.AtomicBoolean
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** Requiere Emulator Suite local activo. Nunca utiliza un proyecto real. */
class NubeIntegracionTest {
    @get:Rule val regla = createAndroidComposeRule<MainActivity>()
    private fun esperar(texto: String) {
        regla.waitUntil(15000) { regla.onAllNodesWithText(texto).fetchSemanticsNodes().isNotEmpty() }
    }
    @Test fun cuentaTiempoRealArchivoPrivadoYCierre() {
        val repo = (regla.activity.application as LaboratorioApplication).repo
        regla.runOnIdle { repo.auth.signOut() }
        esperar("Crear cuenta local")
        regla.onNodeWithText("Correo local de prueba").performTextInput("android-${System.nanoTime()}@example.test")
        regla.onNodeWithText("Contraseña local de prueba").performTextInput("Solo-Emulador-84")
        regla.onNodeWithText("Crear cuenta local").performClick()
        esperar("Cerrar sesión")
        regla.onNodeWithText("Título de la nota").performTextInput("Nota desde Android")
        regla.onNodeWithText("Guardar nota").performClick()
        esperar("Nota guardada.")
        esperar("Eliminar nota")
        regla.onNodeWithText("Eliminar nota").performScrollTo().performClick()
        esperar("Nota eliminada.")
        regla.waitUntil(15000) { regla.onAllNodesWithText("Nota desde Android").fetchSemanticsNodes().isEmpty() }
        val bytes = ByteArrayOutputStream().also { out ->
            Bitmap.createBitmap(16, 16, Bitmap.Config.ARGB_8888).apply {
                eraseColor(android.graphics.Color.GREEN)
                assertTrue(compress(Bitmap.CompressFormat.PNG, 100, out))
                recycle()
            }
        }.toByteArray()
        val terminada = AtomicBoolean(false)
        val correcta = AtomicBoolean(false)
        regla.runOnIdle { repo.subir(bytes, "image/png") { correcta.set(it); terminada.set(true) } }
        regla.waitUntil(15000) { terminada.get() }
        assertTrue(correcta.get())
        regla.onNodeWithText("Descargar mi imagen").performScrollTo().performClick()
        esperar("Imagen descargada.")
        regla.onNodeWithContentDescription("Imagen privada descargada").assertExists()
        regla.onNodeWithText("Título de la nota").performScrollTo().performTextInput("Borrador privado de la primera cuenta")
        regla.onNodeWithText("Cerrar sesión").performScrollTo().performClick()
        esperar("Crear cuenta local")
        regla.onNodeWithText("Correo local de prueba").performTextClearance()
        regla.onNodeWithText("Correo local de prueba").performTextInput("otra-${System.nanoTime()}@example.test")
        regla.onNodeWithText("Contraseña local de prueba").performTextInput("Solo-Emulador-84")
        regla.onNodeWithText("Crear cuenta local").performClick()
        esperar("Cerrar sesión")
        regla.onNodeWithText("Borrador privado de la primera cuenta").assertDoesNotExist()
        regla.onNodeWithContentDescription("Imagen privada descargada").assertDoesNotExist()
        regla.onNodeWithText("Todavía no hay notas.").assertExists()
        regla.onNodeWithText("Cerrar sesión").performClick()
        esperar("Crear cuenta local")
    }
}
