package es.mjsplayground.pmdm.persistencia.lecturas.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Autor::class, Lectura::class, Etiqueta::class, LecturaEtiqueta::class],
    version = 2, exportSchema = true)
abstract class LecturasDatabase : RoomDatabase() {
    abstract fun lecturas(): LecturasDao
    companion object {
        val MIGRACION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // TODO migracion-lectura: añade leida sin borrar las filas de la versión anterior.
            }
        }
    }
}
