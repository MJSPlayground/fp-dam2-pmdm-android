package es.mjsplayground.pmdm.segundoplano.estudio

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

object Avisos {
    const val CANAL = "avisos_estudio"
    const val ID = 42

    fun canal(contexto: Context) {
        contexto.getSystemService(NotificationManager::class.java).createNotificationChannel(
            NotificationChannel(CANAL, contexto.getString(R.string.canal), NotificationManager.IMPORTANCE_DEFAULT)
        )
    }

    fun permitido(contexto: Context): Boolean =
        (Build.VERSION.SDK_INT < 33 || ContextCompat.checkSelfPermission(contexto, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) &&
            NotificationManagerCompat.from(contexto).areNotificationsEnabled() &&
            contexto.getSystemService(NotificationManager::class.java).getNotificationChannel(CANAL)?.importance != NotificationManager.IMPORTANCE_NONE

    fun mostrar(contexto: Context, resumen: Boolean = false): Boolean {
        canal(contexto)
        if (!permitido(contexto)) return false
        val abrir = PendingIntent.getActivity(contexto, 0,
            Intent(contexto, MainActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val aviso = NotificationCompat.Builder(contexto, CANAL)
            .setSmallIcon(R.drawable.ic_notificacion)
            .setContentTitle(contexto.getString(R.string.titulo_aviso))
            .setContentText(contexto.getString(R.string.texto_aviso))
            .setContentIntent(abrir).setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        // TODO aviso-estilo: aplica BigTextStyle al aviso cuando resumen sea verdadero.
        // La autorización puede cambiar entre la comprobación y la llamada.
        return try {
            NotificationManagerCompat.from(contexto).notify(ID, aviso.build())
            true
        } catch (_: SecurityException) { false }
    }
}
