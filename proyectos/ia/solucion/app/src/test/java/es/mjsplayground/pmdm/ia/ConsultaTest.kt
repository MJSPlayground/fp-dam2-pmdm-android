package es.mjsplayground.pmdm.ia
import org.junit.Assert.*
import org.junit.Test
class ConsultaTest {
 @Test fun limitesYEspacios() {
  assertFalse(consultaValida("  ab  ")); assertTrue(consultaValida(" abc "))
  assertTrue(consultaValida("a".repeat(2000))); assertFalse(consultaValida("a".repeat(2001)))
 }
}
