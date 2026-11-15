package es.mjsplayground.pmdm.segundoplano.estudio

import android.app.PendingIntent
import android.content.Intent
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

class ReproductorService : MediaSessionService() {
    private var sesion: MediaSession? = null

    override fun onCreate() {
        super.onCreate()
        val reproductor = ExoPlayer.Builder(this).build().apply {
            setAudioAttributes(AudioAttributes.Builder().setUsage(C.USAGE_MEDIA)
                .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC).build(), true)
            setHandleAudioBecomingNoisy(true)
            setMediaItem(MediaItem.Builder().setUri("android.resource://$packageName/${R.raw.tono}")
                .setMediaMetadata(MediaMetadata.Builder().setTitle(getString(R.string.audio)).build()).build())
            repeatMode = Player.REPEAT_MODE_ONE
            prepare()
        }
        val abrir = PendingIntent.getActivity(this, 1, Intent(this, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE)
        sesion = MediaSession.Builder(this, reproductor).setSessionActivity(abrir).build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = sesion

    override fun onDestroy() {
        sesion?.run { player.release(); release() }
        sesion = null
        super.onDestroy()
    }
}
