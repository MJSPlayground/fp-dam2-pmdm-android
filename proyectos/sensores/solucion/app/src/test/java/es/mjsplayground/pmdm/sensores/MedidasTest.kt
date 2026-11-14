package es.mjsplayground.pmdm.sensores
import org.junit.Assert.*
import org.junit.Test
class MedidasTest {
    @Test fun moduloYUnidades() {
        assertEquals(5.0, magnitud(3f,4f,0f), 0.0001)
        assertEquals(9.81, magnitud(0f,0f,9.81f), 0.001)
        assertEquals(180.0, radianesAGrados(Math.PI.toFloat()), 0.001)
        assertEquals(-90.0, radianesAGrados((-Math.PI/2).toFloat()), 0.001)
    }
    @Test fun entradasNoFinitas() {
        assertThrows(IllegalArgumentException::class.java) { magnitud(Float.NaN,0f,0f) }
        assertThrows(IllegalArgumentException::class.java) { radianesAGrados(Float.POSITIVE_INFINITY) }
    }
}
