package es.mjsplayground.pmdm.persistencia.lecturas.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.mjsplayground.pmdm.persistencia.lecturas.data.DatosRepository
import es.mjsplayground.pmdm.persistencia.lecturas.data.Preferencias
import es.mjsplayground.pmdm.persistencia.lecturas.domain.AlternarLectura
import es.mjsplayground.pmdm.persistencia.lecturas.domain.FichaLectura
import es.mjsplayground.pmdm.persistencia.lecturas.domain.LecturasRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class Pantalla(val fichas: List<FichaLectura> = emptyList(), val soloPendientes: Boolean = false,
    val cargando: Boolean = true, val ocupada: Boolean = false, val error: Boolean = false)
@HiltViewModel
class LecturasViewModel @Inject constructor(repository: LecturasRepository,
    private val datos: DatosRepository, private val preferencias: Preferencias,
    private val alternar: AlternarLectura) : ViewModel() {
    private val operacion = MutableStateFlow(false)
    private val error = MutableStateFlow(false)
    val pantalla = combine(repository.observar(), preferencias.soloPendientes, operacion, error) { fichas, filtro, ocupada, fallo ->
        Pantalla(if (filtro) fichas.filterNot { it.leida } else fichas, filtro, false, ocupada, fallo)
    }.catch { emit(Pantalla(cargando = false, error = true)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Pantalla())
    fun cambiarFiltro(valor: Boolean) = ejecutar { preferencias.cambiarSoloPendientes(valor) }
    fun alternar(ficha: FichaLectura) = ejecutar { alternar.invoke(ficha) }
    fun actualizar() = ejecutar { datos.actualizar() }
    fun demostracion() = ejecutar { datos.demostracion() }
    private fun ejecutar(accion: suspend () -> Unit) {
        if (operacion.value) return
        operacion.value = true
        error.value = false
        viewModelScope.launch {
            try { accion() }
            catch (cancelada: CancellationException) { throw cancelada }
            catch (_: Exception) { error.value = true }
            finally { operacion.value = false }
        }
    }
}
