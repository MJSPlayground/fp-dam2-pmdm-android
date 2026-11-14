package es.mjsplayground.pmdm.asincronia.buscador

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import es.mjsplayground.pmdm.asincronia.buscador.ui.BuscadorTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { BuscadorTheme { Buscador() } }
    }
}

@Composable
fun Buscador(modelo: BuscadorViewModel = viewModel()) {
    val consulta by modelo.consulta.collectAsStateWithLifecycle()
    val favoritos by modelo.favoritos.collectAsStateWithLifecycle()
    val resultado by modelo.resultado.collectAsStateWithLifecycle()
    val mensajes = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val ayuda = stringResource(R.string.ayuda_texto)
    var estable by remember { mutableStateOf(false) }
    // TODO efecto: un efecto con consulta como clave debe reiniciar estable y esperar un segundo.
    // Mant?n preparado el efecto de ciclo de vida y el scope del bot?n Ayuda.
    val propietario = LocalLifecycleOwner.current
    var retornos by remember { mutableIntStateOf(0) }
    DisposableEffect(propietario) {
        val observador = LifecycleEventObserver { _, evento ->
            if (evento == Lifecycle.Event.ON_START) retornos++
        }
        propietario.lifecycle.addObserver(observador)
        onDispose { propietario.lifecycle.removeObserver(observador) }
    }
    Scaffold(Modifier.fillMaxSize(), snackbarHost = { SnackbarHost(mensajes) }) { margen ->
        LazyColumn(
            Modifier.padding(margen).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineSmall) }
            item {
                OutlinedTextField(
                    value = consulta, onValueChange = modelo::cambiarConsulta,
                    label = { Text(stringResource(R.string.consulta)) }, modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().toggleable(
                        value = favoritos, role = Role.Checkbox,
                        onValueChange = modelo::cambiarFavoritos
                    ), verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = favoritos, onCheckedChange = null)
                    Text(stringResource(R.string.favoritos))
                }
            }
            item { Text(stringResource(R.string.simular)) }
            item { Text(stringResource(if (estable) R.string.estable else R.string.esperando)) }
            item { Text(stringResource(R.string.ciclo, retornos)) }
            item {
                Button(onClick = { scope.launch { mensajes.showSnackbar(ayuda) } }) {
                    Text(stringResource(R.string.ayuda))
                }
            }
            when {
                resultado.cargando -> item { CircularProgressIndicator(); Text(stringResource(R.string.cargando)) }
                resultado.error -> item { Text(stringResource(R.string.error), color = MaterialTheme.colorScheme.error) }
                resultado.libros.isEmpty() -> item { Text(stringResource(R.string.vacia)) }
                else -> items(resultado.libros, key = { it.id }) { libro ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            Text(libro.titulo)
                            Text(libro.categoria)
                            if (libro.favorito) Text(stringResource(R.string.favorito))
                        }
                    }
                }
            }
        }
    }
}
