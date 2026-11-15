package es.mjsplayground.pmdm.persistencia.lecturas.domain

import kotlinx.coroutines.flow.Flow

data class FichaLectura(val id: Long, val titulo: String, val autor: String,
    val etiquetas: List<String>, val leida: Boolean)
interface LecturasRepository {
    fun observar(): Flow<List<FichaLectura>>
    suspend fun actualizar()
    suspend fun marcar(id: Long, leida: Boolean)
}
