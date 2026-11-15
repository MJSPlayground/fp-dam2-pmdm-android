package es.mjsplayground.pmdm.cartografia

import org.junit.Assert.*
import org.junit.Test

class GeometriaTest {
    @Test fun mismoPunto() { assertEquals(0.0, Geometria.distancia(Geometria.centro, Geometria.centro), 0.001) }
    @Test fun distanciaConocida() {
        val madrid = Punto(-3.7038, 40.4168)
        val londres = Punto(-0.1276, 51.5072)
        assertEquals(1263000.0, Geometria.distancia(madrid, londres), 3000.0)
        assertEquals(Geometria.distancia(madrid, londres), Geometria.distancia(londres, madrid), 0.001)
    }
    @Test(expected = IllegalArgumentException::class) fun coordenadaInvalida() { Punto(0.0, 91.0) }
    @Test(expected = IllegalArgumentException::class) fun coordenadaNoFinita() { Punto(Double.NaN, 0.0) }
    @Test fun clasificacionConIncertidumbre() {
        assertEquals(Zona.DENTRO, Geometria.clasificar(490.0, 10.0, 500.0))
        assertEquals(Zona.INCIERTA, Geometria.clasificar(500.0, 10.0, 500.0))
        assertEquals(Zona.INCIERTA, Geometria.clasificar(510.0, 10.0, 500.0))
        assertEquals(Zona.FUERA, Geometria.clasificar(511.0, 10.0, 500.0))
        assertEquals(Zona.INCIERTA, Geometria.clasificar(10.0, 800.0, 500.0))
    }
    @Test(expected = IllegalArgumentException::class) fun precisionNegativa() {
        Geometria.clasificar(20.0, -1.0, 500.0)
    }
}
