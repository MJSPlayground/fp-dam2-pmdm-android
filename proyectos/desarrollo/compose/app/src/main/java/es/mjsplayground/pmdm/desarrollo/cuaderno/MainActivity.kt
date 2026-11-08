package es.mjsplayground.pmdm.desarrollo.cuaderno

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import es.mjsplayground.pmdm.desarrollo.cuaderno.ui.CuadernoTheme
import es.mjsplayground.pmdm.desarrollo.cuaderno.model.Nota
import es.mjsplayground.pmdm.desarrollo.cuaderno.model.esNotaValida

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { CuadernoTheme { Cuaderno() } }
    }
}

@Composable
private fun Cuaderno() {
    var titulo by rememberSaveable { mutableStateOf("") }
    var contenido by rememberSaveable { mutableStateOf("") }
    var notas by remember { mutableStateOf(emptyList<Nota>()) }
    var siguienteId by remember { mutableStateOf(1L) }
    Scaffold(modifier = Modifier.fillMaxSize().imePadding()) { margen ->
        LazyColumn(Modifier.padding(margen).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item { Text(stringResource(R.string.app_name)) }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(value = titulo, onValueChange = { titulo = it },
                        label = { Text(stringResource(R.string.titulo)) }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = contenido, onValueChange = { contenido = it },
                        label = { Text(stringResource(R.string.contenido)) }, modifier = Modifier.fillMaxWidth())
                    Text(stringResource(R.string.validacion))
                    Button(enabled = esNotaValida(titulo, contenido), onClick = {
                        notas = notas + Nota(siguienteId++, titulo.trim(), contenido.trim())
                        titulo = ""; contenido = ""
                    }) { Text(stringResource(R.string.guardar)) }
                }
            }
            if (notas.isEmpty()) item { Text(stringResource(R.string.vacia)) }
            items(notas, key = { it.id }) { nota ->
                Column { Text(nota.titulo); Text(nota.contenido) }
            }
        }
    }
}
