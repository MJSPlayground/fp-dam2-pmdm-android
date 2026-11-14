package es.mjsplayground.pmdm.multimedia.reproductor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import es.mjsplayground.pmdm.multimedia.reproductor.ui.MultimediaTheme

class MainActivity : ComponentActivity() {
    // TODO player-crear: crear un ControlReproductor al entrar en primer plano.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); enableEdgeToEdge()
        setContent { MultimediaTheme {
            Scaffold(Modifier.fillMaxSize()) { margen ->
                Column(Modifier.padding(margen).padding(16.dp).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(stringResource(R.string.app_name)); Text(stringResource(R.string.pendiente))
                    // TODO player-cargar: preparar tono.wav o una URI de vídeo seleccionada.
                    Button(enabled = false, onClick = {}) { Text(stringResource(R.string.audio)) }
                    Button(enabled = false, onClick = {}) { Text(stringResource(R.string.video)) }
                    Button(enabled = false, onClick = {}) { Text(stringResource(R.string.play)) }
                    Button(enabled = false, onClick = {}) { Text(stringResource(R.string.pause)) }
                    Button(enabled = false, onClick = {}) { Text(stringResource(R.string.stop)) }
                    // TODO player-eventos: observar estado/isPlaying/error y vincular AnimatedVisibility.
                }
            }
        } }
    }
    // TODO player-liberar: liberar ExoPlayer en onStop y readquirir en onStart.
}

