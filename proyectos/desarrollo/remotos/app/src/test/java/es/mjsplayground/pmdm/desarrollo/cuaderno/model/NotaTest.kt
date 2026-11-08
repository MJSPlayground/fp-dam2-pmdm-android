package es.mjsplayground.pmdm.desarrollo.cuaderno.model

import es.mjsplayground.pmdm.desarrollo.cuaderno.data.NotaNoValidaException
import es.mjsplayground.pmdm.desarrollo.cuaderno.data.UrlNoValidaException
import es.mjsplayground.pmdm.desarrollo.cuaderno.data.PublicacionDto
import es.mjsplayground.pmdm.desarrollo.cuaderno.data.aNota
import es.mjsplayground.pmdm.desarrollo.cuaderno.data.validarUrl
import es.mjsplayground.pmdm.desarrollo.cuaderno.ui.Fallo
import es.mjsplayground.pmdm.desarrollo.cuaderno.ui.aFallo
import org.junit.Assert.*
import org.junit.Test

class NotaTest {
    @Test fun validaLosLimitesDelTituloYRecortaEspacios() {
        assertFalse(esNotaValida("ab", "texto"))
        assertTrue(esNotaValida(" abc ", "texto"))
        assertTrue(esNotaValida("a".repeat(80), "texto"))
        assertFalse(esNotaValida("a".repeat(81), "texto"))
        assertFalse(esNotaValida("   ", "texto"))
        assertFalse(esNotaValida("válido", " \n\t"))
    }

    @Test fun convierteUnaPublicacionEnUnaCopiaLocalSinIdRemoto() {
        val nota = PublicacionDto(32, "  " + "a".repeat(90), " contenido ").aNota()
        assertEquals(0L, nota.id)
        assertEquals(80, nota.titulo.length)
        assertEquals("contenido", nota.contenido)
        assertTrue(esNotaValida(nota.titulo, nota.contenido))
    }

    @Test fun urlHttpsYHttpPrivadoSoloDebug() {
        assertEquals("https://example.com/posts?delay=2", validarUrl(" https://example.com/posts?delay=2 ", false))
        assertEquals("http://192.168.1.8:8000/posts", validarUrl("http://192.168.1.8:8000/posts", true))
        assertEquals("http://10.0.2.2:8000/posts", validarUrl("http://10.0.2.2:8000/posts", true))
        assertEquals("http://172.16.0.2/posts", validarUrl("http://172.16.0.2/posts", true))
        assertThrows(UrlNoValidaException::class.java) { validarUrl("http://10.0.2.2/posts", false) }
        assertThrows(UrlNoValidaException::class.java) { validarUrl("http://example.com/posts", true) }
        assertThrows(UrlNoValidaException::class.java) { validarUrl("https://user:pass@example.com/posts", true) }
        assertThrows(UrlNoValidaException::class.java) { validarUrl("sin-url", true) }
        assertThrows(UrlNoValidaException::class.java) { validarUrl("http://172.32.0.1/posts", true) }
    }

    @Test fun diferenciaValidacionDeUrl() {
        assertEquals(Fallo.Validacion, NotaNoValidaException().aFallo())
        assertEquals(Fallo.Url, UrlNoValidaException().aFallo())
    }
}
