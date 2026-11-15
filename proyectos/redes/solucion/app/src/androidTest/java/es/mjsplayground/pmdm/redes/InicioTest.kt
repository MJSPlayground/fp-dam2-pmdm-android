package es.mjsplayground.pmdm.redes

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.assertIsDisplayed
import org.junit.Rule
import org.junit.Test

class InicioTest {
    @get:Rule val pantalla = createAndroidComposeRule<MainActivity>()
    @Test fun arranqueSinRadio() {
        pantalla.onNodeWithText("Mensajería cercana").assertIsDisplayed()
        pantalla.onNodeWithText("Ninguna operación Bluetooth iniciada.").performScrollTo().assertIsDisplayed()
    }
}
