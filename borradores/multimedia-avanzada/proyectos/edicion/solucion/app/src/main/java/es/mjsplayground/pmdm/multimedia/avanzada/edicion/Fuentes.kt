package es.mjsplayground.pmdm.multimedia.avanzada.edicion
import java.net.URI
/** Direcciones explícitas del laboratorio; nunca recibe claves ni extrae un vídeo de YouTube. */
fun validarFuente(texto: String): String {
    val limpia = texto.trim()
    val uri = URI(limpia)
    require(uri.scheme in setOf("https", "rtsp", "android.resource"))
    require(!uri.host.isNullOrBlank() && uri.userInfo == null)
    return limpia
}
