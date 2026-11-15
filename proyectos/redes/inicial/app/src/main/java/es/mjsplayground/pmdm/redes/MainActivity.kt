package es.mjsplayground.pmdm.redes

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.provider.Settings
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import es.mjsplayground.pmdm.redes.ui.MultimediaTheme
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var enlaces: Enlaces
    private lateinit var api: ApiMensajes
    private var estado by mutableStateOf("")
    private var estadoHttp by mutableStateOf("")
    private var cargando by mutableStateOf(false)
    private var mensajes by mutableStateOf(emptyList<Mensaje>())
    private var vinculados by mutableStateOf(emptyList<BluetoothDevice>())
    private var encontrados by mutableStateOf(emptyList<BluetoothDevice>())
    private var elegidoClasico: BluetoothDevice? = null
    private var elegidoBle: BluetoothDevice? = null
    private var pendiente = ""
    private var trabajo: Job? = null
    private val permisos = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        val accion = pendiente
        pendiente = ""
        if (necesarios(accion).all { autorizado(it) }) ejecutar(accion)
        else estado = getString(R.string.sin_permiso)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        pendiente = savedInstanceState?.getString("accion") ?: ""
        estado = getString(R.string.instruccion)
        api = ApiMensajes()
        enlaces = Enlaces(this, { estado = it }, { dispositivo ->
            if (encontrados.none { it == dispositivo } && encontrados.size < 10) encontrados = encontrados + dispositivo
        })
        setContent {
            MultimediaTheme {
                var direccion by rememberSaveable { mutableStateOf("http://10.0.2.2:8092/mensajes") }
                var soloPendientes by rememberSaveable { mutableStateOf(false) }
                Scaffold(Modifier.fillMaxSize()) { margen ->
                    Column(Modifier.padding(margen).padding(16.dp).verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(stringResource(R.string.app_name), style = MaterialTheme.typography.titleLarge)
                        Text(stringResource(R.string.http_titulo), style = MaterialTheme.typography.titleMedium)
                        OutlinedTextField(direccion, { direccion = it }, label = { Text(stringResource(R.string.direccion)) },
                            modifier = Modifier.fillMaxWidth(), singleLine = true)
                        Button(onClick = { cargar(direccion) }, enabled = !cargando) { Text(stringResource(R.string.cargar)) }
                        Button(onClick = { comprobarRed() }) { Text(stringResource(R.string.comprobar_red)) }
                        Row {
                            Checkbox(soloPendientes, { soloPendientes = it })
                            Text(stringResource(R.string.solo_pendientes))
                        }
                        if (cargando) CircularProgressIndicator()
                        if (estadoHttp.isNotEmpty()) Text(estadoHttp)
                        val visibles = if (soloPendientes) Bandeja.pendientes(mensajes) else mensajes
                        visibles.forEach { Text(stringResource(R.string.mensaje, it.id, it.texto)) }
                        HorizontalDivider()
                        Text(stringResource(R.string.cercanos), style = MaterialTheme.typography.titleMedium)
                        Text(stringResource(R.string.aviso_propios))
                        Text(estado)
                        Button(onClick = { startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS)) }) { Text(stringResource(R.string.ajustes)) }
                        Button(onClick = { solicitar("vinculados") }) { Text(stringResource(R.string.vinculados)) }
                        vinculados.forEachIndexed { indice, dispositivo ->
                            Button(onClick = { elegidoClasico = dispositivo; estado = getString(R.string.elegido, indice + 1) }) {
                                Text(enlaces.etiqueta(dispositivo, indice + 1))
                            }
                        }
                        Button(onClick = { solicitar("escuchar") }) { Text(stringResource(R.string.escuchar)) }
                        Button(onClick = { solicitar("hola") }) { Text(stringResource(R.string.enviar_hola)) }
                        Button(onClick = { solicitar("ping") }) { Text(stringResource(R.string.enviar_ping)) }
                        HorizontalDivider()
                        Text(stringResource(R.string.ble_titulo), style = MaterialTheme.typography.titleMedium)
                        Button(onClick = { solicitar("anunciar") }) { Text(stringResource(R.string.anunciar)) }
                        Button(onClick = { solicitar("explorar") }) { Text(stringResource(R.string.explorar)) }
                        encontrados.forEachIndexed { indice, dispositivo ->
                            Button(onClick = { elegidoBle = dispositivo; solicitar("conectar") }) {
                                Text(stringResource(R.string.conectar_ble, indice + 1))
                            }
                        }
                        Button(onClick = { solicitar("leer") }) { Text(stringResource(R.string.leer_ble)) }
                        Button(onClick = { solicitar("escribir") }) { Text(stringResource(R.string.escribir_ble)) }
                        Button(onClick = { enlaces.cerrar(); estado = getString(R.string.detenido) }) { Text(stringResource(R.string.detener)) }
                    }
                }
            }
        }
    }

    private fun autorizado(permiso: String) = ContextCompat.checkSelfPermission(this, permiso) == PackageManager.PERMISSION_GRANTED
    private fun necesarios(accion: String): Array<String> = when {
        Build.VERSION.SDK_INT >= 31 && accion == "explorar" -> arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT)
        Build.VERSION.SDK_INT >= 31 && accion == "anunciar" -> arrayOf(Manifest.permission.BLUETOOTH_ADVERTISE, Manifest.permission.BLUETOOTH_CONNECT)
        Build.VERSION.SDK_INT >= 31 -> arrayOf(Manifest.permission.BLUETOOTH_CONNECT)
        accion == "explorar" -> arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        else -> emptyArray()
    }

    private fun solicitar(accion: String) {
        if (necesarios(accion).all { autorizado(it) }) ejecutar(accion)
        else { pendiente = accion; permisos.launch(necesarios(accion)) }
    }

    private fun ejecutar(accion: String) {
        if (!enlaces.disponible()) { estado = getString(R.string.no_disponible); return }
        when (accion) {
            "vinculados" -> { vinculados = enlaces.vinculados(); estado = getString(R.string.cantidad, vinculados.size) }
            "escuchar" -> enlaces.escucharClasico()
            "hola", "ping" -> elegidoClasico?.let { enlaces.enviarClasico(it, if (accion == "hola") "HOLA" else "PING") }
                ?: run { estado = getString(R.string.elige_antes) }
            "anunciar" -> enlaces.anunciar()
            "explorar" -> { encontrados = emptyList(); enlaces.explorar() }
            "conectar" -> elegidoBle?.let { enlaces.conectarBle(it) } ?: run { estado = getString(R.string.elige_antes) }
            "leer" -> enlaces.leerBle()
            "escribir" -> enlaces.escribirBle("PING")
        }
    }

    private fun cargar(direccion: String) {
        cargando = true
        estadoHttp = getString(R.string.cargando)
        trabajo = lifecycleScope.launch {
            try {
                mensajes = api.cargar(direccion).mensajes
                estadoHttp = getString(if (mensajes.isEmpty()) R.string.vacio else R.string.carga_lista)
            } catch (cancelada: CancellationException) { throw cancelada }
              catch (_: ClientRequestException) { mensajes = emptyList(); estadoHttp = getString(R.string.error_cliente) }
              catch (_: ServerResponseException) { mensajes = emptyList(); estadoHttp = getString(R.string.error_servidor) }
              catch (_: Exception) { mensajes = emptyList(); estadoHttp = getString(R.string.error_http) }
            finally { cargando = false }
        }
    }

    private fun comprobarRed() {
        val gestor = getSystemService(ConnectivityManager::class.java)
        val capacidades = gestor.getNetworkCapabilities(gestor.activeNetwork)
        estadoHttp = getString(when {
            capacidades == null -> R.string.sin_red
            capacidades.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> R.string.red_wifi
            capacidades.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> R.string.red_movil
            else -> R.string.red_otra
        })
    }

    override fun onStop() {
        enlaces.cerrar()
        estado = getString(R.string.detenido)
        if (trabajo?.isActive == true) estadoHttp = getString(R.string.cancelado)
        trabajo?.cancel()
        cargando = false
        super.onStop()
    }
    override fun onSaveInstanceState(outState: Bundle) { outState.putString("accion", pendiente); super.onSaveInstanceState(outState) }
    override fun onDestroy() { enlaces.destruir(); api.close(); super.onDestroy() }
}
