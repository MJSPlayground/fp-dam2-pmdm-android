package es.mjsplayground.pmdm.cartografia

import org.junit.Assert.*
import org.junit.Test

class MetricaRecorridoTest {
    private val a = Punto(-3.7038, 40.4168)
    private val b = Punto(-3.7000, 40.4188)
    private val c = Punto(-3.6900, 40.4200)
    @Test fun sinTramos() {
        assertEquals(0.0, MetricaRecorrido.longitud(emptyList()), 0.001)
        assertEquals(0.0, MetricaRecorrido.longitud(listOf(a)), 0.001)
        assertEquals(0.0, MetricaRecorrido.longitud(listOf(a,a)), 0.001)
    }
    @Test fun sumaSinCerrarAnillo() {
        val total = MetricaRecorrido.longitud(listOf(a,b,c))
        assertEquals(Geometria.distancia(a,b) + Geometria.distancia(b,c), total, 0.001)
        assertTrue(total > Geometria.distancia(a,c))
        assertEquals(total, MetricaRecorrido.longitud(listOf(a,b,b,c)), 0.001)
    }
}
