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
import androidx.compose.foundation.clickable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import es.mjsplayground.pmdm.desarrollo.cuaderno.model.Nota
import es.mjsplayground.pmdm.desarrollo.cuaderno.model.esNotaValida
import es.mjsplayground.pmdm.desarrollo.cuaderno.navigation.Detalle
import es.mjsplayground.pmdm.desarrollo.cuaderno.navigation.Lista
import es.mjsplayground.pmdm.desarrollo.cuaderno.navigation.Nueva

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { CuadernoTheme { Cuaderno() } }
    }
}

@Composable
private fun Cuaderno() {
    var notas by remember { mutableStateOf(emptyList<Nota>()) }
    var siguienteId by remember { mutableStateOf(1L) }
    val nav = rememberNavController()
    Scaffold(modifier = Modifier.fillMaxSize().imePadding()) { margen ->
        NavHost(nav, startDestination = Lista, modifier = Modifier.padding(margen)) {
            composable<Lista> {
                LazyColumn(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    item { Text(stringResource(R.string.app_name)) }
                    item { Button(onClick = { nav.navigate(Nueva) }) { Text(stringResource(R.string.nueva)) } }
                    if (notas.isEmpty()) item { Text(stringResource(R.string.vacia)) }
                    items(notas, key = { it.id }) { nota ->
                        Column(Modifier.fillMaxWidth().clickable { nav.navigate(Detalle(nota.id)) }.padding(12.dp)) {
                            Text(nota.titulo); Text(nota.contenido)
                        }
                    }
                }
            }
            composable<Nueva> {
                Editor(Nota(titulo = "", contenido = ""), { nav.popBackStack() }) { nota ->
                    notas = notas + nota.copy(id = siguienteId++)
                    nav.popBackStack()
                }
            }
            composable<Detalle> { entry ->
                val nota = notas.find { it.id == entry.toRoute<Detalle>().id }
                if (nota == null) {
                    Column(Modifier.padding(16.dp)) {
                        Text(stringResource(R.string.nota_ausente))
                        TextButton(onClick = { nav.popBackStack() }) { Text(stringResource(R.string.volver)) }
                    }
                } else Editor(nota, { nav.popBackStack() }) { nueva ->
                    notas = notas.map { if (it.id == nueva.id) nueva else it }
                    nav.popBackStack()
                }
            }
        }
    }
}
@Composable
private fun Editor(nota: Nota, onVolver: () -> Unit, onGuardar: (Nota) -> Unit) {
    var titulo by rememberSaveable(nota.id) { mutableStateOf(nota.titulo) }
    var contenido by rememberSaveable(nota.id) { mutableStateOf(nota.contenido) }
    Column(Modifier.padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        TextButton(onClick = onVolver) { Text(stringResource(R.string.volver)) }
        OutlinedTextField(value = titulo, onValueChange = { titulo = it },
            label = { Text(stringResource(R.string.titulo)) }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = contenido, onValueChange = { contenido = it },
            label = { Text(stringResource(R.string.contenido)) }, modifier = Modifier.fillMaxWidth())
        Text(stringResource(R.string.validacion))
        Button(onClick = { onGuardar(nota.copy(titulo = titulo.trim(), contenido = contenido.trim())) },
            enabled = esNotaValida(titulo, contenido)) { Text(stringResource(R.string.guardar)) }
    }
}
