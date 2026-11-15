package es.mjsplayground.pmdm.calidad.revision

import org.junit.Assert.assertTrue
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

class RevisionTest {
    @get:Rule val main = MainDispatcherRule()
    @Test fun tituloNormal() { assertTrue(tituloValido("Tres letras")) }

    // TODO limites: comprobar 2/3/80/81 caracteres y espacios.
    @Ignore("Completa TODO limites antes de activar esta prueba")
    @Test fun limitesDeTitulo() { }

    // TODO repository: comprobar IDs y 300 ms con runTest.
    @Ignore("Completa TODO repository antes de activar esta prueba")
    @Test fun repositorioDevuelveDatosTrasEspera() { }

    // TODO viewmodel: MockK y tiempo virtual para carga y resultado.
    @Ignore("Completa TODO viewmodel antes de activar esta prueba")
    @Test fun cargaExitoYUnaConsulta() { }

    // TODO recuperacion: simular error conocido y otra carga correcta.
    @Ignore("Completa TODO recuperacion antes de activar esta prueba")
    @Test fun errorSeRecuperaConOtraCarga() { }
}
