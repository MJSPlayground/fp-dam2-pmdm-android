package es.mjsplayground.pmdm.multimedia.reproductor

import android.net.Uri
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReproductorTest {
    @get:Rule val regla = createAndroidComposeRule<MainActivity>()
    private fun esperar(texto: String) {
        regla.waitUntil(10_000) { regla.onAllNodesWithText(texto).fetchSemanticsNodes().isNotEmpty() }
    }

    @Test fun reproducePausaDetieneTerminaMuestraErrorYLibera() {
        esperar("Preparado")
        regla.onNodeWithText("Reproducir").performScrollTo().performClick()
        esperar("Reproduciendo")
        regla.onNodeWithText("Reproducción en curso").assertIsDisplayed()
        regla.onNodeWithText("Pausar").performScrollTo().performClick()
        esperar("Preparado")
        regla.onNodeWithText("Detener").performScrollTo().performClick()
        esperar("Detenido")
        regla.onNodeWithText("Reproducir").performScrollTo().performClick()
        esperar("Finalizado")
        regla.activityRule.scenario.onActivity { it.control!!.cargar(Uri.parse("file:///archivo-inexistente.mp4")) }
        esperar("No se pudo reproducir el contenido. Selecciona otro archivo o el audio de ejemplo.")
        val anterior = regla.activity.control!!
        regla.activityRule.scenario.moveToState(Lifecycle.State.CREATED)
        assertTrue(anterior.liberado)
        regla.activityRule.scenario.moveToState(Lifecycle.State.RESUMED)
        esperar("Preparado")
        assertNotSame(anterior, regla.activity.control)
    }
}
