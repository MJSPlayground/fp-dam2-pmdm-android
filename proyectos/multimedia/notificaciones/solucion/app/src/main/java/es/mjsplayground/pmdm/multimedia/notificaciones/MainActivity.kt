package es.mjsplayground.pmdm.multimedia.notificaciones

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import es.mjsplayground.pmdm.multimedia.notificaciones.ui.MultimediaTheme

class MainActivity : ComponentActivity() {
    private lateinit var avisos: Avisos
    private var estado by mutableStateOf(R.string.instruccion)
    private var detalle by mutableStateOf<String?>(null)
    private var pendiente = false
    private val permiso = registerForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
        if (concedido) publicar(pendiente) else estado = R.string.denegado
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        avisos = Avisos(this)
        avisos.crearCanal()
        pendiente = savedInstanceState?.getBoolean("nota-pendiente") ?: false
        detalle = savedInstanceState?.getString("detalle")
        if (intent.hasExtra(Avisos.EXTRA_DETALLE)) recibir(intent)
        setContent {
            MultimediaTheme {
                Scaffold(Modifier.fillMaxSize()) { margen ->
                    Column(Modifier.padding(margen).padding(24.dp).verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(stringResource(R.string.app_name))
                        if (detalle != null) {
                            Text(stringResource(if (detalle == "nota") R.string.detalle_nota else R.string.detalle_demo))
                            Button(onClick = { detalle = null; intent.removeExtra(Avisos.EXTRA_DETALLE) }) {
                                Text(stringResource(R.string.volver))
                            }
                        } else {
                            Text(stringResource(R.string.explicacion))
                            Button(onClick = { solicitar(false) }) { Text(stringResource(R.string.basica)) }
                            Button(onClick = { solicitar(true) }) { Text(stringResource(R.string.nota)) }
                            Text(stringResource(estado))
                            Button(onClick = {
                                startActivity(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                    .putExtra(Settings.EXTRA_APP_PACKAGE, packageName))
                            }) { Text(stringResource(R.string.ajustes)) }
                        }
                    }
                }
            }
        }
    }

    private fun solicitar(esNota: Boolean) {
        pendiente = esNota
        if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(this,
                Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            permiso.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else publicar(esNota)
    }

    private fun publicar(esNota: Boolean) {
        val datos = if (esNota) avisos.notaGuardada() else avisos.demostracion()
        if (datos == null) { estado = R.string.pendiente; return }
        estado = if (avisos.publicar(datos)) R.string.enviada else R.string.bloqueada
    }

    private fun recibir(nuevo: Intent) {
        detalle = nuevo.getStringExtra(Avisos.EXTRA_DETALLE)?.takeIf { it == "demo" || it == "nota" }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        recibir(intent)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean("nota-pendiente", pendiente)
        outState.putString("detalle", detalle)
        super.onSaveInstanceState(outState)
    }
}
