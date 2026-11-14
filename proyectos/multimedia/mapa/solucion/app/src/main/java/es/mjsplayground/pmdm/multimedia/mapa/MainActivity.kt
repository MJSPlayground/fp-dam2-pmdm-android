package es.mjsplayground.pmdm.multimedia.mapa

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import es.mjsplayground.pmdm.multimedia.mapa.ui.MultimediaTheme
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.CircleLayer
import org.maplibre.android.style.layers.PropertyFactory.*
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.Point

class MainActivity : ComponentActivity() {
    private lateinit var vista: MapView
    private lateinit var ubicacion: UbicacionActual
    private var mapa: MapLibreMap? = null
    private var estilo: Style? = null
    private var estadoMapa by mutableStateOf(R.string.cargando)
    private var estadoUbicacion by mutableStateOf(R.string.sin_solicitud)
    private var coordenadas by mutableStateOf("")
    private var leyendo by mutableStateOf(false)
    private val permisos = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        if (permiso(Manifest.permission.ACCESS_COARSE_LOCATION)) leerPosicion()
        else estadoUbicacion = R.string.permiso_denegado
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        MapLibre.getInstance(this)
        vista = MapView(this)
        vista.onCreate(savedInstanceState)
        ubicacion = UbicacionActual(this)
        vista.addOnDidFailLoadingMapListener { estadoMapa = R.string.error_mapa }
        cargarMapa()
        setContent {
            MultimediaTheme {
                Scaffold(Modifier.fillMaxSize()) { margen ->
                    Column(Modifier.padding(margen).padding(16.dp).verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(stringResource(R.string.app_name))
                        Text(stringResource(estadoMapa))
                        AndroidView(factory = { vista }, modifier = Modifier.fillMaxWidth().height(320.dp))
                        Text(stringResource(R.string.atribucion))
                        Button(onClick = { cargarMapa() }) { Text(stringResource(R.string.recargar)) }
                        Button(onClick = { centrarLugar() }, enabled = estilo != null) {
                            Text(stringResource(R.string.centrar))
                        }
                        Text(stringResource(R.string.explicacion_permiso))
                        Button(onClick = { solicitarUbicacion() }, enabled = !leyendo && estilo != null) {
                            Text(stringResource(R.string.obtener))
                        }
                        Text(stringResource(estadoUbicacion))
                        if (coordenadas.isNotEmpty()) Text(coordenadas)
                    }
                }
            }
        }
    }

    private fun cargarMapa() {
        estadoMapa = R.string.cargando
        estilo = null
        vista.getMapAsync { preparado ->
            if (isDestroyed) return@getMapAsync
            mapa = preparado
            preparado.setStyle(Style.Builder().fromUri("https://tiles.openfreemap.org/styles/liberty")) { nuevo ->
                if (isDestroyed) return@setStyle
                estilo = nuevo
                // Ejemplo resuelto mapa-referencia: datos GeoJSON y apariencia son piezas distintas.
                nuevo.addSource(GeoJsonSource("lugar", Point.fromLngLat(-3.7038, 40.4168)))
                nuevo.addLayer(CircleLayer("lugar-circulo", "lugar").withProperties(
                    circleRadius(10f), circleColor("#1B5E20"), circleStrokeColor("#FFFFFF"), circleStrokeWidth(3f)))
                estadoMapa = R.string.mapa_listo
                centrarLugar()
            }
        }
    }

    private fun centrarLugar() {
        mapa?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(40.4168, -3.7038), 13.0))
    }

    private fun permiso(nombre: String) =
        ContextCompat.checkSelfPermission(this, nombre) == PackageManager.PERMISSION_GRANTED

    private fun solicitarUbicacion() {
        if (permiso(Manifest.permission.ACCESS_COARSE_LOCATION)) leerPosicion()
        else permisos.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION))
    }

    private fun leerPosicion() {
        leyendo = true
        estadoUbicacion = R.string.buscando
        ubicacion.obtener(resultado = { posicion ->
            leyendo = false
            if (posicion == null) estadoUbicacion = R.string.sin_posicion
            else {
                estadoUbicacion = R.string.posicion_lista
                coordenadas = getString(R.string.coordenadas, posicion.latitude, posicion.longitude, posicion.accuracy)
                representarPosicion(posicion.longitude, posicion.latitude)
            }
        }, fallo = {
            leyendo = false
            estadoUbicacion = R.string.sin_proveedor
        })
    }

    private fun representarPosicion(longitud: Double, latitud: Double) {
        val actual = estilo ?: return
        val punto = Point.fromLngLat(longitud, latitud)
        val fuente = actual.getSourceAs<GeoJsonSource>("posicion")
        if (fuente == null) {
            actual.addSource(GeoJsonSource("posicion", punto))
            actual.addLayer(CircleLayer("posicion-circulo", "posicion").withProperties(
                circleRadius(6f), circleColor("#1565C0"), circleStrokeColor("#FFFFFF"), circleStrokeWidth(2f)))
        } else fuente.setGeoJson(punto)
        mapa?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitud, longitud), 15.0))
    }

    override fun onStart() { super.onStart(); vista.onStart() }
    override fun onResume() { super.onResume(); vista.onResume() }
    override fun onPause() { vista.onPause(); super.onPause() }
    override fun onStop() {
        ubicacion.cancelar()
        if (leyendo) estadoUbicacion = R.string.cancelada
        leyendo = false
        vista.onStop()
        super.onStop()
    }
    override fun onSaveInstanceState(outState: Bundle) {
        vista.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }
    override fun onLowMemory() { super.onLowMemory(); vista.onLowMemory() }
    override fun onDestroy() { ubicacion.cancelar(); vista.onDestroy(); super.onDestroy() }
}
