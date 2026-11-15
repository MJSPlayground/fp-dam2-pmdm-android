package es.mjsplayground.pmdm.multimedia.avanzada.edicion
import android.content.Context
import android.content.Intent
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
@RunWith(AndroidJUnit4::class)
class StreamTest {
    @get:Rule val compose = createEmptyComposeRule()
    @Test fun recibePrimerFotogramaYLiberaAlVolver() {
        val contexto = ApplicationProvider.getApplicationContext<Context>()
        val fuente = InstrumentationRegistry.getArguments().getString("fuentePrueba")
            ?: "android.resource://${contexto.packageName}/${R.raw.muestra}"
        ActivityScenario.launch<StreamActivity>(Intent(contexto, StreamActivity::class.java).putExtra("fuente", fuente)).use {
            compose.waitUntil(30_000) { compose.onAllNodesWithText("Primera imagen recibida").fetchSemanticsNodes().isNotEmpty() }
            compose.onNodeWithText("Volver al editor").performScrollTo().performClick()
        }
    }
}
