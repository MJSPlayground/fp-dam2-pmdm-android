package es.mjsplayground.pmdm.diseno.galeria

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test

class GaleriaUiTest {
    @get:Rule val pantalla = createAndroidComposeRule<MainActivity>()
    private val contexto get() = InstrumentationRegistry.getInstrumentation().targetContext
    private fun texto(id: Int) = contexto.getString(id)
    private fun destino(id: Int) {
        val compacta = pantalla.onAllNodesWithTag("barra").fetchSemanticsNodes().isNotEmpty()
        if (!compacta) {
            pantalla.onNodeWithText(texto(R.string.menu)).performClick()
        }
        pantalla.onNode(hasText(texto(id)) and hasAnyAncestor(hasTestTag(if (compacta) "barra" else "drawer"))).performClick()
    }

    @Test fun abrirDetalleGuardarYFiltrar() {
        val bosque = texto(R.string.lamina_bosque)
        pantalla.onNodeWithText(bosque).performClick()
        pantalla.onNodeWithText(texto(R.string.volver)).assertIsDisplayed().performClick()
        pantalla.onNodeWithContentDescription(contexto.getString(R.string.accion_favorito, texto(R.string.guardar), bosque)).performClick()
        destino(R.string.favoritos)
        pantalla.onNodeWithText(bosque).assertIsDisplayed()
        pantalla.onNodeWithContentDescription(contexto.getString(R.string.accion_favorito, texto(R.string.quitar), bosque)).performClick()
        pantalla.onNodeWithText(texto(R.string.vacia)).assertIsDisplayed()
        destino(R.string.catalogo)
        pantalla.onNodeWithText(texto(R.string.actualizar)).performClick()
        pantalla.waitUntil(5_000) {
            try { pantalla.onNodeWithText(contexto.getString(R.string.revision, 1)).assertIsDisplayed(); true }
            catch (_: AssertionError) { false }
        }
    }
}
