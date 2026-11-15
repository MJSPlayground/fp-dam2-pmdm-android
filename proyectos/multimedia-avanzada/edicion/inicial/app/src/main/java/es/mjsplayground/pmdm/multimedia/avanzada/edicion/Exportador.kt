package es.mjsplayground.pmdm.multimedia.avanzada.edicion
import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.annotation.OptIn
import androidx.compose.runtime.*
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.effect.Presentation
import androidx.media3.transformer.*
import androidx.media3.transformer.Composition
import java.io.File
@OptIn(UnstableApi::class)
fun prepararVideo(uri: Uri): EditedMediaItem = EditedMediaItem.Builder(MediaItem.fromUri(uri))
    .setRemoveAudio(false) // TODO media-audio: elimina la pista de audio de la copia.
    .setEffects(Effects(emptyList(), listOf(Presentation.createForHeight(144)))).build()
@OptIn(UnstableApi::class)
class Exportador(private val contexto: Context) {
    var activo by mutableStateOf(false)
        private set
    var progreso by mutableStateOf<Int?>(null)
        private set
    var estado by mutableStateOf(R.string.export_inicial)
        private set
    var resultado by mutableStateOf<File?>(null)
        private set
    private var transformer: Transformer? = null
    private var destino: File? = null
    private val handler = Handler(Looper.getMainLooper())
    private val consultar = object : Runnable {
        override fun run() {
            val motor = transformer ?: return
            if (!activo) return
            val valor = ProgressHolder()
            progreso = if (motor.getProgress(valor) == Transformer.PROGRESS_STATE_AVAILABLE) valor.progress else null
            handler.postDelayed(this, 250)
        }
    }
    fun iniciar(origen: Uri) {
        if (activo) return
        val carpeta = File(contexto.filesDir, "medios").apply { mkdirs() }
        val archivo = File.createTempFile("video-", ".mp4", carpeta)
        archivo.delete()
        destino = archivo; activo = true; estado = R.string.exportando; progreso = null
        try {
            transformer = Transformer.Builder(contexto.applicationContext)
                .setVideoMimeType(MimeTypes.VIDEO_H264).setAudioMimeType(MimeTypes.AUDIO_AAC)
                .addListener(object : Transformer.Listener {
                    override fun onCompleted(composition: Composition, exportResult: ExportResult) {
                        terminar()
                        if (archivo.isFile && archivo.length() > 0) { resultado = archivo; estado = R.string.export_fin }
                        else { archivo.delete(); estado = R.string.error }
                    }
                    override fun onError(composition: Composition, exportResult: ExportResult, exportException: ExportException) {
                        terminar(); archivo.delete(); estado = R.string.error
                    }
                }).build()
            transformer!!.start(prepararVideo(origen), archivo.absolutePath)
            handler.post(consultar)
        } catch (_: Exception) { terminar(); archivo.delete(); estado = R.string.error }
    }
    private fun terminar() { handler.removeCallbacks(consultar); activo = false; progreso = null; transformer = null; destino = null }
    fun cancelar() {
        if (!activo) return
        transformer?.cancel(); destino?.delete(); terminar(); estado = R.string.cancelada
    }
}
