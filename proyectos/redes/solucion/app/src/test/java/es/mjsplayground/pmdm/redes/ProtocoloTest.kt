package es.mjsplayground.pmdm.redes

import org.junit.Assert.*
import org.junit.Test

class ProtocoloTest {
    @Test fun saludo() { assertEquals("OK HOLA", Protocolo.responder("HOLA".toByteArray())) }
    @Test fun desconocido() { assertEquals("ERROR", Protocolo.responder("OTRO".toByteArray())) }
    @Test(expected = IllegalArgumentException::class) fun limiteDeTrama() { Protocolo.responder(ByteArray(21)) }
    @Test fun ping() { assertEquals("PONG", Protocolo.responder("PING".toByteArray())) }
    @Test fun pendientesConservanOrden() { 
        val datos = listOf(Mensaje(1,"Uno",false), Mensaje(2,"Dos",true), Mensaje(3,"Tres",false))
        assertEquals(listOf(1L,3L), Bandeja.pendientes(datos).map { it.id })
        assertTrue(Bandeja.pendientes(emptyList()).isEmpty())
    }
}
