package es.mjsplayground.pmdm.sensores

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import org.junit.Rule
import org.junit.Test

class DispositivoTest {
    @get:Rule val regla = createAndroidComposeRule<MainActivity>()
    @Test fun seleccionEventosYAccionesSiguenAccesibles() {
        regla.onAllNodesWithText("Acelerómetro")[0].assertIsDisplayed()
        regla.onNodeWithText("Giroscopio").performClick()
        regla.onAllNodesWithText("Giroscopio")[0].assertIsDisplayed()
        regla.onNodeWithText("Luz ambiental").performClick()
        regla.onNodeWithText("Temperatura ambiente").performClick()
        regla.onNodeWithText("Respuesta háptica").performScrollTo().performClick()
        regla.onNodeWithText("Mostrar demostración biométrica").performScrollTo().assertIsDisplayed()
        regla.activityRule.scenario.recreate()
        regla.onAllNodesWithText("Acelerómetro")[0].assertIsDisplayed()
    }
}
