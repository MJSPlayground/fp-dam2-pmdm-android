package es.mjsplayground.pmdm.persistencia.lecturas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.ui.Alignment
import androidx.compose.ui.semantics.Role
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import es.mjsplayground.pmdm.persistencia.lecturas.ui.LecturasViewModel
import es.mjsplayground.pmdm.persistencia.lecturas.ui.LecturasTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val vm: LecturasViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LecturasTheme {
                val estado by vm.pantalla.collectAsStateWithLifecycle()
                Scaffold(Modifier.fillMaxSize()) { margen ->
                    LazyColumn(Modifier.padding(margen).padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        item { Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineSmall) }
                        item { Text(stringResource(R.string.explicacion)) }
                        item { Button(onClick = vm::demostracion, enabled = !estado.ocupada) { Text(stringResource(R.string.demo)) } }
                        item { Button(onClick = vm::actualizar, enabled = !estado.ocupada) { Text(stringResource(R.string.actualizar)) } }
                        item { Row(Modifier.fillMaxWidth().toggleable(value = estado.soloPendientes,
                            enabled = !estado.ocupada, role = Role.Checkbox, onValueChange = vm::cambiarFiltro),
                            horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = estado.soloPendientes, onCheckedChange = null)
                            Text(stringResource(R.string.pendientes))
                        } }
                        if (estado.cargando || estado.ocupada) item { CircularProgressIndicator() }
                        if (estado.error) item { Text(stringResource(R.string.error), color = MaterialTheme.colorScheme.error) }
                        if (!estado.cargando && estado.fichas.isEmpty()) item { Text(stringResource(R.string.vacia)) }
                        items(estado.fichas, key = { it.id }) { ficha ->
                            Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(ficha.titulo, style = MaterialTheme.typography.titleMedium)
                                Text(ficha.autor)
                                Text(ficha.etiquetas.joinToString(" · "))
                                Text(stringResource(if (ficha.leida) R.string.leida else R.string.pendiente))
                                TextButton(onClick = { vm.alternar(ficha) }, enabled = !estado.ocupada) {
                                    Text(stringResource(if (ficha.leida) R.string.marcar_pendiente else R.string.marcar_leida))
                                }
                            } }
                        }
                    }
                }
            }
        }
    }
}
