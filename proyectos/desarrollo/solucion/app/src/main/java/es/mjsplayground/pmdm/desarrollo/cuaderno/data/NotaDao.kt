package es.mjsplayground.pmdm.desarrollo.cuaderno.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import es.mjsplayground.pmdm.desarrollo.cuaderno.model.Nota

@Dao
interface NotaDao {
    @Query("SELECT * FROM notas ORDER BY id DESC")
    fun observar(): Flow<List<Nota>>

    @Query("SELECT * FROM notas WHERE id = :id")
    fun buscar(id: Long): Flow<Nota?>

    @Upsert
    suspend fun guardar(nota: Nota): Long

    @Delete
    suspend fun eliminar(nota: Nota)
}
