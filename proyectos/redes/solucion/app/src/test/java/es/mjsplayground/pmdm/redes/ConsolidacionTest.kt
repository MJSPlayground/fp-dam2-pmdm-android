package es.mjsplayground.pmdm.redes

import org.junit.Assert.assertEquals
import org.junit.Test

class ConsolidacionTest {
    @Test fun estadoSinCambiarSaludoNiPing() {
        assertEquals("LISTO", Protocolo.responder("ESTADO".toByteArray()))
        assertEquals("OK HOLA", Protocolo.responder("HOLA".toByteArray()))
        assertEquals("PONG", Protocolo.responder("PING".toByteArray()))
    }
}
