package es.mjsplayground.pmdm.persistencia.lecturas

import es.mjsplayground.pmdm.persistencia.lecturas.domain.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class AlternarLecturaTest {
    @Test fun invierteEstadoConservandoIdentidad() = runBlocking {
        var recibida: Pair<Long, Boolean>? = null
        val repo = object : LecturasRepository {
            override fun observar() = flowOf(emptyList<FichaLectura>())
            override suspend fun actualizar() = Unit
            override suspend fun marcar(id: Long, leida: Boolean) { recibida = id to leida }
        }
        AlternarLectura(repo)(FichaLectura(8, "Título", "Autor", emptyList(), false))
        assertEquals(8L to true, recibida)
        AlternarLectura(repo)(FichaLectura(8, "Título", "Autor", emptyList(), true))
        assertEquals(8L to false, recibida)
    }
}
