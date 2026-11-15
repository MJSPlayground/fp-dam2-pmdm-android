package es.mjsplayground.pmdm.segundoplano.estudio

import android.app.NotificationManager
import android.content.ComponentName
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.lifecycle.Lifecycle
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.test.platform.app.InstrumentationRegistry
import java.util.concurrent.TimeUnit
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ReproduccionTest {
    @get:Rule val pantalla = createAndroidComposeRule<MainActivity>()

    @Test fun sesionContinuaConActivityDetenidaYSePuedeParar() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val contexto = instrumentation.targetContext
        val futuro = MediaController.Builder(contexto,
            SessionToken(contexto, ComponentName(contexto, ReproductorService::class.java))).buildAsync()
        val controlador = futuro.get(10, TimeUnit.SECONDS)
        try {
            pantalla.waitUntil(10_000) {
                try { pantalla.onNodeWithText(contexto.getString(R.string.play)).assertIsEnabled(); true }
                catch (_: AssertionError) { false }
            }
            pantalla.onNodeWithText(contexto.getString(R.string.play)).performScrollTo().performClick()
            pantalla.waitUntil(10_000) {
                var activo = false
                instrumentation.runOnMainSync { activo = controlador.isPlaying }
                activo
            }
            pantalla.activityRule.scenario.moveToState(Lifecycle.State.CREATED)
            instrumentation.runOnMainSync { assertTrue(controlador.isPlaying) }
            val manager = contexto.getSystemService(NotificationManager::class.java)
            assertTrue(manager.activeNotifications.any { it.id != Avisos.ID })
        } finally {
            instrumentation.runOnMainSync { controlador.stop(); controlador.release() }
        }
    }
}
