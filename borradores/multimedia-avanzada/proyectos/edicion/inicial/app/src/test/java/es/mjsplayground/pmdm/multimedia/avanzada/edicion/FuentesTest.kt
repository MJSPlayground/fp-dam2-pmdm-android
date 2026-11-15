package es.mjsplayground.pmdm.multimedia.avanzada.edicion
import org.junit.Assert.*
import org.junit.Test
class FuentesTest {
    @Test fun aceptaContratosYRechazaCredencialesYEsquemasAjenos() {
        assertEquals("https://example.org/propio.m3u8", validarFuente(" https://example.org/propio.m3u8 "))
        assertEquals("rtsp://192.168.1.20/propio", validarFuente("rtsp://192.168.1.20/propio"))
        assertThrows(IllegalArgumentException::class.java) { validarFuente("file:///sdcard/privado") }
        assertThrows(IllegalArgumentException::class.java) { validarFuente("https://user:secret@example.org/video") }
    }
}
