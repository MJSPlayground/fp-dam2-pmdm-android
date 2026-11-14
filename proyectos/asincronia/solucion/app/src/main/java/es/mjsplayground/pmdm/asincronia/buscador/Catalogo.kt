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
    libros.filter { it.titulo.normalizada().contains(consulta.normalizada()) }
        .filter { !soloFavoritos || it.favorito }
        .sortedBy { it.titulo.normalizada() }

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
): Flow<ResultadoBusqueda> = combine(
    consultas.map { it.normalizada() }.debounce(esperaEscritura).distinctUntilChanged(),
    favoritos
) { texto, solo -> texto to solo }.transformLatest { (texto, solo) ->
    emit(ResultadoBusqueda(cargando = true))
    try {
        delay(latencia)
        if (texto == "!error") throw IllegalArgumentException("Fallo de laboratorio")
        emit(ResultadoBusqueda(libros = filtrar(libros, texto, solo)))
    } catch (cancelada: CancellationException) {
        throw cancelada
    } catch (_: IllegalArgumentException) {
        emit(ResultadoBusqueda(error = true))
    }
}
