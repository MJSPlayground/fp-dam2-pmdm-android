package es.mjsplayground.pmdm.desarrollo.cuaderno.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import es.mjsplayground.pmdm.desarrollo.cuaderno.data.NotaRepository
import es.mjsplayground.pmdm.desarrollo.cuaderno.model.Nota
import es.mjsplayground.pmdm.desarrollo.cuaderno.navigation.Detalle

data class DetalleUiState(val cargando: Boolean = true, val nota: Nota? = null, val fallo: Boolean = false)

class DetalleViewModel(repository: NotaRepository, savedStateHandle: SavedStateHandle) : ViewModel() {
    private val id = savedStateHandle.toRoute<Detalle>().id
    val estado = repository.buscar(id)
        .map { DetalleUiState(cargando = false, nota = it) }
        .catch { emit(DetalleUiState(cargando = false, fallo = true)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DetalleUiState())
}
