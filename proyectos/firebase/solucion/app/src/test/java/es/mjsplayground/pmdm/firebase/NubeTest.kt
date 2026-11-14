package es.mjsplayground.pmdm.firebase

import org.junit.Assert.*
import org.junit.Test

class NubeTest {
    @Test fun tituloConLimites() {
        assertFalse(tituloValido(" ab "))
        assertTrue(tituloValido(" abc "))
        assertTrue(tituloValido("a".repeat(80)))
        assertFalse(tituloValido("a".repeat(81)))
    }
    @Test fun lecturaNuncaCreceSinLimite() {
        assertEquals(11, leerAcotado(ByteArray(100).inputStream(), 10).size)
        assertEquals(0, leerAcotado(ByteArray(0).inputStream(), 10).size)
        assertEquals(5, leerAcotado(ByteArray(5).inputStream(), 10).size)
    }
}
