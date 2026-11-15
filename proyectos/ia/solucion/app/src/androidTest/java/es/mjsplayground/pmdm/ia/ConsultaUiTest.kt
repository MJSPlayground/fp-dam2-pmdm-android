package es.mjsplayground.pmdm.ia

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test

/** Requiere servidor.py en modo simulado; no llama a un modelo remoto. */
class ConsultaUiTest {
    @get:Rule val regla = createAndroidComposeRule<MainActivity>()
    private fun esperar(texto: String) {
        regla.waitUntil(15000) { regla.onAllNodesWithText(texto).fetchSemanticsNodes().isNotEmpty() }
    }
    @Test fun respuestaErrorRecuperacionYCancelacionSinResultadoTardio() {
        regla.onNodeWithText("Pregunta").performTextInput("Consulta de prueba")
        regla.onNodeWithText("Consultar").performScrollTo().performClick()
        val origen = regla.activity.getString(R.string.simulada)
        esperar(origen)
        regla.onNodeWithText("Pregunta").performScrollTo().performTextReplacement("!error")
        regla.onNodeWithText("Consultar").performScrollTo().performClick()
        esperar(regla.activity.getString(R.string.error_red))
        regla.onNodeWithText("Pregunta").performScrollTo().performTextReplacement("Consulta recuperada")
        regla.onNodeWithText("Consultar").performScrollTo().performClick()
        esperar(origen)
        regla.onNodeWithText("Pregunta").performScrollTo().performTextReplacement("!demora")
        regla.onNodeWithText("Consultar").performScrollTo().performClick()
        esperar("Cancelar")
        regla.onNodeWithText("Cancelar").performScrollTo().performClick()
        esperar(regla.activity.getString(R.string.cancelada))
        Thread.sleep(2500)
        regla.waitForIdle()
        regla.onNodeWithText(origen).assertDoesNotExist()
        regla.onNodeWithText("Consultar").assertIsEnabled()
    }
}
