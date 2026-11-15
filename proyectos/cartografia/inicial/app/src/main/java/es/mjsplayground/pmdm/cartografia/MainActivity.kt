package es.mjsplayground.pmdm.cartografia

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import es.mjsplayground.pmdm.cartografia.ui.MultimediaTheme
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.*
import org.maplibre.android.style.layers.PropertyFactory.*
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.*
import kotlin.math.*

class MainActivity : ComponentActivity() {
    private lateinit var vista: MapView
    private lateinit var seguimiento: Seguimiento
    private var mapa: MapLibreMap? = null
    private var estilo: Style? = null
    private var local by mutableStateOf(true)
    private var cartografia by mutableStateOf(R.string.cargando)
    private var activo by mutableStateOf(false)
    private var estado by mutableStateOf("")
    private var red by mutableStateOf("")
    private var ocupado by mutableStateOf(false)
    private var trabajo: Job? = null
    private var posicion: Punto? = null
    private var busqueda: Punto? = null
    private var puntosRuta = emptyList<Punto>()
    private val servicio = ServicioCartografico()
    private val permisos = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        if (autorizada()) comenzar()
        else estado = getString(R.string.denegado)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        MapLibre.getInstance(this)
        vista = MapView(this)
        vista.onCreate(savedInstanceState)
        seguimiento = Seguimiento(this)
        estado = getString(R.string.sin_lectura)
        vista.addOnDidFailLoadingMapListener { cartografia = R.string.error_mapa }
        cargarMapa()
        setContent {
            MultimediaTheme {
                var base by rememberSaveable { mutableStateOf("http://10.0.2.2:8091") }
                var consulta by rememberSaveable { mutableStateOf("Museo") }
                var consentimiento by rememberSaveable { mutableStateOf(false) }
                Scaffold(Modifier.fillMaxSize()) { margen ->
                    Column(Modifier.padding(margen).padding(16.dp).verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(stringResource(R.string.app_name), style = MaterialTheme.typography.titleLarge)
                        Text(stringResource(cartografia))
                        AndroidView(factory = { vista }, modifier = Modifier.fillMaxWidth().height(300.dp))
                        Text(stringResource(if (local) R.string.credito_local else R.string.credito_remoto))
                        Button(onClick = { local = !local; cargarMapa() }) {
                            Text(stringResource(if (local) R.string.usar_remoto else R.string.usar_local))
                        }
                        Button(onClick = { encuadrar() }, enabled = estilo != null) { Text(stringResource(R.string.encuadrar)) }
                        Text(stringResource(R.string.explicacion))
                        Button(onClick = {
                            if (activo) detener() else if (autorizada()) comenzar()
                            else permisos.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION))
                        }) { Text(stringResource(if (activo) R.string.detener else R.string.iniciar)) }
                        Button(onClick = { recibir(Geometria.centro, 5.0) }) { Text(stringResource(R.string.simular_dentro)) }
                        Button(onClick = { recibir(Punto(-3.69, 40.42), 5.0) }) { Text(stringResource(R.string.simular_fuera)) }
                        Text(estado)
                        HorizontalDivider()
                        Text(stringResource(R.string.servicios))
                        OutlinedTextField(base, { base = it }, label = { Text(stringResource(R.string.base)) }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                        OutlinedTextField(consulta, { consulta = it }, label = { Text(stringResource(R.string.consulta)) }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                        Row {
                            Checkbox(consentimiento, { consentimiento = it })
                            Text(stringResource(R.string.consentimiento), modifier = Modifier.weight(1f))
                        }
                        Button(onClick = { consultar(base, consulta, false) }, enabled = consentimiento && !ocupado && consulta.isNotBlank()) {
                            Text(stringResource(R.string.buscar))
                        }
                        Button(onClick = { consultar(base, consulta, true) }, enabled = consentimiento && !ocupado) {
                            Text(stringResource(R.string.ruta))
                        }
                        if (ocupado) CircularProgressIndicator()
                        if (red.isNotEmpty()) Text(red)
                    }
                }
            }
        }
    }

    private fun cargarMapa() {
        cartografia = R.string.cargando
        estilo = null
        vista.getMapAsync { preparado ->
            if (isDestroyed) return@getMapAsync
            mapa = preparado
            val configuracion = if (local) Style.Builder().fromJson("""{"version":8,"name":"Esquema local","sources":{},"layers":[{"id":"fondo","type":"background","paint":{"background-color":"#E8F5E9"}}]}""")
                else Style.Builder().fromUri("https://tiles.openfreemap.org/styles/liberty")
            preparado.setStyle(configuracion) { nuevo ->
                if (isDestroyed) return@setStyle
                estilo = nuevo
                // Ejemplo resuelto: polígono GeoJSON propio que aproxima una zona circular.
                val centro = Geometria.centro
                val anillo = (0..72).map { paso ->
                    val angulo = 2 * PI * paso / 72
                    Point.fromLngLat(centro.longitud + cos(angulo) * Geometria.radioMetros /
                        (111320 * cos(Math.toRadians(centro.latitud))),
                        centro.latitud + sin(angulo) * Geometria.radioMetros / 111320)
                }
                nuevo.addSource(GeoJsonSource("zona", Polygon.fromLngLats(listOf(anillo))))
                nuevo.addLayer(FillLayer("zona-relleno", "zona").withProperties(fillColor("#2E7D32"), fillOpacity(0.20f)))
                nuevo.addLayer(LineLayer("zona-borde", "zona").withProperties(lineColor("#1B5E20"), lineWidth(2f)))
                punto("centro", centro, "#1B5E20", 9f)
                posicion?.let { punto("posicion", it, "#1565C0", 6f) }
                busqueda?.let { punto("busqueda", it, "#9C4500", 7f) }
                dibujarRuta()
                cartografia = if (local) R.string.local_listo else R.string.remoto_listo
                encuadrar()
            }
        }
    }

    private fun punto(id: String, punto: Punto, color: String, radio: Float) {
        val actual = estilo ?: return
        val dato = Point.fromLngLat(punto.longitud, punto.latitud)
        val fuente = actual.getSourceAs<GeoJsonSource>(id)
        if (fuente == null) {
            actual.addSource(GeoJsonSource(id, dato))
            actual.addLayer(CircleLayer("$id-circulo", id).withProperties(circleColor(color), circleRadius(radio),
                circleStrokeColor("#FFFFFF"), circleStrokeWidth(2f)))
        } else fuente.setGeoJson(dato)
    }

    private fun encuadrar() {
        val centro = Geometria.centro
        val limites = LatLngBounds.Builder()
            .include(LatLng(centro.latitud - 0.007, centro.longitud - 0.009))
            .include(LatLng(centro.latitud + 0.007, centro.longitud + 0.009))
            .apply { puntosRuta.forEach { include(LatLng(it.latitud, it.longitud)) } }.build()
        mapa?.animateCamera(CameraUpdateFactory.newLatLngBounds(limites, 40))
    }

    private fun autorizada() = ContextCompat.checkSelfPermission(this,
        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    private fun comenzar() {
        activo = seguimiento.iniciar(resultado = { recibir(Punto(it.longitude, it.latitude), it.accuracy.toDouble()) },
            fallo = { activo = false; estado = getString(R.string.sin_proveedor) })
        if (activo) estado = getString(R.string.siguiendo)
    }

    private fun detener() { seguimiento.detener(); activo = false; estado = getString(R.string.detenido) }

    private fun recibir(puntoRecibido: Punto, precision: Double) {
        // TODO cartografia-posicion: actualiza marcador y presenta distancia, precisión y clasificación.
        estado = getString(R.string.pendiente)
    }

    private fun consultar(base: String, consulta: String, esRuta: Boolean) {
        ocupado = true
        red = getString(R.string.consultando)
        trabajo = lifecycleScope.launch {
            try {
                if (esRuta) {
                    puntosRuta = servicio.ruta(base)
                    dibujarRuta()
                    encuadrar()
                    red = getString(R.string.ruta_lista)
                } else {
                    // TODO cartografia-busqueda: adapta el ejemplo de ruta para buscar, tratar vacío y representar el punto.
                    red = getString(R.string.pendiente)
                }
            } catch (cancelada: CancellationException) { throw cancelada }
              catch (_: Exception) { red = getString(R.string.error_servicio) }
            finally { ocupado = false }
        }
    }

    private fun dibujarRuta() {
        val actual = estilo ?: return
        if (puntosRuta.size < 2) return
        val datos = LineString.fromLngLats(puntosRuta.map { Point.fromLngLat(it.longitud, it.latitud) })
        val fuente = actual.getSourceAs<GeoJsonSource>("ruta")
        if (fuente == null) {
            actual.addSource(GeoJsonSource("ruta", datos))
            actual.addLayer(LineLayer("ruta-trazo", "ruta").withProperties(lineColor("#9C4500"), lineWidth(5f)))
        } else fuente.setGeoJson(datos)
    }

    override fun onStart() { super.onStart(); vista.onStart() }
    override fun onResume() { super.onResume(); vista.onResume() }
    override fun onPause() { vista.onPause(); super.onPause() }
    override fun onStop() {
        detener()
        if (trabajo?.isActive == true) red = getString(R.string.consulta_cancelada)
        trabajo?.cancel()
        ocupado = false
        vista.onStop()
        super.onStop()
    }
    override fun onSaveInstanceState(outState: Bundle) { vista.onSaveInstanceState(outState); super.onSaveInstanceState(outState) }
    override fun onLowMemory() { super.onLowMemory(); vista.onLowMemory() }
    override fun onDestroy() { seguimiento.detener(); vista.onDestroy(); super.onDestroy() }
}
