package es.mjsplayground.pmdm.diseno.galeria

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import org.junit.Assert.*
import org.junit.Test

class GaleriaTest {
    @Test fun clasesDeAncho() {
        assertEquals(1, columnas(WindowWidthSizeClass.Compact))
        assertEquals(2, columnas(WindowWidthSizeClass.Medium))
        assertEquals(3, columnas(WindowWidthSizeClass.Expanded))
    }
    @Test fun categoriasYFavoritosSeCombinan() {
        assertEquals(listOf(1, 2), seleccionar(laminas, R.string.naturaleza, false, emptySet()).map { it.id })
        assertEquals(listOf(2), seleccionar(laminas, R.string.naturaleza, true, setOf(2, 3)).map { it.id })
        assertTrue(seleccionar(laminas, null, true, emptySet()).isEmpty())
        assertEquals(4, seleccionar(laminas, null, false, emptySet()).size)
    }
    @Test fun alternarNoModificaEntrada() {
        val entrada = setOf(1)
        assertEquals(setOf(1, 2), alternar(entrada, 2))
        assertEquals(emptySet<Int>(), alternar(entrada, 1))
        assertEquals(setOf(1), entrada)
    }
    @Test fun favoritosPrimeroSinDuplicados() {
        val resultado = ordenarSeleccion(laminas.reversed() + laminas.first(), setOf(2, 4))
        assertEquals(listOf(2, 4, 1, 3), resultado.map { it.id })
    }
    @Test fun contrasteSimetricoYExtremos() {
        assertEquals(21.0, contraste(0x000000, 0xFFFFFF), .0001)
        assertEquals(1.0, contraste(0x2E7D32, 0x2E7D32), .0001)
        assertEquals(contraste(0x2E7D32, 0xFFFFFF), contraste(0xFFFFFF, 0x2E7D32), .0001)
        assertTrue(contraste(0x2E7D32, 0xFFFFFF) >= 4.5)
    }
}
