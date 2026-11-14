package es.mjsplayground.pmdm.multimedia.imagenes

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ImagenUiState(
    val bitmap: Bitmap? = null, val cargando: Boolean = false,
    val resultado: ResultadoExportacion? = null, val mensaje: Int? = null
)

class ImagenViewModel(private val procesador: ProcesadorImagen) : ViewModel() {
    private val _estado = MutableStateFlow(ImagenUiState())
    val estado = _estado.asStateFlow()

    fun mensaje(recurso: Int) { _estado.update { it.copy(mensaje = recurso, cargando = false) } }

    fun cargar(uri: Uri) = ejecutar {
        val bitmap = withContext(Dispatchers.IO) { procesador.cargar(uri) }
        _estado.update { it.copy(bitmap = bitmap, resultado = null, mensaje = null) }
    }

    fun rotar() {
        val bitmap = _estado.value.bitmap ?: return
        ejecutar {
            val nueva = withContext(Dispatchers.Default) { procesador.rotar90(bitmap) }
            _estado.update { it.copy(bitmap = nueva, resultado = null, mensaje = null) }
        }
    }

    fun exportar(formato: Formato) {
        val bitmap = _estado.value.bitmap ?: return
        ejecutar {
            val resultado = withContext(Dispatchers.IO) { procesador.exportar(bitmap, formato) }
            _estado.update { it.copy(resultado = resultado, mensaje = R.string.guardada) }
        }
    }

    fun prepararCaptura(onLista: (Uri) -> Unit) = ejecutar {
        val uri = withContext(Dispatchers.IO) { procesador.prepararCaptura() }
        onLista(uri)
    }

    private fun ejecutar(accion: suspend () -> Unit) {
        if (_estado.value.cargando) return
        _estado.update { it.copy(cargando = true, mensaje = null) }
        viewModelScope.launch {
            try { accion()
            } catch (cancelada: CancellationException) { throw cancelada
            } catch (_: OutOfMemoryError) { mensaje(R.string.error_memoria)
            } catch (_: Exception) { mensaje(R.string.error_imagen)
            } finally { _estado.update { it.copy(cargando = false) } }
        }
    }
}

