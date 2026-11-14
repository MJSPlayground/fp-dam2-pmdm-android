package es.mjsplayground.pmdm.asincronia.buscador

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CatalogoTest {
    @Test fun filtroCombinaTextoFavoritosYOrden() {
        assertEquals(listOf(1L, 4L), filtrar(catalogoEjemplo, " KOTLIN ", false).map { it.id })
        assertEquals(listOf(1L), filtrar(catalogoEjemplo, "kotlin", true).map { it.id })
        assertTrue(filtrar(catalogoEjemplo, "inexistente", false).isEmpty())
        assertEquals(5, catalogoEjemplo.size)
    }

    @Test fun debounceNoBuscaCadaCaracter() = runTest {
        val consulta = MutableStateFlow("")
        val eventos = mutableListOf<ResultadoBusqueda>()
        backgroundScope.launch { flujoBusqueda(consulta, MutableStateFlow(false)).collect(eventos::add) }
        runCurrent()
        consulta.value = "k"
        advanceTimeBy(200)
        consulta.value = "kot"
        advanceTimeBy(299)
        runCurrent()
        assertTrue(eventos.isEmpty())
        advanceTimeBy(1)
        runCurrent()
        assertTrue(eventos.single().cargando)
        advanceTimeBy(600)
        runCurrent()
        assertEquals(listOf(1L, 4L), eventos.last().libros.map { it.id })
    }

    @Test fun nuevaConsultaCancelaLaAnteriorAlSuperarDebounce() = runTest {
        val consulta = MutableStateFlow("kotlin")
        val eventos = mutableListOf<ResultadoBusqueda>()
        backgroundScope.launch { flujoBusqueda(consulta, MutableStateFlow(false)).collect(eventos::add) }
        runCurrent()
        advanceTimeBy(300)
        runCurrent() // Kotlin en curso, terminaría en 900.
        consulta.value = "rutas"
        advanceTimeBy(300)
        runCurrent() // Se cancela en 600 y rutas terminará en 1200.
        advanceTimeBy(600)
        runCurrent()
        assertEquals(1, eventos.count { !it.cargando })
        assertEquals(setOf(3L, 5L), eventos.last().libros.map { it.id }.toSet())
        assertFalse(eventos.any { it.error })
    }

    @Test fun favoritosRecalculaSinNuevaEscritura() = runTest {
        val favoritos = MutableStateFlow(false)
        val eventos = mutableListOf<ResultadoBusqueda>()
        backgroundScope.launch { flujoBusqueda(MutableStateFlow("kotlin"), favoritos).collect(eventos::add) }
        runCurrent()
        advanceTimeBy(900)
        runCurrent()
        assertEquals(2, eventos.last().libros.size)
        favoritos.value = true
        runCurrent()
        assertTrue(eventos.last().cargando)
        advanceTimeBy(600)
        runCurrent()
        assertEquals(listOf(1L), eventos.last().libros.map { it.id })
    }

    @Test fun errorNoTerminaElFlujo() = runTest {
        val consulta = MutableStateFlow("!error")
        val eventos = mutableListOf<ResultadoBusqueda>()
        backgroundScope.launch { flujoBusqueda(consulta, MutableStateFlow(false)).collect(eventos::add) }
        runCurrent()
        advanceTimeBy(900)
        runCurrent()
        assertTrue(eventos.last().error)
        consulta.value = "compose"
        advanceTimeBy(900)
        runCurrent()
        assertFalse(eventos.last().error)
        assertEquals(listOf(2L), eventos.last().libros.map { it.id })
    }

    @Test fun resumenNoCuentaDuplicados() {
        assertEquals(mapOf("Programación" to 3, "Viajes" to 2), resumenPorCategoria(catalogoEjemplo + catalogoEjemplo.first()))
        assertTrue(resumenPorCategoria(emptyList()).isEmpty())
    }

    @Test fun dosProveedoresSeEjecutanEnParaleloYDeduplican() = runTest {
        val inicio = testScheduler.currentTime
        val libros = catalogoDoble(
            { delay(400); catalogoEjemplo.take(2) },
            { delay(600); catalogoEjemplo.takeLast(4) }
        )
        assertEquals(600, testScheduler.currentTime - inicio)
        assertEquals(5, libros.size)
        assertEquals(libros.map { it.titulo.normalizada() }.sorted(), libros.map { it.titulo.normalizada() })
    }

    @Test fun falloDeUnaFuenteCancelaLaOtra() = runTest {
        var liberada = false
        var recibida = false
        try {
            catalogoDoble(
                { delay(100); throw IllegalArgumentException("Fallo previsto") },
                { try { delay(1_000); catalogoEjemplo } finally { liberada = true } }
            )
        } catch (_: IllegalArgumentException) { recibida = true }
        assertTrue(recibida)
        assertTrue(liberada)
        assertEquals(100, testScheduler.currentTime)
    }

    @Test fun cancelacionNoSeConvierteEnError() = runTest {
        var recibida = false
        try { intentarConsulta { throw CancellationException("salir") } }
        catch (_: CancellationException) { recibida = true }
        assertTrue(recibida)
        assertTrue(intentarConsulta { throw IllegalArgumentException("fallo") }.error)
        assertEquals(catalogoEjemplo, intentarConsulta { catalogoEjemplo }.libros)
    }
}
