package es.mjsplayground.pmdm.calidad.revision

import org.junit.Assert.*
import org.junit.Test

class VersionesTest {
    private val instalada = VersionInstalable("es.ejemplo.notas", "certificado-a", 8, "1.0")
    @Test fun requiereIdentidadFirmaYCodigoMayor() {
        assertTrue(candidataCompatible(instalada, instalada.copy(codigo = 9, nombre = "1.1")))
        assertFalse(candidataCompatible(instalada, instalada.copy(codigo = 8, nombre = "2.0")))
        assertFalse(candidataCompatible(instalada, instalada.copy(codigo = 7)))
        assertFalse(candidataCompatible(instalada, instalada.copy(codigo = 9, paquete = "es.otro.notas")))
        assertFalse(candidataCompatible(instalada, instalada.copy(codigo = 9, huellaFirma = "certificado-b")))
    }
}
