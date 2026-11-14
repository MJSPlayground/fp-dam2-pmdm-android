package es.mjsplayground.pmdm.multimedia.reproductor

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.Player
import androidx.media3.ui.PlayerView
import es.mjsplayground.pmdm.multimedia.reproductor.ui.MultimediaTheme

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class MainActivity : ComponentActivity() {
    var control by mutableStateOf<ControlReproductor?>(null)
        private set
    private var uriActual: Uri? = null
    private var esVideo by mutableStateOf(false)
    private var seleccionCancelada by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        uriActual = savedInstanceState?.getString("uri")?.let(Uri::parse) ?: audioEjemplo()
        esVideo = savedInstanceState?.getBoolean("video") ?: false
        setContent {
            MultimediaTheme {
                val selector = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                    seleccionCancelada = uri == null
                    if (uri != null) { uriActual = uri; esVideo = true; control?.cargar(uri) }
                }
                Scaffold(Modifier.fillMaxSize()) { margen ->
                    Column(Modifier.padding(margen).padding(16.dp).verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(stringResource(R.string.app_name))
                        Button(onClick = { uriActual = audioEjemplo(); esVideo = false; control?.cargar(audioEjemplo()) }) {
                            Text(stringResource(R.string.audio))
                        }
                        Button(onClick = { selector.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)) }) {
                            Text(stringResource(R.string.video))
                        }
                        control?.let { actual ->
                            val estado = actual.estado
                            if (esVideo) AndroidView(
                                factory = { PlayerView(it).apply { useController = false; player = actual.player } },
                                update = { it.player = actual.player },
                                modifier = Modifier.fillMaxWidth().height(220.dp)
                            )
                            val texto = when {
                                estado.error -> R.string.error
                                estado.reproduciendo -> R.string.reproduciendo
                                estado.fase == Player.STATE_BUFFERING -> R.string.cargando
                                estado.fase == Player.STATE_ENDED -> R.string.finalizado
                                estado.fase == Player.STATE_IDLE -> R.string.detenido
                                else -> R.string.preparado
                            }
                            Text(stringResource(texto))
                            Button(onClick = actual::play, enabled = !estado.reproduciendo && !estado.error) { Text(stringResource(R.string.play)) }
                            Button(onClick = actual::pause, enabled = estado.reproduciendo) { Text(stringResource(R.string.pause)) }
                            Button(onClick = actual::stop) { Text(stringResource(R.string.stop)) }
                            /* TODO player-animacion: ligar visible al estado del motor. */
                            AnimatedVisibility(visible = false) { Text(stringResource(R.string.animacion)) }
                        }
                        if (seleccionCancelada) Text(stringResource(R.string.cancelada))
                    }
                }
            }
        }
    }
    private fun audioEjemplo() = Uri.parse("android.resource://" + packageName + "/" + R.raw.tono)
    override fun onStart() {
        super.onStart()
        control = ControlReproductor(this).also { it.cargar(uriActual ?: audioEjemplo()) }
    }
    override fun onStop() {
        control?.liberar()
        control = null
        super.onStop()
    }
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("uri", uriActual.toString())
        outState.putBoolean("video", esVideo)
        super.onSaveInstanceState(outState)
    }
}
