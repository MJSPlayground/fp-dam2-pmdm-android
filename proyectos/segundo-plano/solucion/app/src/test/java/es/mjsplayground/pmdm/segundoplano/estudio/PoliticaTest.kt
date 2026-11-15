package es.mjsplayground.pmdm.segundoplano.estudio

import org.junit.Assert.*
import org.junit.Test

class PoliticaTest {
    @Test fun decidePorResultadoNaturalezaYLimite() {
        assertEquals(Decision.COMPLETAR, decidir(true, false, 0, 2))
        assertEquals(Decision.REINTENTAR, decidir(false, true, 0, 2))
        assertEquals(Decision.FALLAR, decidir(false, true, 2, 2))
        assertEquals(Decision.FALLAR, decidir(false, false, 0, 2))
        assertEquals(Decision.FALLAR, decidir(false, true, 0, 0))
    }
    @Test(expected = IllegalArgumentException::class)
    fun rechazaIntentoNegativo() { decidir(false, true, -1, 2) }
}
