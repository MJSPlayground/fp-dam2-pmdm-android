package es.mjsplayground.pmdm.desarrollo.cuaderno.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException
import es.mjsplayground.pmdm.desarrollo.cuaderno.data.NotaRepository
import es.mjsplayground.pmdm.desarrollo.cuaderno.data.PublicacionDto
import es.mjsplayground.pmdm.desarrollo.cuaderno.data.UrlNoValidaException
import es.mjsplayground.pmdm.desarrollo.cuaderno.data.NotaNoValidaException
import es.mjsplayground.pmdm.desarrollo.cuaderno.model.Nota

sealed interface Fallo {
    data object Conexion : Fallo
    data object Formato : Fallo
    data object Url : Fallo
    data object Datos : Fallo
    data object Validacion : Fallo
    data class Http(val codigo: Int) : Fallo
}

fun Exception.aFallo(): Fallo = when (this) {
    is HttpException -> Fallo.Http(code())
    is SerializationException -> Fallo.Formato
    is IOException -> Fallo.Conexion
    is UrlNoValidaException -> Fallo.Url
    is NotaNoValidaException -> Fallo.Validacion
    else -> Fallo.Datos
}

data class CuadernoUiState(
    val notas: List<Nota> = emptyList(),
    val cargandoLocal: Boolean = true,
    val errorLocal: Fallo? = null,
    val remotas: List<PublicacionDto> = emptyList(),
    val cargandoRemotas: Boolean = false,
    val remotoConsultado: Boolean = false,
    val errorRemoto: Fallo? = null,
    val guardando: Boolean = false,
    val importada: Boolean = false
)

class CuadernoViewModel(private val repository: NotaRepository) : ViewModel() {
    private val _estado = MutableStateFlow(CuadernoUiState())
    val estado = _estado.asStateFlow()
    private var consulta: Job? = null

    init {
        viewModelScope.launch {
            try {
                repository.observar().collect { notas ->
                    _estado.update { it.copy(notas = notas, cargandoLocal = false) }
                }
            } catch (cancelada: CancellationException) { throw cancelada
            } catch (error: Exception) {
                _estado.update { it.copy(cargandoLocal = false, errorLocal = error.aFallo()) }
            }
        }
    }

    fun guardar(nota: Nota, onGuardado: () -> Unit) {
        if (_estado.value.guardando) return
        viewModelScope.launch {
            _estado.update { it.copy(guardando = true, errorLocal = null) }
            try {
                repository.guardar(nota)
                _estado.update { it.copy(guardando = false) }
                onGuardado()
            } catch (cancelada: CancellationException) { throw cancelada
            } catch (error: Exception) {
                _estado.update { it.copy(guardando = false, errorLocal = error.aFallo()) }
            }
        }
    }

    fun eliminar(nota: Nota, onEliminada: () -> Unit) {
        if (_estado.value.guardando) return
        viewModelScope.launch {
            _estado.update { it.copy(guardando = true, errorLocal = null) }
            try {
                repository.eliminar(nota)
                _estado.update { it.copy(guardando = false) }
                onEliminada()
            } catch (cancelada: CancellationException) { throw cancelada
            } catch (error: Exception) {
                _estado.update { it.copy(guardando = false, errorLocal = error.aFallo()) }
            }
        }
    }

    fun cargarRemotas(url: String) {
        consulta?.cancel()
        consulta = viewModelScope.launch {
            _estado.update { it.copy(cargandoRemotas = true, errorRemoto = null, importada = false) }
            try {
                val notas = repository.consultarRemotas(url)
                _estado.update { it.copy(remotas = notas, cargandoRemotas = false, remotoConsultado = true) }
            } catch (cancelada: CancellationException) { throw cancelada
            } catch (error: Exception) {
                _estado.update { it.copy(cargandoRemotas = false, remotoConsultado = true,
                    remotas = emptyList(), errorRemoto = error.aFallo()) }
            }
        }
    }

    fun importar(publicacion: PublicacionDto) {
        if (_estado.value.guardando) return
        viewModelScope.launch {
            _estado.update { it.copy(guardando = true, errorRemoto = null, importada = false) }
            try {
                repository.importar(publicacion)
                _estado.update { it.copy(guardando = false, importada = true) }
            } catch (cancelada: CancellationException) { throw cancelada
            } catch (error: Exception) {
                _estado.update { it.copy(guardando = false, errorRemoto = error.aFallo()) }
            }
        }
    }
}
