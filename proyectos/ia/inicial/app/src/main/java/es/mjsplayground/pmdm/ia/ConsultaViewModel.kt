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

data class ConsultaUi(val cargando:Boolean=false,val respuesta:String="",val simulada:Boolean=true,val mensaje:Int?=null,val imagenPreparada:Boolean=false,val preparando:Boolean=false,val incompleta:Boolean=false)
class ConsultaViewModel(private val generar:(String,String?)->Respuesta = ClienteIa()::generar) : ViewModel() {
    private val _estado=MutableStateFlow(ConsultaUi())
    val estado=_estado.asStateFlow()
    private var imagen:String?=null
    private var tarea:Job?=null
    private var preparacion:Job?=null
    private var versionImagen=0L
    fun quitarImagen() {
        versionImagen++;preparacion?.cancel();imagen=null
        _estado.update{it.copy(imagenPreparada=false,preparando=false)}
    }
    fun preparar(lector:suspend()->String) {
        quitarImagen()
        val version=versionImagen
        _estado.update{it.copy(preparando=true,mensaje=null)}
        preparacion=viewModelScope.launch {
            try {
                val datos=lector()
                if(version==versionImagen){imagen=datos;_estado.update{it.copy(imagenPreparada=true)}}
            } catch(cancelada:CancellationException){throw cancelada
            } catch(_:Exception){if(version==versionImagen)_estado.update{it.copy(mensaje=R.string.error_imagen)}
            } finally {if(version==versionImagen)_estado.update{it.copy(preparando=false)}}
        }
    }
    fun consultar(texto:String) {
        if(_estado.value.preparando)return
        if(!consultaValida(texto)){_estado.update{it.copy(mensaje=R.string.error_consulta)};return}
        tarea?.cancel()
        val adjunto=imagen
        _estado.update{it.copy(cargando=true,respuesta="",mensaje=null)}
        tarea=viewModelScope.launch {
            try {
                val resultado=withContext(Dispatchers.IO){generar(texto,adjunto)}
                _estado.update{it.copy(cargando=false,respuesta=resultado.texto,simulada=resultado.simulada,incompleta=resultado.incompleta)}
            } catch(cancelada:CancellationException){throw cancelada
            } catch(_:Exception){_estado.update{it.copy(cargando=false,mensaje=R.string.error_red)}}
        }
    }
    fun cancelar(){
        // TODO ia-cancelacion: cancelar la tarea y representar abandono sin error.
    }
}
