package es.mjsplayground.pmdm.desarrollo.cuaderno.data

import java.net.URI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
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

class NotaRepository(private val api: NotasApi) {
    private val notas = MutableStateFlow(emptyList<Nota>())
    private var siguienteId = 1L
    fun observar() = notas.asStateFlow()
    fun buscar(id: Long) = notas.map { lista -> lista.find { it.id == id } }

    suspend fun guardar(nota: Nota): Long {
        if (!esNotaValida(nota.titulo, nota.contenido)) throw NotaNoValidaException()
        val id = if (nota.id == 0L) siguienteId++ else nota.id
        val guardada = nota.copy(id = id, titulo = nota.titulo.trim(), contenido = nota.contenido.trim())
        notas.update { lista ->
            (lista.filterNot { it.id == id } + guardada).sortedByDescending { it.id }
        }
        return id
    }

    suspend fun eliminar(nota: Nota) { notas.update { lista -> lista.filterNot { it.id == nota.id } } }

    suspend fun consultarRemotas(url: String): List<PublicacionDto> =
        api.publicaciones(validarUrl(url, BuildConfig.DEBUG))

    suspend fun importar(publicacion: PublicacionDto) = guardar(publicacion.aNota())
}
