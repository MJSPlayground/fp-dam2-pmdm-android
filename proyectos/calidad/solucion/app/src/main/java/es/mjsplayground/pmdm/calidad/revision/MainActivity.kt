package es.mjsplayground.pmdm.calidad.revision

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import es.mjsplayground.pmdm.calidad.revision.ui.RevisionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RevisionTheme {
                val modelo: RevisionViewModel = viewModel()
                val estado by modelo.estado.collectAsStateWithLifecycle()
                RevisionPantalla(estado, modelo::cargar)
            }
        }
    }
}

@Composable
fun RevisionPantalla(estado: RevisionState, recargar: () -> Unit) {
    Scaffold(Modifier.fillMaxSize()) { margen ->
        LazyColumn(Modifier.padding(margen).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item { Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineSmall) }
            item { Button(onClick = recargar) { Text(stringResource(R.string.cargar)) } }
            when {
                estado.cargando -> item {
                    Column { CircularProgressIndicator(); Text(stringResource(R.string.cargando)) }
                }
                estado.error -> item { Text(stringResource(R.string.error), color = MaterialTheme.colorScheme.error) }
                estado.notas.isEmpty() -> item { Text(stringResource(R.string.vacia)) }
                else -> items(estado.notas, key = { it.id }) { Text(it.titulo) }
            }
        }
    }
}
