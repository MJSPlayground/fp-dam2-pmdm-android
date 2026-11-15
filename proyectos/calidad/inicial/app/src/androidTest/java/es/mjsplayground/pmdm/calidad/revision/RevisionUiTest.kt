package es.mjsplayground.pmdm.calidad.revision

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import es.mjsplayground.pmdm.calidad.revision.ui.RevisionTheme
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

class RevisionUiTest {
    @get:Rule val pantalla = createComposeRule()
    @Test fun estadoVacio() {
        pantalla.setContent { RevisionTheme { RevisionPantalla(RevisionState(), {}) } }
        pantalla.onNodeWithText("No hay notas").assertIsDisplayed()
    }

    // TODO ui-evento: mostrar una nota, pulsar y verificar una llamada.
    @Ignore("Completa TODO ui-evento antes de activar esta prueba")
    @Test fun listaYEventoDeRecarga() { }

    // TODO ui-error: representar error y comprobar mensaje y reintento.
    @Ignore("Completa TODO ui-error antes de activar esta prueba")
    @Test fun errorOfreceReintento() { }
}
