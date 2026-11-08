package es.mjsplayground.pmdm.ecosistema

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PrimeraPantallaTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun comprobarMuestraUnaRespuesta() {
        composeRule.onNodeWithText("Mi primera app Android").assertIsDisplayed()
        composeRule.onNodeWithText("La aplicación responde").assertDoesNotExist()
        composeRule.onNodeWithText("Comprobar").performClick()
        composeRule.onNodeWithText("La aplicación responde").assertIsDisplayed()
        composeRule.onNodeWithText("Comprobar").performClick()
        composeRule.onNodeWithText("La aplicación responde").assertIsDisplayed()
    }
}
