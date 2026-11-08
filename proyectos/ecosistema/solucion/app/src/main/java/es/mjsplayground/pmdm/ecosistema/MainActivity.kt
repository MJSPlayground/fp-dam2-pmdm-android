package es.mjsplayground.pmdm.ecosistema

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    var comprobado by remember { mutableStateOf(false) }
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = stringResource(R.string.saludo))
            Button(onClick = { comprobado = true }) {
                Text(text = stringResource(R.string.comprobar))
            }
            if (comprobado) {
                Text(text = stringResource(R.string.respuesta))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PrimeraPantallaPreview() {
    PrimeraAplicacionTheme { PrimeraPantalla() }
}
