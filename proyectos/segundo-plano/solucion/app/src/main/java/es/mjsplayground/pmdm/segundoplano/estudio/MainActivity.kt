package es.mjsplayground.pmdm.segundoplano.estudio

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.work.WorkManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import es.mjsplayground.pmdm.segundoplano.estudio.ui.EstudioTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { EstudioTheme { Estudio() } }
    }
}

@Composable
fun Estudio() {
    val contexto = LocalContext.current
    var permitido by remember { mutableStateOf(Avisos.permitido(contexto)) }
    val permiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        permitido = Avisos.permitido(contexto)
    }
    var controlador by remember { mutableStateOf<MediaController?>(null) }
    var falloAudio by remember { mutableStateOf(false) }
    val trabajos by remember(contexto) {
        WorkManager.getInstance(contexto).getWorkInfosForUniqueWorkFlow("revision")
    }.collectAsStateWithLifecycle(initialValue = emptyList())
    DisposableEffect(contexto) {
        val futuro = MediaController.Builder(contexto,
            SessionToken(contexto, ComponentName(contexto, ReproductorService::class.java))).buildAsync()
        futuro.addListener({
            try { controlador = futuro.get() }
            catch (_: Exception) { falloAudio = true }
        }, ContextCompat.getMainExecutor(contexto))
        onDispose { controlador = null; MediaController.releaseFuture(futuro) }
    }
    Scaffold(Modifier.fillMaxSize()) { margen ->
        Column(Modifier.padding(margen).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(stringResource(R.string.app_name))
            Text(stringResource(if (permitido) R.string.autorizado else R.string.denegado))
            Button(onClick = {
                if (Build.VERSION.SDK_INT >= 33) permiso.launch(Manifest.permission.POST_NOTIFICATIONS)
                else permitido = Avisos.permitido(contexto)
            }) { Text(stringResource(R.string.permiso)) }
            Button(onClick = { permitido = Avisos.permitido(contexto); Avisos.mostrar(contexto) }) {
                Text(stringResource(R.string.inmediata))
            }
            Button(onClick = { permitido = Avisos.permitido(contexto); Avisos.mostrar(contexto, resumen = true) }) {
                Text(stringResource(R.string.expandida))
            }
            Button(enabled = permitido, onClick = { programar(contexto) }) { Text(stringResource(R.string.programar)) }
            Button(onClick = { WorkManager.getInstance(contexto).cancelUniqueWork("revision") }) {
                Text(stringResource(R.string.cancelar))
            }
            trabajos.forEach { trabajo -> Text(stringResource(R.string.estado_trabajo,
                trabajo.id.toString().take(8), trabajo.state.name, trabajo.runAttemptCount)) }
            Button(enabled = permitido, onClick = {
                contexto.sendBroadcast(Intent(contexto, RevisionReceiver::class.java).setAction(RevisionReceiver.ACCION))
            }) { Text(stringResource(R.string.receiver)) }
            Text(stringResource(R.string.audio_explicacion))
            Button(enabled = controlador != null, onClick = {
                controlador?.run { if (playbackState == Player.STATE_IDLE) prepare(); play() }
            }) { Text(stringResource(R.string.play)) }
            Button(enabled = controlador != null, onClick = { controlador?.pause() }) { Text(stringResource(R.string.pause)) }
            Button(enabled = controlador != null, onClick = { controlador?.run { stop(); seekTo(0) } }) {
                Text(stringResource(R.string.stop))
            }
            if (falloAudio) Text(stringResource(R.string.audio_error))
            Text(stringResource(R.string.fcm))
        }
    }
}
