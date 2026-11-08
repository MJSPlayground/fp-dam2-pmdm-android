package es.mjsplayground.pmdm.desarrollo.cuaderno.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onLast
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.runners.AndroidJUnit4
import es.mjsplayground.pmdm.desarrollo.cuaderno.CuadernoApplication
import es.mjsplayground.pmdm.desarrollo.cuaderno.MainActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CuadernoTest {
    @get:Rule val regla = createAndroidComposeRule<MainActivity>()

    @Before fun vaciarNotasDePrueba() = runBlocking {
        val repository = (regla.activity.application as CuadernoApplication).contenedor.repository
        repository.observar().first().forEach { repository.eliminar(it) }
    }

    @Test fun formularioRotacionCrudYDialogo() {
        regla.onNodeWithText("Nueva nota").performClick()
        regla.onNodeWithText("Guardar").assertIsNotEnabled()
        regla.onNodeWithText("Título").performTextInput("ab")
        regla.onNodeWithText("Contenido", useUnmergedTree = false).performTextInput("Fruta y pan")
        regla.onNodeWithText("Guardar").assertIsNotEnabled()
        regla.onNodeWithText("Título").performTextReplacement("Compra de prueba")
        regla.activityRule.scenario.recreate()
        regla.onNodeWithText("Compra de prueba").assertIsDisplayed()
        regla.onNodeWithText("Guardar").performScrollTo().performClick()
        regla.onNodeWithText("Compra de prueba").assertIsDisplayed().performClick()
        regla.onNodeWithText("Título").performTextReplacement("Compra editada")
        regla.onNodeWithText("Guardar").performScrollTo().performClick()
        regla.onNodeWithText("Compra editada").assertIsDisplayed().performClick()
        regla.onNodeWithText("Eliminar").performScrollTo().performClick()
        regla.onNodeWithText("Cancelar").performClick()
        regla.onNodeWithText("Compra editada").assertIsDisplayed()
        regla.onNodeWithText("Eliminar").performScrollTo().performClick()
        regla.onNodeWithText("¿Eliminar esta nota?").assertIsDisplayed()
        regla.onAllNodesWithText("Eliminar").onLast().performClick()
        regla.onNodeWithText("Todavía no hay notas. Crea la primera.").assertIsDisplayed()
    }

    @Test fun rechazaHttpPublicoYMuestraReintento() {
        regla.onNodeWithText("Notas remotas").performClick()
        regla.onNodeWithText("URL completa del recurso").performTextReplacement("http://example.com/posts")
        regla.onNodeWithText("Cargar").performClick()
        regla.onNodeWithText("Usa una URL HTTPS completa. HTTP solo se admite en debug y con una IP privada o localhost.")
            .performScrollTo().assertIsDisplayed()
        regla.onNodeWithText("Reintentar").performScrollTo().assertIsDisplayed()
    }
}
