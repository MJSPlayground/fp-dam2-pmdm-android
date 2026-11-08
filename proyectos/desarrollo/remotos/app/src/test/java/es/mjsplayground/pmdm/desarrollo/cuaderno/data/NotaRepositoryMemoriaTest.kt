package es.mjsplayground.pmdm.desarrollo.cuaderno.data

import es.mjsplayground.pmdm.desarrollo.cuaderno.model.Nota
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class NotaRepositoryMemoriaTest {
    private val api = object : NotasApi {
        override suspend fun publicaciones(url: String) = emptyList<PublicacionDto>()
    }

    @Test fun guardaEditaBuscaEliminaYNoComparteEstadoEntreInstancias() = runBlocking {
        val repository = NotaRepository(api)
        val id = repository.guardar(Nota(titulo = " Compra ", contenido = " Fruta "))
        assertEquals("Compra", repository.buscar(id).first()?.titulo)
        repository.guardar(Nota(id, "Compra editada", "Pan"))
        assertEquals(1, repository.observar().first().size)
        assertEquals("Pan", repository.buscar(id).first()?.contenido)
        assertTrue(NotaRepository(api).observar().first().isEmpty())
        repository.eliminar(Nota(id, "Compra editada", "Pan"))
        assertTrue(repository.observar().first().isEmpty())
    }
}
