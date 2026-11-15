package es.mjsplayground.pmdm.ia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import es.mjsplayground.pmdm.ia.ui.PrimeraAplicacionTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState);enableEdgeToEdge()
        setContent {PrimeraAplicacionTheme{
            val vm:ConsultaViewModel=viewModel()
            val estado by vm.estado.collectAsStateWithLifecycle()
            var pregunta by rememberSaveable{mutableStateOf("")}
            val selector=rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()){uri->
                if(uri!=null)vm.preparar {withContext(Dispatchers.IO){prepararImagen(applicationContext,uri)}}
            }
            Scaffold(Modifier.fillMaxSize().imePadding()){margen->
                Column(Modifier.padding(margen).padding(16.dp).verticalScroll(rememberScrollState()),verticalArrangement=Arrangement.spacedBy(12.dp)){
                    Text(stringResource(R.string.app_name))
                    Text(stringResource(R.string.privacidad))
                    OutlinedTextField(pregunta,{pregunta=it},label={Text(stringResource(R.string.pregunta))})
                    Button(onClick={selector.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))},enabled=!estado.cargando){Text(stringResource(R.string.seleccionar))}
                    Text(stringResource(if(estado.imagenPreparada)R.string.imagen_lista else R.string.sin_imagen))
                    if(estado.preparando)Text(stringResource(R.string.preparando))
                    Button(onClick=vm::quitarImagen,enabled=(estado.imagenPreparada||estado.preparando)&&!estado.cargando){Text(stringResource(R.string.quitar))}
                    Button(onClick={vm.consultar(pregunta)},enabled=!estado.cargando&&!estado.preparando&&consultaValida(pregunta)){Text(stringResource(R.string.consultar))}
                    if(estado.cargando){CircularProgressIndicator();Button(onClick=vm::cancelar){Text(stringResource(R.string.cancelar))}}
                    estado.mensaje?.let{Text(stringResource(it))}
                    if(estado.respuesta.isNotEmpty()){
                        Text(stringResource(if(estado.simulada)R.string.simulada else R.string.real))
                        if(estado.incompleta)Text(stringResource(R.string.parcial))
                        Text(estado.respuesta)
                        Text(stringResource(R.string.verificar))
                    }
                }
            }
        }}
    }
}
