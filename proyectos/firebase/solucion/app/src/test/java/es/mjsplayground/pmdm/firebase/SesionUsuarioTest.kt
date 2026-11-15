package es.mjsplayground.pmdm.firebase

import org.junit.Assert.*
import org.junit.Test

class SesionUsuarioTest {
    @Test fun resultadosDiferidosNoCruzanCuentasNiUnaSalidaYVuelta() {
        val sesion = SesionUsuario()
        sesion.actualizar("cuenta-a")
        val descargaAnterior = sesion.capturar()
        var pantalla = "datos-b"
        val terminarDescarga = { if (sesion.vigente(descargaAnterior, "cuenta-b")) pantalla = "datos-a" }
        sesion.actualizar("cuenta-b")
        terminarDescarga()
        assertEquals("datos-b", pantalla)
        sesion.actualizar(null)
        sesion.actualizar("cuenta-a")
        assertFalse(sesion.vigente(descargaAnterior, "cuenta-a"))
    }
    @Test fun volverDelSelectorConMismaIdentidadNoInvalidaLaSeleccion() {
        val sesion = SesionUsuario()
        sesion.actualizar("cuenta-a")
        val seleccion = sesion.capturar()
        sesion.actualizar("cuenta-a")
        assertTrue(sesion.vigente(seleccion, "cuenta-a"))
        assertFalse(sesion.vigente(seleccion, "cuenta-b"))
        val recreada = SesionUsuario()
        recreada.restaurar(seleccion!!, "cuenta-a")
        recreada.actualizar("cuenta-a")
        assertTrue(recreada.vigente(seleccion, "cuenta-a"))
    }
}
