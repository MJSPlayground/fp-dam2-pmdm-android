package es.mjsplayground.pmdm.redes

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.net.URI

@Serializable data class Mensaje(val id: Long, val texto: String, val leido: Boolean)
@Serializable data class Pagina(val mensajes: List<Mensaje>, val siguiente: String? = null)

fun clienteMensajes() = HttpClient(Android) {
    expectSuccess = true
    install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
    install(HttpTimeout) { requestTimeoutMillis = 8000; connectTimeoutMillis = 5000 }
}

class ApiMensajes(private val cliente: HttpClient = clienteMensajes()) : AutoCloseable {
    suspend fun cargar(direccion: String): Pagina {
        val uri = URI(direccion)
        require(uri.scheme == "https" || (BuildConfig.DEBUG && uri.scheme == "http" &&
            uri.host in setOf("10.0.2.2", "127.0.0.1", "localhost"))) { "Dirección no autorizada" }
        require(uri.userInfo == null)
        return cliente.get(direccion).body()
    }
    override fun close() { cliente.close() }
}

object Bandeja {
    fun pendientes(mensajes: List<Mensaje>): List<Mensaje> {
        // TODO redes-pendientes: selecciona solo los mensajes no leídos.
        return emptyList()
    }
}
