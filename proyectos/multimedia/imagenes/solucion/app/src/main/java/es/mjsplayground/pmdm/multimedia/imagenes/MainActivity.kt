package es.mjsplayground.pmdm.multimedia.imagenes

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import es.mjsplayground.pmdm.multimedia.imagenes.ui.MultimediaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { MultimediaTheme { EditorImagen() } }
    }
}

@Composable
private fun EditorImagen() {
    val context = LocalContext.current
    val vm: ImagenViewModel = viewModel(factory = viewModelFactory {
        initializer { ImagenViewModel(ProcesadorImagen(context.applicationContext)) }
    })
    val estado by vm.estado.collectAsStateWithLifecycle()
    var pendiente by rememberSaveable { mutableStateOf<String?>(null) }
    val selector = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri == null) vm.mensaje(R.string.cancelada) else vm.cargar(uri)
    }
    val camara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { guardada ->
        val uri = pendiente
        pendiente = null
        if (guardada && uri != null) vm.cargar(Uri.parse(uri)) else vm.mensaje(R.string.cancelada)
    }
    val tituloCompartir = stringResource(R.string.compartir)
    Scaffold(modifier = Modifier.fillMaxSize()) { margen ->
        Column(Modifier.padding(margen).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(stringResource(R.string.app_name))
            Button(enabled = !estado.cargando, onClick = {
                selector.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }) { Text(stringResource(R.string.seleccionar)) }
            Button(enabled = !estado.cargando, onClick = {
                vm.prepararCaptura { uri ->
                    pendiente = uri.toString()
                    try { camara.launch(uri)
                    } catch (_: ActivityNotFoundException) { vm.mensaje(R.string.sin_camara) }
                }
            }) { Text(stringResource(R.string.capturar)) }
            if (estado.cargando) CircularProgressIndicator()
            estado.bitmap?.let { bitmap ->
                Image(bitmap.asImageBitmap(), contentDescription = stringResource(R.string.imagen_preview),
                    modifier = Modifier.fillMaxWidth().heightIn(max = 320.dp), contentScale = ContentScale.Fit)
                Text(stringResource(R.string.dimensiones, bitmap.width, bitmap.height))
                Button(enabled = !estado.cargando, onClick = vm::rotar) { Text(stringResource(R.string.rotar)) }
                Button(enabled = !estado.cargando, onClick = { vm.exportar(Formato.PNG) }) { Text(stringResource(R.string.png)) }
                Button(enabled = !estado.cargando, onClick = { vm.exportar(Formato.JPEG) }) { Text(stringResource(R.string.jpeg)) }
            }
            estado.resultado?.let { resultado ->
                Text(stringResource(R.string.resultado, resultado.nombre, resultado.mime, resultado.bytes))
                Button(onClick = {
                    // --8<-- [start:imagen-compartir]
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = resultado.mime
                        putExtra(Intent.EXTRA_STREAM, resultado.uri)
                        clipData = ClipData.newUri(context.contentResolver, resultado.nombre, resultado.uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    try { context.startActivity(Intent.createChooser(intent, tituloCompartir))
                    } catch (_: ActivityNotFoundException) { vm.mensaje(R.string.sin_destino) }
                    // --8<-- [end:imagen-compartir]
                }) { Text(tituloCompartir) }
            }
            estado.mensaje?.let { Text(stringResource(it)) }
        }
    }
}
