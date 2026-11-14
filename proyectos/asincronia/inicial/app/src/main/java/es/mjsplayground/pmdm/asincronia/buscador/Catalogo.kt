package es.mjsplayground.pmdm.asincronia.buscador

import java.util.Locale
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transformLatest

data class Libro(val id: Long, val titulo: String, val categoria: String, val favorito: Boolean)

val catalogoEjemplo = listOf(
    Libro(1, "Kotlin en práctica", "Programación", true),
    Libro(2, "Android con Compose", "Programación", false),
    Libro(3, "Rutas por el bosque", "Viajes", true),
    Libro(4, "Kotlin y corrutinas", "Programación", false),
    Libro(5, "Cuaderno de rutas", "Viajes", false)
)

fun String.normalizada(): String = trim().lowercase(Locale.ROOT)

fun filtrar(libros: List<Libro>, consulta: String, soloFavoritos: Boolean): List<Libro> =
    // TODO colecciones: filtra por texto y favorito y ordena sin modificar la lista recibida.
    libros

data class ResultadoBusqueda(
    val libros: List<Libro> = emptyList(),
    val cargando: Boolean = false,
    val error: Boolean = false
)

/** Laboratorio local: delay simula latencia; no se realiza ninguna petición de red. */
@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
fun flujoBusqueda(
    consultas: Flow<String>,
    favoritos: Flow<Boolean>,
    libros: List<Libro> = catalogoEjemplo,
    esperaEscritura: Long = 300,
    latencia: Long = 600
): Flow<ResultadoBusqueda> = consultas.map {
    // TODO flujo: combina consulta pausada y favoritos; cancela la b?squeda anterior.
    // Emite carga, simula latencia y representa ?xito/error sin consumir CancellationException.
    ResultadoBusqueda(libros = libros)
}
