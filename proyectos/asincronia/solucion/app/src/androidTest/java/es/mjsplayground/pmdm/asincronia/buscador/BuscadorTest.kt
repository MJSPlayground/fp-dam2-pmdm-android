package es.mjsplayground.pmdm.asincronia.buscador

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BuscadorTest {
    @get:Rule val pantalla = createAndroidComposeRule<MainActivity>()

    private fun esperarTexto(texto: String) {
        pantalla.waitUntil(5_000) {
            pantalla.onAllNodesWithText(texto).fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test fun filtrarRecuperarseYMostrarAyuda() {
        pantalla.onNodeWithText("Buscar por título").performTextReplacement("kotlin")
        esperarTexto("Kotlin en práctica")
        pantalla.onNodeWithText("Kotlin y corrutinas").assertIsDisplayed()
        // Se usa la etiqueta visible del control; el texto no es otro botón independiente.
        pantalla.onNode(androidx.compose.ui.test.isToggleable()).performClick()
        pantalla.waitUntil(5_000) {
            pantalla.onAllNodesWithText("Kotlin y corrutinas").fetchSemanticsNodes().isEmpty()
        }
        pantalla.onNodeWithText("Kotlin en práctica").assertIsDisplayed()
        pantalla.onNodeWithText("Kotlin y corrutinas").assertDoesNotExist()
        pantalla.onNodeWithText("Buscar por título").performTextReplacement("!error")
        esperarTexto("Consulta rechazada. Cambia el texto y vuelve a intentarlo.")
        pantalla.onNodeWithText("Buscar por título").performTextReplacement("rutas")
        esperarTexto("Rutas por el bosque")
        pantalla.onNodeWithText("Ayuda").performClick()
        esperarTexto("Escribe parte del título y espera a que termine la búsqueda.")
    }
}
