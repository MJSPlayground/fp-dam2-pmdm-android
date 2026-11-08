package es.mjsplayground.pmdm.desarrollo.cuaderno.data

import java.net.URI
import es.mjsplayground.pmdm.desarrollo.cuaderno.BuildConfig
import es.mjsplayground.pmdm.desarrollo.cuaderno.model.Nota
import es.mjsplayground.pmdm.desarrollo.cuaderno.model.esNotaValida

const val URL_INICIAL = "https://jsonplaceholder.typicode.com/posts"

class UrlNoValidaException : IllegalArgumentException()
class NotaNoValidaException : IllegalArgumentException()

/** HTTP se limita al laboratorio debug; release sólo acepta HTTPS. */
fun validarUrl(url: String, permitirHttp: Boolean): String {
    val limpia = url.trim()
    val uri = try { URI(limpia) } catch (_: Exception) { throw UrlNoValidaException() }
    if (uri.host == null || uri.userInfo != null || uri.fragment != null) throw UrlNoValidaException()
    val host = uri.host.lowercase()
    val partes = host.split(".").map { it.toIntOrNull() }
    val privada = host == "localhost" || (
        partes.size == 4 && partes.all { it != null && it in 0..255 } &&
        (partes[0] == 10 || partes[0] == 127 ||
            (partes[0] == 192 && partes[1] == 168) ||
            (partes[0] == 172 && partes[1]!! in 16..31))
    )
    if (!(uri.scheme == "https" || (uri.scheme == "http" && permitirHttp && privada))) throw UrlNoValidaException()
    return limpia
}

class NotaRepository(private val dao: NotaDao, private val api: NotasApi) {
    fun observar() = dao.observar()
    fun buscar(id: Long) = dao.buscar(id)

    suspend fun guardar(nota: Nota): Long {
        if (!esNotaValida(nota.titulo, nota.contenido)) throw NotaNoValidaException()
        return dao.guardar(nota.copy(titulo = nota.titulo.trim(), contenido = nota.contenido.trim()))
    }

    suspend fun eliminar(nota: Nota) = dao.eliminar(nota)

    suspend fun consultarRemotas(url: String): List<PublicacionDto> =
        api.publicaciones(validarUrl(url, BuildConfig.DEBUG))

    suspend fun importar(publicacion: PublicacionDto) = guardar(publicacion.aNota())
}
