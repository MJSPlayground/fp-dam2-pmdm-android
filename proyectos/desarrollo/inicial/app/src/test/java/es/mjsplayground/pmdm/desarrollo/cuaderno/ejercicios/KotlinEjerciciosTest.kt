package es.mjsplayground.pmdm.desarrollo.cuaderno.ejercicios

import org.junit.Assert.assertEquals
import org.junit.Test

@org.junit.Ignore("Activa al completar los TODO de KotlinEjercicios.kt")
class KotlinEjerciciosTest {
    @Test fun etiquetaTrataAusenciaEspaciosYNombre() {
        assertEquals("Sin nombre", etiqueta(null))
        assertEquals("Sin nombre", etiqueta("   "))
        assertEquals("Ane", etiqueta(" Ane "))
    }
    @Test fun pendientesNoModificaLaColeccionYConservaElOrden() {
        val entrada = listOf(Tarea("Leer"), Tarea("Entregar", true), Tarea("Revisar"))
        assertEquals(listOf("Leer", "Revisar"), pendientes(entrada))
        assertEquals(3, entrada.size)
        assertEquals(emptyList<String>(), pendientes(emptyList()))
    }
    @Test fun resumenCuentaSoloPendientes() {
        assertEquals("Pendientes: 0", resumen(listOf(Tarea("Hecha", true))))
        assertEquals("Pendientes: 2", resumen(listOf(Tarea("Leer"), Tarea("Probar"))))
    }
    @Test fun clasificarCompruebaFronterasYValoresInvalidos() {
        assertEquals("Inválida", clasificar(-1))
        assertEquals("Revisar", clasificar(0))
        assertEquals("Revisar", clasificar(4))
        assertEquals("Superada", clasificar(5))
        assertEquals("Superada", clasificar(10))
        assertEquals("Inválida", clasificar(11))
    }
}
