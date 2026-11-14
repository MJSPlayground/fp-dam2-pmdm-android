package es.mjsplayground.pmdm.firebase

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata

const val LIMITE_IMAGEN = 1024 * 1024
fun leerAcotado(entrada: java.io.InputStream, limite: Int = LIMITE_IMAGEN): ByteArray {
    val buffer = ByteArray(limite + 1)
    var total = 0
    while (total < buffer.size) {
        val cantidad = entrada.read(buffer, total, buffer.size - total)
        if (cantidad < 0) break
        if (cantidad == 0) continue
        total += cantidad
    }
    return buffer.copyOf(total)
}
fun tituloValido(titulo: String): Boolean = titulo.trim().length in 3..80

data class NotaNube(val id: String, val titulo: String)

/** Solo Emulator Suite. No hay configuración, claves ni operaciones de un proyecto de producción. */
class NubeRepository(context: Context) {
    private val app = FirebaseApp.getApps(context).firstOrNull { it.name == "laboratorio" }
        ?: FirebaseApp.initializeApp(context, FirebaseOptions.Builder()
            .setProjectId("demo-pmdm")
            .setApplicationId("1:123456789:android:demo")
            .setApiKey("demo-api-key")
            .setStorageBucket("demo-pmdm.appspot.com").build(), "laboratorio")
    val auth: FirebaseAuth = FirebaseAuth.getInstance(app)
    val db: FirebaseFirestore = FirebaseFirestore.getInstance(app)
    private val storage = FirebaseStorage.getInstance(app)
    init {
        // Se configuran antes de cualquier operación. Crear Repository una vez por proceso.
        auth.useEmulator("10.0.2.2", 9099)
        db.useEmulator("10.0.2.2", 8085)
        storage.useEmulator("10.0.2.2", 9199)
    }
    private fun notas() = db.collection("usuarios").document(requireNotNull(auth.currentUser).uid).collection("notas")
    fun observar(resultado: (List<NotaNube>?, Exception?) -> Unit): ListenerRegistration =
        notas().orderBy("titulo").addSnapshotListener { datos, error ->
            if (error != null) resultado(null,error)
            else resultado(datos?.documents.orEmpty().map { NotaNube(it.id,it.getString("titulo").orEmpty()) },null)
        }
    fun agregar(titulo: String, termina: (Boolean) -> Unit) {
        if (!tituloValido(titulo)) { termina(false); return }
        notas().add(mapOf("titulo" to titulo.trim())).addOnCompleteListener { termina(it.isSuccessful) }
    }
    fun eliminar(id: String, termina: (Boolean) -> Unit) {
        notas().document(id).delete().addOnCompleteListener { termina(it.isSuccessful) }
    }
    fun subir(bytes: ByteArray, mime: String, termina: (Boolean) -> Unit) {
        if (bytes.isEmpty() || bytes.size > LIMITE_IMAGEN || mime !in setOf("image/png","image/jpeg")) { termina(false); return }
        val uid = requireNotNull(auth.currentUser).uid
        val metadata = StorageMetadata.Builder().setContentType(mime).build()
        storage.reference.child("usuarios/$uid/imagen").putBytes(bytes,metadata)
            .addOnCompleteListener { termina(it.isSuccessful) }
    }
    fun descargar(termina: (ByteArray?) -> Unit) {
        val uid = requireNotNull(auth.currentUser).uid
        storage.reference.child("usuarios/$uid/imagen").getBytes(LIMITE_IMAGEN.toLong())
            .addOnSuccessListener { termina(it) }.addOnFailureListener { termina(null) }
    }
}
