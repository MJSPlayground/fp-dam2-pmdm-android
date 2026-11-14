package es.mjsplayground.pmdm.asincronia.buscador

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn

class BuscadorViewModel : ViewModel() {
    private val _consulta = MutableStateFlow("")
    val consulta = _consulta.asStateFlow()
    private val _favoritos = MutableStateFlow(false)
    val favoritos = _favoritos.asStateFlow()
    val resultado = flujoBusqueda(consulta, favoritos).stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), ResultadoBusqueda(cargando = true)
    )

    fun cambiarConsulta(texto: String) { _consulta.value = texto }
    fun cambiarFavoritos(solo: Boolean) { _favoritos.value = solo }
}
