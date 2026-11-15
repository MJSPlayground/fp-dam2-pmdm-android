package es.mjsplayground.pmdm.diseno.galeria

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import es.mjsplayground.pmdm.diseno.galeria.ui.GaleriaTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { GaleriaTheme { Galeria(calculateWindowSizeClass(this).widthSizeClass) } }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun Galeria(ancho: WindowWidthSizeClass) {
    var categoria by rememberSaveable { mutableStateOf<Int?>(null) }
    var soloFavoritos by rememberSaveable { mutableStateOf(false) }
    var favoritos by rememberSaveable { mutableStateOf(listOf<Int>()) }
    var elegido by rememberSaveable { mutableStateOf<Int?>(null) }
    var refrescando by remember { mutableStateOf(false) }
    var revision by rememberSaveable { mutableIntStateOf(0) }
    val drawer = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val visibles = seleccionar(laminas, categoria, soloFavoritos, favoritos.toSet())
    val compacto = ancho == WindowWidthSizeClass.Compact
    val actualizar = {
        if (!refrescando) scope.launch {
            refrescando = true
            try { delay(800); revision++ } finally { refrescando = false }
        }
        Unit
    }
    BackHandler(elegido != null) { elegido = null }
    ModalNavigationDrawer(drawerState = drawer, gesturesEnabled = !compacto,
        drawerContent = {
            ModalDrawerSheet(Modifier.testTag("drawer")) {
                Text(stringResource(R.string.app_name), Modifier.padding(16.dp))
                listOf(false, true).forEach { favoritosDestino ->
                    NavigationDrawerItem(
                        label = { Text(stringResource(if (favoritosDestino) R.string.favoritos else R.string.catalogo)) },
                        selected = soloFavoritos == favoritosDestino,
                        onClick = { soloFavoritos = favoritosDestino; elegido = null; scope.launch { drawer.close() } }
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text(stringResource(R.string.app_name), fontFamily = FontFamily.Serif) },
                    navigationIcon = {
                        if (!compacto) TextButton(onClick = { scope.launch { drawer.open() } }) {
                            Text(stringResource(R.string.menu))
                        }
                    }, actions = { TextButton(onClick = actualizar) { Text(stringResource(R.string.actualizar)) } })
            },
            bottomBar = {
                if (compacto) NavigationBar(Modifier.testTag("barra")) {
                    listOf(false, true).forEach { favoritosDestino ->
                        NavigationBarItem(selected = soloFavoritos == favoritosDestino,
                            onClick = { soloFavoritos = favoritosDestino; elegido = null },
                            icon = { Icon(painterResource(R.drawable.ic_app), contentDescription = null) },
                            label = { Text(stringResource(if (favoritosDestino) R.string.favoritos else R.string.catalogo)) })
                    }
                }
            }
        ) { margen ->
            SharedTransitionLayout(Modifier.padding(margen)) {
                AnimatedContent(targetState = elegido, label = "detalle") { id ->
                    val transicion = this
                    if (id == null) {
                        PullToRefreshBox(isRefreshing = refrescando, onRefresh = actualizar) {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(columnas(ancho)),
                                modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                item(span = { GridItemSpan(maxLineSpan) }) {
                                    Column {
                                        Text(stringResource(R.string.revision, revision), Modifier.padding(8.dp))
                                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            items(listOf<Int?>(null, R.string.naturaleza, R.string.ciudad)) { grupo ->
                                                FilterChip(selected = categoria == grupo,
                                                    onClick = { categoria = grupo },
                                                    label = { Text(stringResource(grupo ?: R.string.todas)) })
                                            }
                                        }
                                    }
                                }
                                if (refrescando) item(span = { GridItemSpan(maxLineSpan) }) { Esqueleto() }
                                if (visibles.isEmpty()) item(span = { GridItemSpan(maxLineSpan) }) {
                                    Text(stringResource(R.string.vacia), Modifier.padding(16.dp))
                                }
                                items(visibles, key = { it.id }) { lamina ->
                                    Card(onClick = { elegido = lamina.id }, modifier = Modifier.fillMaxWidth()) {
                                        Foto(lamina.imagen, Modifier.fillMaxWidth().height(150.dp).sharedElement(
                                            rememberSharedContentState("foto-${lamina.id}"), transicion))
                                        Text(stringResource(lamina.titulo), Modifier.padding(12.dp),
                                            style = MaterialTheme.typography.titleMedium)
                                        val marcado = lamina.id in favoritos
                                        val estado = stringResource(if (marcado) R.string.marcado else R.string.sin_marcar)
                                        val nombreAccion = stringResource(R.string.accion_favorito,
                                            stringResource(if (marcado) R.string.quitar else R.string.guardar),
                                            stringResource(lamina.titulo))
                                        TextButton(
                                            onClick = { favoritos = alternar(favoritos.toSet(), lamina.id).toList() },
                                            modifier = Modifier.semantics { contentDescription = nombreAccion; stateDescription = estado }
                                        ) { Text(stringResource(if (marcado) R.string.quitar else R.string.guardar)) }
                                    }
                                }
                            }
                        }
                    } else {
                        val lamina = laminas.first { it.id == id }
                        LazyVerticalGrid(columns = GridCells.Fixed(1), modifier = Modifier.fillMaxSize().padding(16.dp)) {
                            item { TextButton(onClick = { elegido = null }) { Text(stringResource(R.string.volver)) } }
                            item {
                                Foto(lamina.imagen, Modifier.fillMaxWidth().height(280.dp).sharedElement(
                                    rememberSharedContentState("foto-${lamina.id}"), transicion))
                            }
                            item { Text(stringResource(lamina.titulo), style = MaterialTheme.typography.headlineMedium) }
                            item { Text(stringResource(R.string.descripcion), Modifier.padding(vertical = 16.dp)) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Foto(modelo: Any, modifier: Modifier = Modifier) {
    var fallo by remember(modelo) { mutableStateOf(false) }
    var cargando by remember(modelo) { mutableStateOf(true) }
    Box(modifier.background(MaterialTheme.colorScheme.surfaceVariant)) {
        AsyncImage(model = modelo, contentDescription = null, contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            onSuccess = { cargando = false; fallo = false },
            onError = { cargando = false; fallo = true })
        if (cargando) LinearProgressIndicator(Modifier.fillMaxWidth())
        if (fallo) Text(stringResource(R.string.error_imagen), Modifier.padding(16.dp))
    }
}

@Composable
fun Esqueleto() {
    val transicion = rememberInfiniteTransition(label = "carga")
    val alpha by transicion.animateFloat(initialValue = .35f, targetValue = .7f,
        animationSpec = infiniteRepeatable(tween(600), RepeatMode.Reverse), label = "opacidad")
    Box(Modifier.fillMaxWidth().height(64.dp).alpha(alpha)
        .background(MaterialTheme.colorScheme.surfaceVariant))
}
