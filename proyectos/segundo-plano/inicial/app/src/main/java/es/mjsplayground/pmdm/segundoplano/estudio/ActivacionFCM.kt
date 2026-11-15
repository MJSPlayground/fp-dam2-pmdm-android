package es.mjsplayground.pmdm.segundoplano.estudio

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging

/** Solo llamar tras configurar un proyecto real y su registro autenticado de tokens. */
fun activarMensajeria(
    contexto: Context,
    registrar: (String) -> Unit,
    alFallar: (Exception) -> Unit
): Boolean {
    if (FirebaseApp.getApps(contexto).isEmpty()) return false
    val mensajeria = FirebaseMessaging.getInstance()
    mensajeria.isAutoInitEnabled = true
    mensajeria.token.addOnSuccessListener { registrar(it) }.addOnFailureListener { alFallar(it) }
    return true
}
