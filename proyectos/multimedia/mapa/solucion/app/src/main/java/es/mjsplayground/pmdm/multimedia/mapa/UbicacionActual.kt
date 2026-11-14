package es.mjsplayground.pmdm.multimedia.mapa

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import androidx.core.os.CancellationSignal

/** Lectura puntual: no inicia seguimiento ni conserva coordenadas. */
class UbicacionActual(private val contexto: Context) {
    private val gestor = contexto.getSystemService(LocationManager::class.java)
    private var cancelacion: CancellationSignal? = null

    fun obtener(resultado: (Location?) -> Unit, fallo: () -> Unit) {
        cancelar()
        val precisa = ContextCompat.checkSelfPermission(contexto, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val aproximada = ContextCompat.checkSelfPermission(contexto, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (!aproximada && !precisa) { fallo(); return }
        val candidatos = if (precisa) listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
            else listOf(LocationManager.NETWORK_PROVIDER)
        try {
            val proveedor = candidatos.firstOrNull { gestor.isProviderEnabled(it) }
            if (proveedor == null) { fallo(); return }
            val senal = CancellationSignal()
            cancelacion = senal
            LocationManagerCompat.getCurrentLocation(gestor, proveedor, senal,
                ContextCompat.getMainExecutor(contexto)) { posicion ->
                if (cancelacion === senal && !senal.isCanceled) {
                    cancelacion = null
                    resultado(posicion)
                }
            }
        } catch (_: SecurityException) { cancelar(); fallo() }
          catch (_: IllegalArgumentException) { cancelar(); fallo() }
    }

    fun cancelar() { cancelacion?.cancel(); cancelacion = null }
}
