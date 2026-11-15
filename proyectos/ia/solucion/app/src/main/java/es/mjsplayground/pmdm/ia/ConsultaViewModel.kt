package es.mjsplayground.pmdm.ia

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ConsultaUi(val cargando:Boolean=false,val respuesta:String="",val simulada:Boolean=true,val mensaje:Int?=null,val imagenPreparada:Boolean=false)
class ConsultaViewModel : ViewModel() {
    private val _estado=MutableStateFlow(ConsultaUi())
    val estado=_estado.asStateFlow()
    private var imagen:String?=null
    private var tarea:Job?=null
    fun imagen(datos:String?) {imagen=datos;_estado.update{it.copy(imagenPreparada=datos!=null)}}
    fun falloImagen(){_estado.update{it.copy(mensaje=R.string.error_imagen)}}
    fun consultar(texto:String) {
        if(!consultaValida(texto)){_estado.update{it.copy(mensaje=R.string.error_consulta)};return}
        tarea?.cancel()
        val adjunto=imagen
        _estado.update{it.copy(cargando=true,respuesta="",mensaje=null)}
        tarea=viewModelScope.launch {
            try {
                val resultado=withContext(Dispatchers.IO){ClienteIa().generar(texto,adjunto)}
                _estado.update{it.copy(cargando=false,respuesta=resultado.texto,simulada=resultado.simulada)}
            } catch(cancelada:CancellationException){throw cancelada
            } catch(_:Exception){_estado.update{it.copy(cargando=false,mensaje=R.string.error_red)}}
        }
    }
    fun cancelar(){tarea?.cancel();_estado.update{it.copy(cargando=false,mensaje=R.string.cancelada)}}
}
