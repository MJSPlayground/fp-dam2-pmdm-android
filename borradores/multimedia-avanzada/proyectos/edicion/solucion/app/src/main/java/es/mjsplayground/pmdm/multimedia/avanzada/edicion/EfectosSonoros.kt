package es.mjsplayground.pmdm.multimedia.avanzada.edicion
import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.compose.runtime.*
class EfectosSonoros(contexto: Context) {
    private val pool = SoundPool.Builder().setMaxStreams(2).setAudioAttributes(AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()).build()
    var listo by mutableStateOf(false)
        private set
    private val sonido: Int
    init {
        pool.setOnLoadCompleteListener { _, _, resultado -> listo = resultado == 0 }
        sonido = pool.load(contexto, R.raw.tono, 1)
    }
    fun reproducir(): Boolean = listo && pool.play(sonido, 0.6f, 0.6f, 1, 0, 1f) != 0
    fun liberar() { listo = false; pool.release() }
}
