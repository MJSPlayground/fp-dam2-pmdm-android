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
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { CuadernoTheme { Cuaderno() } }
    }
}

@Composable
private fun Cuaderno() {
    // TODO compose-estado: declarar título y contenido como estado.
    Scaffold(modifier = Modifier.fillMaxSize().imePadding()) { margen ->
        Column(Modifier.padding(margen).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(stringResource(R.string.app_name))
            Text(stringResource(R.string.introduccion_inicial))
            OutlinedTextField(
                value = "", onValueChange = { /* TODO compose-titulo: actualizar estado */ },
                label = { Text(stringResource(R.string.titulo)) }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(
                value = "", onValueChange = { /* TODO compose-contenido: actualizar estado */ },
                label = { Text(stringResource(R.string.contenido)) }, modifier = Modifier.fillMaxWidth())
            Button(enabled = false, // TODO compose-validacion: validar los campos.
                onClick = { /* TODO compose-lista: añadir nota y limpiar formulario */ }) {
                Text(stringResource(R.string.guardar))
            }
            Text(stringResource(R.string.vacia))
            // TODO compose-lista: sustituir esta Column.verticalScroll por LazyColumn;
            // mostrar formulario en item y notas en items(key = id), sin listas anidadas.
        }
    }
}
