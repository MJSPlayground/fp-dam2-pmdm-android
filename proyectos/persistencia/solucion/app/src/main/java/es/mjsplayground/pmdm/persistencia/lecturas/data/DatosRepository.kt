package es.mjsplayground.pmdm.persistencia.lecturas.data

import androidx.room.withTransaction
import es.mjsplayground.pmdm.persistencia.lecturas.domain.FichaLectura
import es.mjsplayground.pmdm.persistencia.lecturas.domain.LecturasRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatosRepository @Inject constructor(private val db: LecturasDatabase,
    private val remoto: FuenteRemota) : LecturasRepository {
    private val dao = db.lecturas()
    override fun observar() = dao.observar().map { filas -> filas.map {
        FichaLectura(it.lectura.lecturaId, it.lectura.titulo, it.autor.nombre,
            it.etiquetas.map(Etiqueta::nombre).sorted(), it.lectura.leida)
    } }
    override suspend fun marcar(id: Long, leida: Boolean) {
        check(dao.marcar(id, leida) == 1) { "Lectura ausente" }
    }
    override suspend fun actualizar() {
        // La red queda fuera de la transacción: si falla, la caché no cambia.
        val recibidas = remoto.consultar()
        guardarRemotas(recibidas)
    }
    suspend fun guardarRemotas(recibidas: List<LecturaDto>) {
        require(recibidas.all { it.id > 0 && it.userId > 0 && it.title.isNotBlank() })
        require(recibidas.map { it.id }.distinct().size == recibidas.size)
        db.withTransaction {
            dao.autores(recibidas.map { Autor(it.userId, "Autor remoto ${it.userId}") }.distinctBy { it.autorId })
            dao.etiquetas(listOf(Etiqueta(1, "API")))
            val actualizadas = recibidas.map { dto ->
                Lectura(dto.id, dto.title.trim(), dto.userId, dao.buscar(dto.id)?.leida ?: false)
            }
            dao.lecturas(actualizadas)
            dao.vincular(recibidas.map { LecturaEtiqueta(it.id, 1) })
        }
    }
    suspend fun demostracion() {
        db.withTransaction {
            dao.autores(listOf(Autor(100, "Biblioteca local")))
            dao.etiquetas(listOf(Etiqueta(10, "Android"), Etiqueta(11, "Kotlin")))
            if (dao.buscar(100) == null) dao.lecturas(listOf(Lectura(100, "Estado y persistencia", 100), Lectura(101, "Diseño de repositorios", 100)))
            dao.vincular(listOf(LecturaEtiqueta(100, 10), LecturaEtiqueta(100, 11), LecturaEtiqueta(101, 10)))
        }
    }
}
