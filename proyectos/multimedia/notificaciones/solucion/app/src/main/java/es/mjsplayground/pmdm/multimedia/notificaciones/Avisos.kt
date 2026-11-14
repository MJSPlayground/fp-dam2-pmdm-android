package es.mjsplayground.pmdm.multimedia.notificaciones

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

data class DatosAviso(val id: Int, val titulo: Int, val texto: Int, val destino: String)

class Avisos(private val contexto: Context) {
    companion object {
        const val CANAL = "acciones"
        const val EXTRA_DETALLE = "detalle-aviso"
    }

    fun crearCanal() {
        val canal = NotificationChannel(CANAL, contexto.getString(R.string.canal), NotificationManager.IMPORTANCE_DEFAULT)
        contexto.getSystemService(NotificationManager::class.java).createNotificationChannel(canal)
    }

    // Ejemplo resuelto: cada dato vincula contenido de recursos y un destino conocido.
    fun demostracion() = DatosAviso(1, R.string.titulo_demo, R.string.texto_demo, "demo")

    fun notaGuardada(): DatosAviso? {
        return DatosAviso(2, R.string.titulo_nota, R.string.texto_nota, "nota")
    }

    fun publicar(datos: DatosAviso): Boolean {
        val gestor = NotificationManagerCompat.from(contexto)
        if (!gestor.areNotificationsEnabled()) return false
        if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(contexto,
                Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) return false
        val canal = contexto.getSystemService(NotificationManager::class.java).getNotificationChannel(CANAL)
        if (canal == null || canal.importance == NotificationManager.IMPORTANCE_NONE) return false
        val destino = Intent(contexto, MainActivity::class.java)
            .putExtra(EXTRA_DETALLE, datos.destino)
            .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val accion = PendingIntent.getActivity(contexto, datos.id, destino,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val aviso = NotificationCompat.Builder(contexto, CANAL)
            .setSmallIcon(R.drawable.ic_aviso)
            .setContentTitle(contexto.getString(datos.titulo))
            .setContentText(contexto.getString(datos.texto))
            .setContentIntent(accion)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .build()
        return try {
            gestor.notify(datos.id, aviso)
            true
        } catch (_: SecurityException) { false }
    }
}
