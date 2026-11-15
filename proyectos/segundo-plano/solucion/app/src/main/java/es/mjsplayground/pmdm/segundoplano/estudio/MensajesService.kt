package es.mjsplayground.pmdm.segundoplano.estudio

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/** Adaptador compilable; no se habilita FCM ni se inventa configuración de proyecto. */
class MensajesService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        // Pendiente de una integración real: registrar con backend autenticado por HTTPS.
        // Se conserva solo en almacenamiento privado, sin publicarlo en Logcat.
        getSharedPreferences("mensajeria", MODE_PRIVATE).edit().putString("token_pendiente", token).apply()
    }

    override fun onMessageReceived(message: RemoteMessage) {
        // El contenido remoto no se ejecuta como código ni se muestra sin un contrato.
        if (message.data["tipo"] == "revision") programar(applicationContext)
    }
}
