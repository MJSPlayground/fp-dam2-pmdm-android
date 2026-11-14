package es.mjsplayground.pmdm.multimedia.mapa

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import org.junit.Rule
import org.junit.Test

/** Comprobación de arranque y estado; no certifica teselas remotas ni GPS físico. */
class MapaTest {
    @get:Rule val pantalla = createAndroidComposeRule<MainActivity>()

    @Test fun mapaNoSolicitaUbicacionAlAbrir() {
        pantalla.onNodeWithText("Mapa de lugares").assertIsDisplayed()
        pantalla.onNodeWithText("Mapa: OpenFreeMap · © OpenMapTiles · Datos © OpenStreetMap contributors").assertIsDisplayed()
        pantalla.onNodeWithText("Todavía no has solicitado una ubicación.").assertExists()
    }
}
