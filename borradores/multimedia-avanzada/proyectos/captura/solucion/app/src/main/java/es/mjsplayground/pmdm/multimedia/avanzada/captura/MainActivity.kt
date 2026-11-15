package es.mjsplayground.pmdm.multimedia.avanzada.captura

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Surface
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import es.mjsplayground.pmdm.multimedia.avanzada.captura.ui.MultimediaTheme
import java.io.File

class MainActivity : ComponentActivity() {
    private lateinit var visor: PreviewView
    private var proveedor: ProcessCameraProvider? = null
    private var foto: ImageCapture? = null
    private var video: VideoCapture<Recorder>? = null
    private var grabacion: Recording? = null
    private var modoVideo by mutableStateOf(false)
    private var preparada by mutableStateOf(false)
    private var ocupada by mutableStateOf(false)
    private var grabando by mutableStateOf(false)
    private var estado by mutableStateOf(R.string.inicial)
    private var ultimo by mutableStateOf<File?>(null)
    private val permiso = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) iniciarCamara() else estado = R.string.denegado
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        visor = PreviewView(this).apply { implementationMode = PreviewView.ImplementationMode.COMPATIBLE }
        setContent { MultimediaTheme {
            Scaffold(Modifier.fillMaxSize()) { margen ->
                Column(Modifier.padding(margen).padding(16.dp).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineSmall)
                    Text(stringResource(R.string.explicacion))
                    Button(onClick = ::solicitarCamara, enabled = !ocupada && !grabando) { Text(stringResource(R.string.activar)) }
                    Text(stringResource(estado))
                    AndroidView(factory = { visor }, modifier = Modifier.fillMaxWidth().height(280.dp))
                    Button(onClick = { modoVideo = !modoVideo; iniciarCamara() }, enabled = preparada && !ocupada && !grabando) {
                        Text(stringResource(if (modoVideo) R.string.modo_foto else R.string.modo_video))
                    }
                    if (!modoVideo) Button(onClick = ::capturarFoto, enabled = preparada && !ocupada) { Text(stringResource(R.string.foto)) }
                    else {
                        Button(onClick = ::grabar, enabled = preparada && !ocupada && !grabando) { Text(stringResource(R.string.grabar)) }
                        Button(onClick = ::detener, enabled = grabando) { Text(stringResource(R.string.detener)) }
                    }
                    ultimo?.let { archivo ->
                        Text(stringResource(R.string.resultado, archivo.name, archivo.length()))
                        Button(onClick = ::compartir, enabled = !ocupada && !grabando) { Text(stringResource(R.string.compartir)) }
                    }
                }
            }
        } }
    }
    private fun solicitarCamara() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) iniciarCamara()
        else permiso.launch(Manifest.permission.CAMERA)
    }
    private fun iniciarCamara() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            estado = R.string.denegado; return
        }
        preparada = false
        estado = R.string.preparando
        val futuro = ProcessCameraProvider.getInstance(this)
        futuro.addListener({
            if (isDestroyed) return@addListener
            try {
                val actual = futuro.get()
                proveedor = actual
                if (!actual.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)) { estado = R.string.sin_camara; return@addListener }
                val preview = Preview.Builder().build().also { it.surfaceProvider = visor.surfaceProvider }
                actual.unbindAll()
                if (modoVideo) {
                    val recorder = Recorder.Builder().setQualitySelector(QualitySelector.from(Quality.SD,
                        FallbackStrategy.lowerQualityOrHigherThan(Quality.SD))).build()
                    video = VideoCapture.withOutput(recorder)
                    foto = null
                    actual.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview, video)
                } else {
                    foto = ImageCapture.Builder().build()
                    video = null
                    actual.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview, foto)
                }
                preparada = true
                estado = R.string.lista
            } catch (_: Exception) { estado = R.string.error }
        }, ContextCompat.getMainExecutor(this))
    }
    private fun archivo(extension: String): File {
        val carpeta = File(filesDir, "medios").apply { mkdirs() }
        return File.createTempFile("captura-", extension, carpeta)
    }
    private fun capturarFoto() {
        val captura = foto ?: return
        ocupada = true
        estado = R.string.capturando
        val salida = archivo(".jpg")
        captura.targetRotation = visor.display?.rotation ?: Surface.ROTATION_0
        captura.takePicture(ImageCapture.OutputFileOptions.Builder(salida).build(), ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(resultado: ImageCapture.OutputFileResults) {
                    if (isDestroyed) return
                    ocupada = false; ultimo = salida; estado = R.string.guardado
                }
                override fun onError(error: ImageCaptureException) {
                    salida.delete()
                    if (!isDestroyed) { ocupada = false; estado = R.string.error }
                }
            })
    }
    private fun grabar() {
        val captura = video ?: return
        val salida = archivo(".mp4")
        estado = R.string.iniciando
        ocupada = true
        try {
            val opciones = FileOutputOptions.Builder(salida).build()
            // Grabación sin micrófono: no utiliza withAudioEnabled ni pide RECORD_AUDIO.
            grabacion = captura.output.prepareRecording(this, opciones)
                .start(ContextCompat.getMainExecutor(this)) { evento -> procesarEvento(evento, salida) }
        } catch (_: Exception) { salida.delete(); ocupada = false; estado = R.string.error }
    }
    private fun procesarEvento(evento: VideoRecordEvent, salida: File) {
        if (isDestroyed) return
        when (evento) {
            is VideoRecordEvent.Start -> { ocupada = false; grabando = true; estado = R.string.grabando }
            is VideoRecordEvent.Finalize -> {
                ocupada = false; grabando = false; grabacion = null
                if (evento.hasError()) { salida.delete(); estado = R.string.error }
                else { ultimo = salida; estado = R.string.guardado }
            }
        }
    }
    private fun detener() {
        ocupada = true
        grabacion?.stop()
    }
    private fun compartir() {
        val archivo = ultimo ?: return
        val uri = FileProvider.getUriForFile(this, "$packageName.archivos", archivo)
        val envio = Intent(Intent.ACTION_SEND).apply {
            type = if (archivo.extension == "jpg") "image/jpeg" else "video/mp4"
            putExtra(Intent.EXTRA_STREAM, uri)
            clipData = ClipData.newRawUri("captura", uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        try { startActivity(Intent.createChooser(envio, getString(R.string.compartir))) }
        catch (_: ActivityNotFoundException) { estado = R.string.sin_destino }
    }
    override fun onStop() { grabacion?.stop(); super.onStop() }
    override fun onDestroy() { grabacion?.close(); proveedor?.unbindAll(); super.onDestroy() }
}
