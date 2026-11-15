package es.mjsplayground.pmdm.firebase

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import es.mjsplayground.pmdm.firebase.ui.PrimeraAplicacionTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    // La Activity no reconstruye Firebase al recomponer ni al girar.
    private val repo by lazy { (application as LaboratorioApplication).repo }
    private var registro: ListenerRegistration? = null
    private var usuario by mutableStateOf<String?>(null)
    private val conectado get() = usuario != null
    private var generacion = 0L
    private var ocupada by mutableStateOf(false)
    private var notas by mutableStateOf(emptyList<NotaNube>())
    private var aviso by mutableStateOf(R.string.inicio)
    private var imagen by mutableStateOf<android.graphics.Bitmap?>(null)
    private val authListener = FirebaseAuth.AuthStateListener { auth ->
        generacion++
        registro?.remove(); registro = null; notas = emptyList(); imagen = null
        usuario = auth.currentUser?.uid
        ocupada = false
        aviso = if (usuario == null) R.string.inicio else R.string.sesion
        val uid = usuario
        val version = generacion
        if (uid != null) registro = repo.observar { lista, error ->
            if (vigente(uid, version)) {
                if (error != null) aviso = R.string.error_datos else notas = lista.orEmpty()
            }
        }
    }
    private fun vigente(uid: String?, version: Long) =
        uid != null && uid == usuario && uid == repo.auth.currentUser?.uid && version == generacion
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); enableEdgeToEdge()
        setContent { PrimeraAplicacionTheme {
            var correo by remember { mutableStateOf("") }
            var clave by remember(usuario) { mutableStateOf("") }
            var titulo by remember(usuario) { mutableStateOf("") }
            val selector = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                val uid = usuario
                val version = generacion
                if (uri != null && uid != null) lifecycleScope.launch {
                    ocupada = true
                    aviso = R.string.trabajando
                    val bytes = withContext(Dispatchers.IO) { runCatching {
                        contentResolver.openInputStream(uri)?.use { leerAcotado(it) }
                    }.getOrNull() }
                    if (!vigente(uid, version)) return@launch
                    val mime = contentResolver.getType(uri).orEmpty()
                    if (bytes == null) {aviso = R.string.error_imagen;ocupada=false}
                    else repo.subir(bytes,mime) {
                        if (vigente(uid, version)) {aviso = if(it) R.string.subida else R.string.error_imagen;ocupada=false}
                    }
                }
            }
            Scaffold(Modifier.fillMaxSize()) { margen ->
                Column(Modifier.padding(margen).padding(16.dp).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(stringResource(R.string.app_name))
                    Text(stringResource(R.string.solo_local))
                    Text(stringResource(aviso))
                    if (!BuildConfig.DEBUG) Text(stringResource(R.string.release_bloqueado))
                    else if (!conectado) {
                        OutlinedTextField(correo,{correo=it},label={Text(stringResource(R.string.correo))})
                        OutlinedTextField(clave,{clave=it},label={Text(stringResource(R.string.clave))},
                            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation())
                        Button(onClick={
                            ocupada=true
                            aviso=R.string.trabajando
                            repo.auth.createUserWithEmailAndPassword(correo,clave).addOnCompleteListener {
                                aviso=if(it.isSuccessful) R.string.sesion else R.string.error_sesion
                                ocupada=false
                            }
                        },enabled=!ocupada&&correo.isNotBlank()&&clave.length>=6) {Text(stringResource(R.string.crear))}
                        Button(onClick={
                            ocupada=true
                            aviso=R.string.trabajando
                            repo.auth.signInWithEmailAndPassword(correo,clave).addOnCompleteListener {
                                aviso=if(it.isSuccessful) R.string.sesion else R.string.error_sesion
                                ocupada=false
                            }
                        },enabled=!ocupada&&correo.isNotBlank()&&clave.isNotEmpty()) {Text(stringResource(R.string.entrar))}
                    } else {
                        Button(onClick={repo.auth.signOut();clave="";aviso=R.string.inicio}) {Text(stringResource(R.string.salir))}
                        OutlinedTextField(titulo,{titulo=it},label={Text(stringResource(R.string.titulo))})
                        Button(onClick={
                            val uid=usuario; val version=generacion
                            ocupada=true
                            repo.agregar(titulo){ok->
                                if(vigente(uid,version)) {ocupada=false;aviso=if(ok)R.string.guardada else R.string.error_datos;if(ok)titulo=""}
                            }
                        },enabled=!ocupada&&tituloValido(titulo)) {Text(stringResource(R.string.guardar))}
                        if(notas.isEmpty())Text(stringResource(R.string.vacia))
                        notas.forEach { nota ->
                            Text(nota.titulo)
                            Button(onClick={
                                val uid=usuario; val version=generacion
                                ocupada=true
                                repo.eliminar(nota.id){if(vigente(uid,version)){ocupada=false;aviso=if(it)R.string.eliminada else R.string.error_datos}}
                            },enabled=!ocupada) {Text(stringResource(R.string.eliminar))}
                        }
                        Button(onClick={selector.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))},enabled=!ocupada) {Text(stringResource(R.string.subir))}
                        Button(onClick={
                            val uid=usuario; val version=generacion
                            ocupada=true
                            aviso=R.string.trabajando
                            repo.descargar { bytes ->
                                if(!vigente(uid,version)) return@descargar
                                lifecycleScope.launch {
                                    val nueva = withContext(Dispatchers.Default) { bytes?.let { datos ->
                                        // Límites de píxeles antes de decodificar; bytes acotados no bastan.
                                        val opciones=BitmapFactory.Options().apply{inJustDecodeBounds=true}
                                        BitmapFactory.decodeByteArray(datos,0,datos.size,opciones)
                                        if(opciones.outWidth !in 1..4096 || opciones.outHeight !in 1..4096) null
                                        else BitmapFactory.decodeByteArray(datos,0,datos.size,BitmapFactory.Options().apply{inSampleSize=4})
                                    } }
                                    if(vigente(uid,version)) {
                                        ocupada=false
                                        imagen=nueva
                                        aviso=if(imagen==null) R.string.error_imagen else R.string.descargada
                                    } else nueva?.recycle()
                                }
                            }
                        },enabled=!ocupada) {Text(stringResource(R.string.descargar))}
                        imagen?.let {Image(it.asImageBitmap(),stringResource(R.string.imagen),Modifier.fillMaxWidth().heightIn(max=240.dp),contentScale=ContentScale.Fit)}
                    }
                }
            }
        } }
    }
    override fun onStart() {super.onStart();if(BuildConfig.DEBUG)repo.auth.addAuthStateListener(authListener)}
    override fun onStop() {
        generacion++
        registro?.remove();registro=null
        if(BuildConfig.DEBUG)repo.auth.removeAuthStateListener(authListener)
        super.onStop()
    }
}
