package es.mjsplayground.pmdm.calidad.revision

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class Nota(val id: Long, val titulo: String)
fun tituloValido(titulo: String): Boolean = titulo.trim().length in 3..80

interface NotaRepository { suspend fun obtener(): List<Nota> }

class DemoRepository : NotaRepository {
    override suspend fun obtener(): List<Nota> {
        delay(300)
        return listOf(Nota(1, "Revisar navegación"), Nota(2, "Comprobar persistencia"))
    }
}

data class RevisionState(
    val notas: List<Nota> = emptyList(), val cargando: Boolean = false, val error: Boolean = false
)

class RevisionViewModel(private val repositorio: NotaRepository = DemoRepository()) : ViewModel() {
    private val _estado = MutableStateFlow(RevisionState())
    val estado = _estado.asStateFlow()
    private var carga: Job? = null

    init { cargar() }

    fun cargar() {
        carga?.cancel()
        carga = viewModelScope.launch {
            _estado.value = RevisionState(cargando = true)
            try {
                _estado.value = RevisionState(notas = repositorio.obtener())
            } catch (cancelada: CancellationException) {
                throw cancelada
            } catch (_: IllegalStateException) {
                _estado.value = RevisionState(error = true)
            }
        }
    }
}
