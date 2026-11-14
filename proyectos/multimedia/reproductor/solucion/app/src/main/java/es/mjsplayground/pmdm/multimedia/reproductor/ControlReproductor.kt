package es.mjsplayground.pmdm.multimedia.reproductor

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

data class EstadoReproductor(val fase: Int = Player.STATE_IDLE, val reproduciendo: Boolean = false, val error: Boolean = false)

class ControlReproductor(context: Context) {
    val player = ExoPlayer.Builder(context).build().apply {
        setAudioAttributes(AudioAttributes.Builder().setUsage(C.USAGE_MEDIA).setContentType(C.AUDIO_CONTENT_TYPE_MUSIC).build(), true)
        setHandleAudioBecomingNoisy(true)
    }
    var estado by mutableStateOf(EstadoReproductor())
        private set
    var liberado = false
        private set

    // --8<-- [start:player-eventos]
    private val listener = object : Player.Listener {
        override fun onEvents(player: Player, events: Player.Events) {
            estado = estado.copy(fase = player.playbackState, reproduciendo = player.isPlaying)
        }
        override fun onPlayerError(error: PlaybackException) { estado = estado.copy(error = true, reproduciendo = false) }
    }
    init { player.addListener(listener) }
    // --8<-- [end:player-eventos]

    // --8<-- [start:player-cargar]
    fun cargar(uri: Uri) {
        estado = EstadoReproductor()
        player.setMediaItem(MediaItem.fromUri(uri))
        player.prepare()
    }
    fun reproducir(uri: Uri) { cargar(uri); play() }
    fun play() {
        if (player.playbackState == Player.STATE_ENDED) player.seekTo(0)
        if (player.playbackState == Player.STATE_IDLE) player.prepare()
        player.play()
    }
    fun pause() { player.pause() }
    fun stop() { player.stop(); player.seekTo(0) }
    // --8<-- [end:player-cargar]

    // --8<-- [start:player-liberar]
    fun liberar() {
        if (!liberado) {
            player.removeListener(listener)
            player.release()
            liberado = true
        }
    }
    // --8<-- [end:player-liberar]
}
