package es.mjsplayground.pmdm.multimedia.notificaciones

import android.app.NotificationManager
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.performClick
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test

/** Para API >=33, concede POST_NOTIFICATIONS antes de ejecutar esta prueba.
 * No certifica la presentación visual de la bandeja ni sonido del dispositivo. */
class AvisosTest {
    @get:Rule val pantalla = createAndroidComposeRule<MainActivity>()
    private val gestor get() = pantalla.activity.getSystemService(NotificationManager::class.java)

    @After fun limpiar() { gestor.cancelAll() }

    @Test fun canalYDosAvisosIndependientes() {
        assertNotNull(gestor.getNotificationChannel(Avisos.CANAL))
        pantalla.onNodeWithText("Mostrar aviso de ejemplo").performClick()
        pantalla.waitUntil(5000) { gestor.activeNotifications.any { it.id == 1 } }
        pantalla.onNodeWithText("Avisar nota guardada").performClick()
        pantalla.waitUntil(5000) { gestor.activeNotifications.map { it.id }.containsAll(listOf(1, 2)) }
        pantalla.onNodeWithText("Avisar nota guardada").performClick()
        pantalla.waitForIdle()
        // Android puede añadir un resumen automático; contamos solo los dos IDs de la app.
        assertEquals(2, gestor.activeNotifications.count { it.id == 1 || it.id == 2 })
        assertNotNull(gestor.activeNotifications.first { it.id == 1 }.notification.contentIntent)
        assertNotNull(gestor.activeNotifications.first { it.id == 2 }.notification.contentIntent)
    }
}
