package es.mjsplayground.pmdm.calidad.revision

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import es.mjsplayground.pmdm.calidad.revision.ui.RevisionTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class RevisionUiTest {
    @get:Rule val pantalla = createComposeRule()
    @Test fun estadoVacio() {
        pantalla.setContent { RevisionTheme { RevisionPantalla(RevisionState(), {}) } }
        pantalla.onNodeWithText("No hay notas").assertIsDisplayed()
    }
    @Test fun listaYEventoDeRecarga() {
        var llamadas = 0
        pantalla.setContent {
            RevisionTheme { RevisionPantalla(RevisionState(notas = listOf(Nota(1, "Nota propia"))), { llamadas++ }) }
        }
        pantalla.onNodeWithText("Nota propia").assertIsDisplayed()
        pantalla.onNodeWithText("Volver a cargar").performClick()
        pantalla.runOnIdle { assertEquals(1, llamadas) }
    }
    @Test fun errorOfreceReintento() {
        pantalla.setContent { RevisionTheme { RevisionPantalla(RevisionState(error = true), {}) } }
        pantalla.onNodeWithText("No se pudieron recuperar las notas. Reintenta.").assertIsDisplayed()
        pantalla.onNodeWithText("Volver a cargar").assertIsDisplayed()
    }
}
