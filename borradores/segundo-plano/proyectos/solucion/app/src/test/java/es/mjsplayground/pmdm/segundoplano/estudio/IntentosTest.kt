package es.mjsplayground.pmdm.segundoplano.estudio

import org.junit.Assert.*
import org.junit.Test

class IntentosTest {
    @Test fun acotaFallosYTerminaElReintento() {
        assertTrue(debeReintentar(0, 1))
        assertFalse(debeReintentar(1, 1))
        assertFalse(debeReintentar(0, -1))
        assertFalse(debeReintentar(3, 100))
    }
}
