package es.mjsplayground.pmdm.persistencia.lecturas

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import es.mjsplayground.pmdm.persistencia.lecturas.data.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class PersistenciaTest {
    private val contexto = ApplicationProvider.getApplicationContext<Context>()
    private fun memoria() = Room.inMemoryDatabaseBuilder(contexto, LecturasDatabase::class.java).build()
    private fun fuente(respuesta: suspend () -> List<LecturaDto>) = FuenteRemota(object : LecturasApi {
        override suspend fun consultar() = respuesta()
    })
    @Test fun relacionesUnoMuchosYMuchosMuchos() = runBlocking {
        val db = memoria()
        try {
            val repo = DatosRepository(db, fuente { emptyList() })
            repo.demostracion()
            val autor = db.lecturas().porAutor(100)!!
            assertEquals(2, autor.lecturas.size)
            val fichas = repo.observar().first()
            assertEquals(listOf("Android", "Kotlin"), fichas.first { it.id == 100L }.etiquetas)
            assertEquals(listOf("Android"), fichas.first { it.id == 101L }.etiquetas)
            val resumen = db.lecturas().resumen().first().single()
            assertEquals(2, resumen.total)
            assertEquals(2, resumen.pendientes)
            repo.marcar(100, true)
            assertEquals(1, db.lecturas().resumen().first().single().pendientes)
        } finally { db.close() }
    }
    @Test fun actualizacionConservaEstadoLocalYCacheAnteFallo() = runBlocking {
        val db = memoria()
        try {
            val repo = DatosRepository(db, fuente { listOf(LecturaDto(1, 1, "Remota")) })
            repo.actualizar()
            repo.marcar(1, true)
            repo.guardarRemotas(listOf(LecturaDto(1, 1, "Título actualizado")))
            assertTrue(repo.observar().first().single().leida)
            val sinRed = DatosRepository(db, fuente { throw IOException("Sin red de prueba") })
            try { sinRed.actualizar(); fail("Debía fallar") } catch (_: IOException) { }
            val guardada = sinRed.observar().first().single()
            assertEquals("Título actualizado", guardada.titulo)
            assertTrue(guardada.leida)
        } finally { db.close() }
    }
    @Test fun datastoreConservaPreferenciaEntreLectores() = runBlocking {
        val preferencias = Preferencias(contexto)
        try {
            preferencias.cambiarSoloPendientes(true)
            assertTrue(Preferencias(contexto).soloPendientes.first())
            preferencias.cambiarSoloPendientes(false)
            assertFalse(preferencias.soloPendientes.first())
        } finally { preferencias.cambiarSoloPendientes(false) }
    }
    @Test fun migracionUnoADosConservaFilasYAdmiteRelaciones() = runBlocking {
        val nombre = "migracion-${System.nanoTime()}.db"
        val vieja = contexto.openOrCreateDatabase(nombre, Context.MODE_PRIVATE, null)
        vieja.execSQL("CREATE TABLE autores (autorId INTEGER NOT NULL PRIMARY KEY, nombre TEXT NOT NULL)")
        vieja.execSQL("CREATE TABLE lecturas (lecturaId INTEGER NOT NULL PRIMARY KEY, titulo TEXT NOT NULL, autorId INTEGER NOT NULL, FOREIGN KEY(autorId) REFERENCES autores(autorId) ON UPDATE NO ACTION ON DELETE RESTRICT)")
        vieja.execSQL("CREATE INDEX index_lecturas_autorId ON lecturas (autorId)")
        vieja.execSQL("CREATE TABLE etiquetas (etiquetaId INTEGER NOT NULL PRIMARY KEY, nombre TEXT NOT NULL)")
        vieja.execSQL("CREATE TABLE lectura_etiqueta (lecturaId INTEGER NOT NULL, etiquetaId INTEGER NOT NULL, PRIMARY KEY(lecturaId,etiquetaId), FOREIGN KEY(lecturaId) REFERENCES lecturas(lecturaId) ON UPDATE NO ACTION ON DELETE CASCADE, FOREIGN KEY(etiquetaId) REFERENCES etiquetas(etiquetaId) ON UPDATE NO ACTION ON DELETE CASCADE)")
        vieja.execSQL("CREATE INDEX index_lectura_etiqueta_etiquetaId ON lectura_etiqueta (etiquetaId)")
        vieja.execSQL("INSERT INTO autores VALUES (7,'Autor conservado')")
        vieja.execSQL("INSERT INTO lecturas VALUES (9,'Lectura conservada',7)")
        vieja.version = 1
        vieja.close()
        val nueva = Room.databaseBuilder(contexto, LecturasDatabase::class.java, nombre)
            .addMigrations(LecturasDatabase.MIGRACION_1_2).build()
        try {
            val fila = nueva.lecturas().buscar(9)!!
            assertEquals("Lectura conservada", fila.titulo)
            assertFalse(fila.leida)
            assertEquals(1, nueva.lecturas().porAutor(7)!!.lecturas.size)
            nueva.lecturas().marcar(9, true)
            assertTrue(nueva.lecturas().buscar(9)!!.leida)
        } finally { nueva.close(); contexto.deleteDatabase(nombre) }
    }
}
