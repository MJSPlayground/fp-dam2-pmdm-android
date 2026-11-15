package es.mjsplayground.pmdm.calidad.revision

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RevisionTest {
    @get:Rule val main = MainDispatcherRule()

    @Test fun tituloNormal() { assertTrue(tituloValido("Tres letras")) }

    @Test fun limitesDeTitulo() {
        assertFalse(tituloValido("ab"))
        assertTrue(tituloValido(" abc "))
        assertTrue(tituloValido("a".repeat(80)))
        assertFalse(tituloValido("a".repeat(81)))
        assertFalse(tituloValido("   "))
    }

    @Test fun repositorioDevuelveDatosTrasEspera() = runTest {
        val inicio = testScheduler.currentTime
        val notas = DemoRepository().obtener()
        assertEquals(listOf(1L, 2L), notas.map { it.id })
        assertEquals(300, testScheduler.currentTime - inicio)
    }

    @Test fun cargaExitoYUnaConsulta() = runTest {
        val repo = mockk<NotaRepository>()
        coEvery { repo.obtener() } coAnswers { delay(200); listOf(Nota(7, "Lista propia")) }
        val modelo = RevisionViewModel(repo)
        runCurrent()
        assertTrue(modelo.estado.value.cargando)
        advanceUntilIdle()
        assertEquals(listOf(Nota(7, "Lista propia")), modelo.estado.value.notas)
        assertFalse(modelo.estado.value.cargando)
        assertFalse(modelo.estado.value.error)
        coVerify(exactly = 1) { repo.obtener() }
    }

    @Test fun errorSeRecuperaConOtraCarga() = runTest {
        val repo = mockk<NotaRepository>()
        coEvery { repo.obtener() } throws IllegalStateException("Fallo controlado")
        val modelo = RevisionViewModel(repo)
        advanceUntilIdle()
        assertTrue(modelo.estado.value.error)
        assertFalse(modelo.estado.value.cargando)
        coEvery { repo.obtener() } returns emptyList()
        modelo.cargar()
        advanceUntilIdle()
        assertEquals(RevisionState(), modelo.estado.value)
        coVerify(exactly = 2) { repo.obtener() }
    }

    @Test fun recargarSustituyeUnaCargaLenta() = runTest {
        val repo = mockk<NotaRepository>()
        var numero = 0
        coEvery { repo.obtener() } coAnswers {
            val actual = ++numero
            delay(if (actual == 1) 1_000 else 100)
            listOf(Nota(actual.toLong(), "Consulta $actual"))
        }
        val modelo = RevisionViewModel(repo)
        runCurrent()
        advanceTimeBy(50)
        modelo.cargar()
        advanceUntilIdle()
        assertEquals(listOf(Nota(2, "Consulta 2")), modelo.estado.value.notas)
        assertFalse(modelo.estado.value.error)
        coVerify(exactly = 2) { repo.obtener() }
    }
}
