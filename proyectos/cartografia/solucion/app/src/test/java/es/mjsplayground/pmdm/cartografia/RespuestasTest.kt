package es.mjsplayground.pmdm.cartografia

import org.junit.Assert.*
import org.junit.Test

class RespuestasTest {
    @Test fun busquedaRespetaOrden() {
        assertEquals(Punto(-3.69, 40.42), Respuestas.lugar("""[{"lat":"40.42","lon":"-3.69"}]"""))
    }
    @Test fun busquedaVacia() { assertNull(Respuestas.lugar("[]")) }
    @Test(expected = IllegalArgumentException::class) fun busquedaMalformada() { Respuestas.lugar("no-json") }
    @Test fun rutaGeoJson() {
        val puntos = Respuestas.ruta("""{"code":"Ok","routes":[{"geometry":{"type":"LineString","coordinates":[[-3.7,40.4],[-3.69,40.42]]}}]}""")
        assertEquals(2, puntos.size)
        assertEquals(Punto(-3.7, 40.4), puntos.first())
    }
    @Test(expected = IllegalArgumentException::class) fun sinRuta() { Respuestas.ruta("""{"code":"NoRoute"}""") }
    @Test(expected = IllegalArgumentException::class) fun rutaConCoordenadaInvalida() {
        Respuestas.ruta("""{"code":"Ok","routes":[{"geometry":{"type":"LineString","coordinates":[[2,95],[3,40]]}}]}""")
    }
}
