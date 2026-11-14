package es.mjsplayground.pmdm.asincronia.buscador

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.async

fun resumenPorCategoria(libros: List<Libro>): Map<String, Int> =
    libros.distinctBy { it.id }.groupingBy { it.categoria }.eachCount().toSortedMap()

/** Los proveedores son suspendidos e inyectables para probarlos sin red. */
suspend fun catalogoDoble(
    primero: suspend () -> List<Libro>,
    segundo: suspend () -> List<Libro>
): List<Libro> = coroutineScope {
    val a = async { primero() }
    val b = async { segundo() }
    (a.await() + b.await()).distinctBy { it.id }.sortedBy { it.titulo.normalizada() }
}

suspend fun intentarConsulta(operacion: suspend () -> List<Libro>): ResultadoBusqueda = try {
    ResultadoBusqueda(libros = operacion())
} catch (cancelada: CancellationException) {
    throw cancelada
} catch (_: IllegalArgumentException) {
    ResultadoBusqueda(error = true)
}
