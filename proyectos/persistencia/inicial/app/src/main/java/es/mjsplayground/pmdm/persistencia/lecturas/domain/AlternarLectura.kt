package es.mjsplayground.pmdm.persistencia.lecturas.domain

import javax.inject.Inject

/** Regla reutilizable por lista, detalle o una futura acción de accesibilidad. */
class AlternarLectura @Inject constructor(private val repository: LecturasRepository) {
    suspend operator fun invoke(ficha: FichaLectura) {
        require(ficha.id > 0)
        repository.marcar(ficha.id, !ficha.leida)
    }
}
