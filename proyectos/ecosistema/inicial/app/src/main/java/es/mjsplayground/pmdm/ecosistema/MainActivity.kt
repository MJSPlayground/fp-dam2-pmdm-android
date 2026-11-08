package es.mjsplayground.pmdm.ecosistema

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.mjsplayground.pmdm.ecosistema.ui.PrimeraAplicacionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { PrimeraAplicacionTheme { PrimeraPantalla() } }
    }
}

@Composable
fun PrimeraPantalla() {
    // TODO etapa-interaccion: conservar aquí si se ha pulsado el botón.
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(24.dp)) {
            // TODO etapa-texto: cambiar saludo en res/values/strings.xml.
            Text(text = stringResource(R.string.saludo))
            // TODO etapa-interaccion: añadir Button "Comprobar" debajo del texto.
            // Al pulsarlo, mostrar el recurso respuesta.
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PrimeraPantallaPreview() {
    PrimeraAplicacionTheme { PrimeraPantalla() }
}
