package es.mjsplayground.pmdm.multimedia.avanzada.edicion
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.midi.MidiManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.FileProvider
import com.canhub.cropper.CropImageView
import es.mjsplayground.pmdm.multimedia.avanzada.edicion.ui.MultimediaTheme
import java.io.File
class MainActivity : ComponentActivity() {
    private lateinit var recorte: CropImageView
    private lateinit var exportador: Exportador
    private var sonidos by mutableStateOf<EfectosSonoros?>(null)
    private var imagenLista by mutableStateOf(false)
    private var recortando by mutableStateOf(false)
    private var estadoImagen by mutableStateOf(R.string.imagen_inicial)
    private var mensaje by mutableStateOf("")
    private var enlace by mutableStateOf("")
    private var video by mutableStateOf<Uri?>(null)
    private var resultadoImagen by mutableStateOf<File?>(null)
    private var salidaImagen: File? = null
    private val imagen = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri -> if (uri != null) cargarImagen(uri) }
    private val selectorVideo = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri -> if (uri != null) video = uri }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); enableEdgeToEdge(); exportador = Exportador(this); recorte = CropImageView(this)
        recorte.setOnSetImageUriCompleteListener { _, _, error ->
            if (!isDestroyed) { imagenLista = error == null; estadoImagen = if (error == null) R.string.imagen_lista else R.string.error }
        }
        recorte.setOnCropImageCompleteListener { _, resultado ->
            val salida = salidaImagen; salidaImagen = null
            if (isDestroyed) { salida?.delete(); return@setOnCropImageCompleteListener }
            recortando = false
            if (resultado.isSuccessful && salida != null && salida.length() > 0) {
                resultadoImagen = salida
                val limites = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeFile(salida.absolutePath, limites)
                mensaje = getString(R.string.dimensiones, limites.outWidth, limites.outHeight, limites.outMimeType)
                estadoImagen = R.string.imagen_fin
            } else { salida?.delete(); estadoImagen = R.string.error }
        }
        setContent { MultimediaTheme {
            Scaffold(Modifier.fillMaxSize()) { margen ->
                Column(Modifier.padding(margen).padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineSmall)
                    Text(stringResource(R.string.explicacion))
                    Text(stringResource(R.string.titulo_imagen), style = MaterialTheme.typography.titleLarge)
                    Button(onClick = { cargarImagen(Uri.parse("android.resource://$packageName/${R.raw.patron}")) }, enabled = !recortando) { Text(stringResource(R.string.patron)) }
                    Button(onClick = { imagen.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }, enabled = !recortando) { Text(stringResource(R.string.elegir_imagen)) }
                    Text(stringResource(estadoImagen))
                    AndroidView(factory = { recorte }, modifier = Modifier.fillMaxWidth().height(260.dp))
                    Button(onClick = ::cuadrado, enabled = imagenLista && !recortando) { Text(stringResource(R.string.cuadrado)) }
                    Button(onClick = { recorte.rotateImage(90) }, enabled = imagenLista && !recortando) { Text(stringResource(R.string.rotar)) }
                    Button(onClick = ::guardarRecorte, enabled = imagenLista && !recortando) { Text(stringResource(R.string.guardar_recorte)) }
                    resultadoImagen?.let { archivo -> Button(onClick = { compartir(archivo, "image/png") }) { Text(stringResource(R.string.compartir_imagen)) } }
                    Text(stringResource(R.string.titulo_video), style = MaterialTheme.typography.titleLarge)
                    Button(onClick = { video = Uri.parse("android.resource://$packageName/${R.raw.muestra}") }, enabled = !exportador.activo) { Text(stringResource(R.string.muestra)) }
                    Button(onClick = { selectorVideo.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)) }, enabled = !exportador.activo) { Text(stringResource(R.string.elegir_video)) }
                    Text(stringResource(if (video == null) R.string.sin_video else R.string.video_elegido))
                    Button(onClick = { video?.let(exportador::iniciar) }, enabled = video != null && !exportador.activo) { Text(stringResource(R.string.exportar)) }
                    Button(onClick = exportador::cancelar, enabled = exportador.activo) { Text(stringResource(R.string.cancelar)) }
                    Text(stringResource(exportador.estado))
                    if (exportador.activo) {
                        val avance = exportador.progreso
                        if (avance == null) CircularProgressIndicator() else Text(stringResource(R.string.progreso, avance))
                    }
                    exportador.resultado?.let { archivo ->
                        Text(stringResource(R.string.resultado_video, archivo.name, archivo.length()))
                        Button(onClick = { compartir(archivo, "video/mp4") }, enabled = !exportador.activo) { Text(stringResource(R.string.compartir_video)) }
                    }
                    Text(stringResource(R.string.stream_titulo), style = MaterialTheme.typography.titleLarge)
                    OutlinedTextField(value = enlace, onValueChange = { enlace = it }, label = { Text(stringResource(R.string.stream_url)) }, modifier = Modifier.fillMaxWidth())
                    Button(onClick = { abrirStream(enlace) }) { Text(stringResource(R.string.stream_abrir)) }
                    Button(onClick = { abrirStream("android.resource://$packageName/${R.raw.muestra}") }) { Text(stringResource(R.string.stream_muestra)) }
                    Text(stringResource(R.string.titulo_sonido), style = MaterialTheme.typography.titleLarge)
                    Button(onClick = { mensaje = getString(if (sonidos?.reproducir() == true) R.string.sonido_solicitado else R.string.sonido_error) }, enabled = sonidos?.listo == true) { Text(stringResource(R.string.sonido)) }
                    Button(onClick = ::enumerarMidi) { Text(stringResource(R.string.midi)) }
                    if (mensaje.isNotEmpty()) Text(mensaje)
                }
            }
        } }
    }
    private fun cargarImagen(uri: Uri) { imagenLista = false; estadoImagen = R.string.imagen_cargando; recorte.setImageUriAsync(uri) }
    private fun cuadrado() { recorte.setAspectRatio(1, 1) }
    private fun guardarRecorte() {
        val carpeta = File(filesDir, "medios").apply { mkdirs() }
        val salida = File.createTempFile("recorte-", ".png", carpeta)
        salidaImagen = salida; recortando = true
        val uri = FileProvider.getUriForFile(this, "$packageName.archivos", salida)
        recorte.croppedImageAsync(saveCompressFormat = Bitmap.CompressFormat.PNG, saveCompressQuality = 100,
            reqWidth = 1024, reqHeight = 1024, customOutputUri = uri)
    }
    private fun compartir(archivo: File, mime: String) {
        val uri = FileProvider.getUriForFile(this, "$packageName.archivos", archivo)
        val envio = Intent(Intent.ACTION_SEND).apply {
            type = mime; putExtra(Intent.EXTRA_STREAM, uri); clipData = ClipData.newRawUri("resultado", uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        try { startActivity(Intent.createChooser(envio, getString(R.string.compartir))) }
        catch (_: ActivityNotFoundException) { mensaje = getString(R.string.sin_destino) }
    }
    private fun abrirStream(fuente: String) {
        try { startActivity(Intent(this, StreamActivity::class.java).putExtra("fuente", validarFuente(fuente))) }
        catch (_: Exception) { mensaje = getString(R.string.fuente_invalida) }
    }
    @Suppress("DEPRECATION")
    private fun enumerarMidi() { mensaje = getString(R.string.dispositivos_midi, getSystemService(MidiManager::class.java)?.devices?.size ?: 0) }
    override fun onStart() { super.onStart(); sonidos = EfectosSonoros(this) }
    override fun onStop() { exportador.cancelar(); sonidos?.liberar(); sonidos = null; super.onStop() }
    override fun onDestroy() { recorte.setOnCropImageCompleteListener(null); recorte.setOnSetImageUriCompleteListener(null); recorte.clearImage(); super.onDestroy() }
}
