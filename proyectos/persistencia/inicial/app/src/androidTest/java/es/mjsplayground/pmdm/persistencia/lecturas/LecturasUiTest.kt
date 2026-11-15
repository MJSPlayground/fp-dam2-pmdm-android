package es.mjsplayground.pmdm.persistencia.lecturas

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import es.mjsplayground.pmdm.persistencia.lecturas.data.Preferencias
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@org.junit.Ignore("Activa al completar las etapas guiadas de persistencia")
@RunWith(AndroidJUnit4::class)
class LecturasUiTest {
    @get:Rule val compose = createEmptyComposeRule()

    /** Usa y reinicia únicamente los datos del laboratorio instalado para esta prueba. */
    @Test fun demostracionMarcadoFiltroYRecreacion() {
        val contexto = ApplicationProvider.getApplicationContext<Context>()
        contexto.deleteDatabase("lecturas.db")
        runBlocking { Preferencias(contexto).cambiarSoloPendientes(false) }
        ActivityScenario.launch(MainActivity::class.java).use { escenario ->
            compose.onNodeWithText("Cargar demostración local").performClick()
            compose.waitUntil(10_000) {
                compose.onAllNodesWithText("Estado y persistencia").fetchSemanticsNodes().isNotEmpty()
            }
            compose.onAllNodesWithText("Marcar leída").onFirst().performScrollTo().performClick()
            compose.onNodeWithText("Mostrar solo pendientes").performScrollTo().performClick()
            compose.waitUntil(10_000) {
                compose.onAllNodesWithText("Estado y persistencia").fetchSemanticsNodes().isEmpty()
            }
            compose.onNodeWithText("Diseño de repositorios").assertExists()
            escenario.recreate()
            compose.onNodeWithText("Mostrar solo pendientes").assertIsOn()
            compose.onNodeWithText("Estado y persistencia").assertDoesNotExist()
            compose.onNodeWithText("Mostrar solo pendientes").performClick()
            compose.waitUntil(10_000) {
                compose.onAllNodesWithText("Estado y persistencia").fetchSemanticsNodes().isNotEmpty()
            }
            compose.onNodeWithText("Leída").assertExists()
        }
    }
}
