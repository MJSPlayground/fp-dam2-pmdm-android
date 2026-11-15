package es.mjsplayground.pmdm.cartografia

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import androidx.core.content.ContextCompat

/** Actualizaciones únicamente mientras la Activity permanece visible. */
class Seguimiento(private val contexto: Context) {
    private val gestor = contexto.getSystemService(LocationManager::class.java)
    private var oyente: LocationListener? = null

    fun iniciar(resultado: (Location) -> Unit, fallo: () -> Unit): Boolean {
        detener()
        val precisa = ContextCompat.checkSelfPermission(contexto, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val aproximada = ContextCompat.checkSelfPermission(contexto, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (!precisa && !aproximada) { fallo(); return false }
        val candidatos = if (precisa) listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
            else listOf(LocationManager.NETWORK_PROVIDER)
        try {
            val proveedor = candidatos.firstOrNull { gestor.isProviderEnabled(it) }
            if (proveedor == null) { fallo(); return false }
            val receptor = object : LocationListener {
                override fun onLocationChanged(location: Location) { resultado(location) }
                override fun onProviderDisabled(provider: String) { detener(); fallo() }
                override fun onProviderEnabled(provider: String) = Unit
                @Deprecated("Callback heredado requerido para versiones antiguas")
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) = Unit
            }
            oyente = receptor
            gestor.requestLocationUpdates(proveedor, 5000L, 5f, receptor, Looper.getMainLooper())
            return true
        } catch (_: SecurityException) { detener(); fallo(); return false }
          catch (_: IllegalArgumentException) { detener(); fallo(); return false }
    }

    fun detener() { oyente?.let { gestor.removeUpdates(it) }; oyente = null }
}
