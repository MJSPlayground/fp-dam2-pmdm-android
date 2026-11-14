package es.mjsplayground.pmdm.multimedia.imagenes

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
import es.mjsplayground.pmdm.multimedia.imagenes.ui.MultimediaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MultimediaTheme {
                Scaffold(Modifier.fillMaxSize()) { margen ->
                    Column(Modifier.padding(margen).padding(16.dp).verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(stringResource(R.string.app_name))
                        Text(stringResource(R.string.pendiente))
                        // TODO imagen-carga: registrar Photo Picker y TakePicture; conservar la URI pendiente.
                        Button(enabled = false, onClick = {}) { Text(stringResource(R.string.seleccionar)) }
                        Button(enabled = false, onClick = {}) { Text(stringResource(R.string.capturar)) }
                        // TODO imagen-transformacion: decodificar con límite y mostrar una rotación de 90 grados.
                        Button(enabled = false, onClick = {}) { Text(stringResource(R.string.rotar)) }
                        // TODO imagen-exportacion: escribir un archivo nuevo PNG o JPEG y comprobarlo.
                        Button(enabled = false, onClick = {}) { Text(stringResource(R.string.png)) }
                        Button(enabled = false, onClick = {}) { Text(stringResource(R.string.jpeg)) }
                        // TODO imagen-compartir: enviar la URI resultante con permisos temporales.
                        Button(enabled = false, onClick = {}) { Text(stringResource(R.string.compartir)) }
                    }
                }
            }
        }
    }
}

