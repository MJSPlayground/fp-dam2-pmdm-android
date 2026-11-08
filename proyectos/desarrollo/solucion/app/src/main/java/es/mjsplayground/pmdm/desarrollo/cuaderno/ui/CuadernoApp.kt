package es.mjsplayground.pmdm.desarrollo.cuaderno.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import es.mjsplayground.pmdm.desarrollo.cuaderno.R
import es.mjsplayground.pmdm.desarrollo.cuaderno.data.NotaRepository
import es.mjsplayground.pmdm.desarrollo.cuaderno.data.PublicacionDto
import es.mjsplayground.pmdm.desarrollo.cuaderno.data.URL_INICIAL
import es.mjsplayground.pmdm.desarrollo.cuaderno.model.Nota
import es.mjsplayground.pmdm.desarrollo.cuaderno.model.esNotaValida
import es.mjsplayground.pmdm.desarrollo.cuaderno.navigation.Detalle
import es.mjsplayground.pmdm.desarrollo.cuaderno.navigation.Lista
import es.mjsplayground.pmdm.desarrollo.cuaderno.navigation.Nueva
import es.mjsplayground.pmdm.desarrollo.cuaderno.navigation.Remotos

@Composable
fun CuadernoApp(repository: NotaRepository) {
    // --8<-- [start:vm-principal]
    val vm: CuadernoViewModel = viewModel(factory = viewModelFactory {
        initializer { CuadernoViewModel(repository) }
    })
    val estado by vm.estado.collectAsStateWithLifecycle()
    // --8<-- [end:vm-principal]
    val nav = rememberNavController()
    Scaffold(modifier = Modifier.fillMaxSize().imePadding()) { margen ->
        NavHost(nav, startDestination = Lista, modifier = Modifier.padding(margen)) {
            composable<Lista> {
                ListaScreen(estado, { nav.navigate(Nueva) }, { nav.navigate(Remotos) },
                    { nav.navigate(Detalle(it)) })
            }
            composable<Nueva> {
                FormularioScreen(
                    nota = Nota(titulo = "", contenido = ""), estado = estado,
                    onVolver = { nav.popBackStack() },
                    onGuardar = { nota -> vm.guardar(nota) { nav.popBackStack() } }
                )
            }
            composable<Detalle> {
                // --8<-- [start:vm-detalle]
                val detalleVm: DetalleViewModel = viewModel(factory = viewModelFactory {
                    initializer { DetalleViewModel(repository, createSavedStateHandle()) }
                })
                val detalle by detalleVm.estado.collectAsStateWithLifecycle()
                // --8<-- [end:vm-detalle]
                when {
                    detalle.cargando -> CircularProgressIndicator(Modifier.padding(24.dp))
                    detalle.fallo -> Column(Modifier.padding(16.dp)) {
                        MensajeFallo(Fallo.Datos)
                        Volver { nav.popBackStack() }
                    }
                    detalle.nota == null -> Column(Modifier.padding(16.dp)) {
                        Text(stringResource(R.string.nota_ausente))
                        Volver { nav.popBackStack() }
                    }
                    else -> {
                        val nota = detalle.nota!!
                        FormularioScreen(nota, estado, { nav.popBackStack() },
                            { modificada -> vm.guardar(modificada) { nav.popBackStack() } },
                            { vm.eliminar(nota) { nav.popBackStack() } })
                    }
                }
            }
            composable<Remotos> {
                RemotosScreen(estado, { nav.popBackStack() }, vm::cargarRemotas, vm::importar)
            }
        }
    }
}

@Composable
private fun ListaScreen(
    estado: CuadernoUiState, onNueva: () -> Unit,
    onRemotos: () -> Unit, onDetalle: (Long) -> Unit
) {
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineMedium) }
        item { Button(onClick = onNueva) { Text(stringResource(R.string.nueva)) } }
        item { TextButton(onClick = onRemotos) { Text(stringResource(R.string.remotos)) } }
        if (estado.cargandoLocal) item { CircularProgressIndicator() }
        estado.errorLocal?.let { fallo -> item { MensajeFallo(fallo) } }
        if (!estado.cargandoLocal && estado.notas.isEmpty()) item { Text(stringResource(R.string.vacia)) }
        items(estado.notas, key = { it.id }) { nota ->
            Card(onClick = { onDetalle(nota.id) }, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(nota.titulo, style = MaterialTheme.typography.titleMedium)
                    Text(nota.contenido, maxLines = 3)
                }
            }
        }
    }
}

@Composable
private fun Volver(onVolver: () -> Unit) {
    TextButton(onClick = onVolver) { Text(stringResource(R.string.volver)) }
}

@Composable
private fun FormularioScreen(
    nota: Nota, estado: CuadernoUiState, onVolver: () -> Unit,
    onGuardar: (Nota) -> Unit, onEliminar: (() -> Unit)? = null
) {
    var titulo by rememberSaveable(nota.id) { mutableStateOf(nota.titulo) }
    var contenido by rememberSaveable(nota.id) { mutableStateOf(nota.contenido) }
    var confirmar by rememberSaveable { mutableStateOf(false) }
    var sinApp by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val tituloCompartir = stringResource(R.string.elegir_app)
    Column(
        Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Volver(onVolver)
        OutlinedTextField(value = titulo, onValueChange = { titulo = it }, enabled = !estado.guardando,
            label = { Text(stringResource(R.string.titulo)) }, modifier = Modifier.fillMaxWidth(),
            singleLine = true, isError = titulo.isNotEmpty() && titulo.trim().length !in 3..80)
        OutlinedTextField(value = contenido, onValueChange = { contenido = it }, enabled = !estado.guardando,
            label = { Text(stringResource(R.string.contenido)) }, modifier = Modifier.fillMaxWidth(), minLines = 4)
        Text(stringResource(R.string.validacion), style = MaterialTheme.typography.bodySmall)
        estado.errorLocal?.let { MensajeFallo(it) }
        Button(enabled = !estado.guardando && esNotaValida(titulo, contenido),
            onClick = { onGuardar(nota.copy(titulo = titulo.trim(), contenido = contenido.trim())) }) {
            Text(stringResource(if (estado.guardando) R.string.guardando else R.string.guardar))
        }
        if (onEliminar != null) {
            TextButton(onClick = { confirmar = true }, enabled = !estado.guardando) {
                Text(stringResource(R.string.eliminar))
            }
            TextButton(enabled = esNotaValida(titulo, contenido), onClick = {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, "${titulo.trim()}\n\n${contenido.trim()}")
                }
                try {
                    context.startActivity(Intent.createChooser(intent, tituloCompartir))
                } catch (_: ActivityNotFoundException) { sinApp = true }
            }) { Text(stringResource(R.string.compartir)) }
        }
        if (sinApp) Text(stringResource(R.string.sin_app))
    }
    if (confirmar && onEliminar != null) {
        AlertDialog(
            onDismissRequest = { confirmar = false },
            title = { Text(stringResource(R.string.confirmar_eliminar)) },
            text = { Text(stringResource(R.string.eliminacion_aviso)) },
            confirmButton = {
                TextButton(onClick = { confirmar = false; onEliminar() }) { Text(stringResource(R.string.eliminar)) }
            },
            dismissButton = {
                TextButton(onClick = { confirmar = false }) { Text(stringResource(R.string.cancelar)) }
            }
        )
    }
}

@Composable
private fun RemotosScreen(
    estado: CuadernoUiState, onVolver: () -> Unit,
    onCargar: (String) -> Unit, onImportar: (PublicacionDto) -> Unit
) {
    var url by rememberSaveable { mutableStateOf(URL_INICIAL) }
    var wifi by rememberSaveable { mutableStateOf<Boolean?>(null) }
    val context = LocalContext.current
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Volver(onVolver) }
        item { Text(stringResource(R.string.remotos), style = MaterialTheme.typography.headlineMedium) }
        item { Text(stringResource(R.string.introduccion_remotas)) }
        item {
            TextButton(onClick = {
                val manager = context.getSystemService(ConnectivityManager::class.java)
                wifi = manager.getNetworkCapabilities(manager.activeNetwork)
                    ?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
            }) { Text(stringResource(R.string.comprobar_wifi)) }
            wifi?.let {
                Text(stringResource(if (it) R.string.wifi_activa else R.string.wifi_ausente))
            }
        }
        item {
            OutlinedTextField(value = url, onValueChange = { url = it },
                label = { Text(stringResource(R.string.url)) }, modifier = Modifier.fillMaxWidth())
        }
        item {
            Button(onClick = { onCargar(url) }, enabled = !estado.cargandoRemotas) {
                Text(stringResource(R.string.cargar))
            }
        }
        if (estado.cargandoRemotas) item {
            Column {
                CircularProgressIndicator()
                Text(stringResource(R.string.cargando))
            }
        }
        estado.errorRemoto?.let { fallo -> item {
            MensajeFallo(fallo)
            Button(onClick = { onCargar(url) }, enabled = !estado.cargandoRemotas) {
                Text(stringResource(R.string.reintentar))
            }
        } }
        if (estado.importada) item { Text(stringResource(R.string.importada)) }
        if (estado.remotoConsultado && !estado.cargandoRemotas &&
            estado.errorRemoto == null && estado.remotas.isEmpty()) item {
            Text(stringResource(R.string.sin_remotas))
        }
        items(estado.remotas, key = { it.id }) { remota ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(remota.title, style = MaterialTheme.typography.titleMedium)
                    Text(remota.body)
                    Button(onClick = { onImportar(remota) }, enabled = !estado.guardando) {
                        Text(stringResource(R.string.importar))
                    }
                }
            }
        }
    }
}

@Composable
private fun MensajeFallo(fallo: Fallo) {
    val mensaje = when (fallo) {
        Fallo.Conexion -> stringResource(R.string.error_red)
        Fallo.Formato -> stringResource(R.string.error_json)
        Fallo.Url -> stringResource(R.string.error_url)
        Fallo.Datos -> stringResource(R.string.error_datos)
        Fallo.Validacion -> stringResource(R.string.validacion)
        is Fallo.Http -> stringResource(R.string.error_http, fallo.codigo)
    }
    Text(mensaje, color = MaterialTheme.colorScheme.error)
}
