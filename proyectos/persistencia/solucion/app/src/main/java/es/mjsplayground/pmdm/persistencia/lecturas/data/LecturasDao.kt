package es.mjsplayground.pmdm.persistencia.lecturas.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LecturasDao {
    @Transaction
    @Query("SELECT * FROM lecturas ORDER BY lecturaId")
    fun observar(): Flow<List<LecturaCompleta>>

    @Transaction
    @Query("SELECT * FROM autores WHERE autorId = :id")
    suspend fun porAutor(id: Long): AutorConLecturas?

    @Query("""SELECT a.nombre, COUNT(l.lecturaId) AS total,
        SUM(CASE WHEN l.leida = 0 THEN 1 ELSE 0 END) AS pendientes
        FROM autores a LEFT JOIN lecturas l ON a.autorId = l.autorId
        GROUP BY a.autorId, a.nombre ORDER BY a.nombre""")
    fun resumen(): Flow<List<ResumenAutor>>

    @Query("SELECT * FROM lecturas WHERE lecturaId = :id")
    suspend fun buscar(id: Long): Lectura?
    @Upsert suspend fun autores(autores: List<Autor>)
    @Upsert suspend fun lecturas(lecturas: List<Lectura>)
    @Upsert suspend fun etiquetas(etiquetas: List<Etiqueta>)
    @Insert(onConflict = OnConflictStrategy.IGNORE) suspend fun vincular(vinculos: List<LecturaEtiqueta>)
    @Query("UPDATE lecturas SET leida = :leida WHERE lecturaId = :id")
    suspend fun marcar(id: Long, leida: Boolean): Int
}
