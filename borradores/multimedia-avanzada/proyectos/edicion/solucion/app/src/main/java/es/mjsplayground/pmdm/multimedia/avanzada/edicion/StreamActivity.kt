package es.mjsplayground.pmdm.multimedia.avanzada.edicion
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.ui.PlayerView
import androidx.media3.exoplayer.rtsp.RtspMediaSource
import es.mjsplayground.pmdm.multimedia.avanzada.edicion.ui.MultimediaTheme
@OptIn(UnstableApi::class)
class StreamActivity : ComponentActivity() {
    private var player by mutableStateOf<ExoPlayer?>(null)
    private var estado by mutableStateOf(R.string.stream_preparando)
    private var anchoBanda by mutableStateOf(0L)
    private var primerFotograma by mutableStateOf(false)
    private var perdidos by mutableStateOf(0)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); enableEdgeToEdge()
        setContent { MultimediaTheme { Scaffold(Modifier.fillMaxSize()) { margen ->
            Column(Modifier.padding(margen).padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(stringResource(R.string.stream_titulo), style = MaterialTheme.typography.headlineSmall)
                Text(stringResource(estado))
                player?.let { motor -> AndroidView(factory = { PlayerView(it) }, update = { it.player = motor },
                    modifier = Modifier.fillMaxWidth().height(250.dp)) }
                Text(stringResource(R.string.stream_metricas, anchoBanda, perdidos))
                if (primerFotograma) Text(stringResource(R.string.stream_primera_imagen))
                Text(stringResource(R.string.stream_limites))
                Button(onClick = { finish() }) { Text(stringResource(R.string.volver)) }
            }
        } } }
    }
    override fun onStart() {
        super.onStart()
        try {
            val url = validarFuente(intent.getStringExtra("fuente") ?: "")
            player = ExoPlayer.Builder(this).build().also { motor ->
                motor.addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        estado = when (playbackState) {
                            Player.STATE_BUFFERING -> R.string.stream_preparando
                            Player.STATE_READY -> R.string.stream_listo
                            Player.STATE_ENDED -> R.string.stream_fin
                            else -> R.string.stream_inactivo
                        }
                    }
                    override fun onPlayerError(error: PlaybackException) { estado = R.string.error }
                })
                motor.addAnalyticsListener(object : AnalyticsListener {
                    override fun onRenderedFirstFrame(eventTime: AnalyticsListener.EventTime, output: Any, renderTimeMs: Long) { primerFotograma = true }
                    override fun onBandwidthEstimate(eventTime: AnalyticsListener.EventTime, totalLoadTimeMs: Int, totalBytesLoaded: Long, bitrateEstimate: Long) { anchoBanda = bitrateEstimate / 1000 }
                    override fun onDroppedVideoFrames(eventTime: AnalyticsListener.EventTime, droppedFrames: Int, elapsedMs: Long) { perdidos += droppedFrames }
                })
                val item = MediaItem.fromUri(url)
                if (url.startsWith("rtsp:")) motor.setMediaSource(RtspMediaSource.Factory().setForceUseRtpTcp(true).createMediaSource(item))
                else motor.setMediaItem(item)
                motor.prepare(); motor.play()
            }
        } catch (_: Exception) { estado = R.string.error }
    }
    override fun onStop() { player?.release(); player = null; super.onStop() }
}
