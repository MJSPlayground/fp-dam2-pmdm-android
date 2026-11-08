package es.mjsplayground.pmdm.desarrollo.cuaderno.ejercicios
import org.junit.Assert.assertEquals
import org.junit.Test

class FiltroTitulosTest {
    @Test fun consultaRecortadaIgnoraMayusculasYConservaOrden() {
        val titulos = listOf("Leer Kotlin", "Comprar pan", "leer Android")
        assertEquals(listOf("Leer Kotlin", "leer Android"), filtrarTitulos(titulos, " LEER "))
        assertEquals(3, titulos.size)
    }
    @Test fun vaciaNoExcluyeNingunoYSinCoincidenciaDevuelveVacia() {
        val titulos = listOf("Leer Kotlin", "Comprar pan")
        assertEquals(titulos, filtrarTitulos(titulos, " "))
        assertEquals(emptyList<String>(), filtrarTitulos(titulos, "red"))
        assertEquals(emptyList<String>(), filtrarTitulos(emptyList(), "a"))
    }
}
