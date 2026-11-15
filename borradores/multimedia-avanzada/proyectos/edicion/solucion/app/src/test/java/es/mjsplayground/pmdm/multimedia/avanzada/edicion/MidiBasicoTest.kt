package es.mjsplayground.pmdm.multimedia.avanzada.edicion
import org.junit.Assert.*
import org.junit.Test
class MidiBasicoTest {
    @Test fun mensajesDeNotaConservanCanalYDatos() {
        assertArrayEquals(byteArrayOf(0x92.toByte(), 60, 100), notaMidi(2, 60, 100, true))
        assertArrayEquals(byteArrayOf(0x82.toByte(), 60, 0), notaMidi(2, 60, 0, false))
    }
    @Test fun rechazaCamposFueraDelContrato() {
        assertThrows(IllegalArgumentException::class.java) { notaMidi(16, 60, 100, true) }
        assertThrows(IllegalArgumentException::class.java) { notaMidi(0, 128, 100, true) }
        assertThrows(IllegalArgumentException::class.java) { notaMidi(0, 60, -1, true) }
    }
}
