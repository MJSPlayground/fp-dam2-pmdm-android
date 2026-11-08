package es.mjsplayground.pmdm.desarrollo.cuaderno.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import es.mjsplayground.pmdm.desarrollo.cuaderno.model.Nota
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotaDaoTest {
    @Test fun roomInsertaObservaActualizaBuscaYElimina() = runBlocking {
        val db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), NotaDatabase::class.java).build()
        try {
            val dao = db.notas()
            assertTrue(dao.observar().first().isEmpty())
            val id = dao.guardar(Nota(titulo = "Compra", contenido = "Fruta"))
            assertTrue(id > 0)
            assertEquals("Compra", dao.buscar(id).first()?.titulo)
            dao.guardar(Nota(id, "Compra actualizada", "Fruta y pan"))
            assertEquals(1, dao.observar().first().size)
            assertEquals("Fruta y pan", dao.buscar(id).first()?.contenido)
            dao.eliminar(Nota(id, "Compra actualizada", "Fruta y pan"))
            assertNull(dao.buscar(id).first())
            assertTrue(dao.observar().first().isEmpty())
        } finally { db.close() }
    }
}
