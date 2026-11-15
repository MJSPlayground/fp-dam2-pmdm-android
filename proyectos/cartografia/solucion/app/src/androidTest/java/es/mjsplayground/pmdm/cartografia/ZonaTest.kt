package es.mjsplayground.pmdm.cartografia

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.assertIsDisplayed
import org.junit.Rule
import org.junit.Test

class ZonaTest {
    @get:Rule val pantalla = createAndroidComposeRule<MainActivity>()

    @Test fun simulacionSinPermisoNiRed() {
        pantalla.onNodeWithText("Simular centro de la zona").performScrollTo().performClick()
        pantalla.onNodeWithText("Dentro de la zona", substring = true).performScrollTo().assertIsDisplayed()
        pantalla.onNodeWithText("Simular punto exterior").performScrollTo().performClick()
        pantalla.onNodeWithText("Fuera de la zona", substring = true).performScrollTo().assertIsDisplayed()
        pantalla.onNodeWithText("Iniciar seguimiento visible").performScrollTo().assertIsDisplayed()
    }
}
