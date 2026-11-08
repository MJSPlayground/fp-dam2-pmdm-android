package es.mjsplayground.pmdm.desarrollo.cuaderno.data

import androidx.room.Database
import androidx.room.RoomDatabase
import es.mjsplayground.pmdm.desarrollo.cuaderno.model.Nota

@Database(entities = [Nota::class], version = 1, exportSchema = true)
abstract class NotaDatabase : RoomDatabase() {
    abstract fun notas(): NotaDao
}
