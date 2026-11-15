package es.mjsplayground.pmdm.redes

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test

class ApiMensajesTest {
    private fun api(cuerpo: String, codigo: HttpStatusCode = HttpStatusCode.OK): ApiMensajes {
        val motor = MockEngine { respond(cuerpo, codigo, headersOf(HttpHeaders.ContentType, "application/json")) }
        return ApiMensajes(HttpClient(motor) {
            expectSuccess = true
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        })
    }
    @Test fun sobreAnidadoYCampoDesconocido() = runBlocking {
        api("""{"mensajes":[{"id":7,"texto":"Prueba","leido":false}],"siguiente":null,"extra":42}""").use {
            assertEquals(7L, it.cargar("https://laboratorio.invalid/mensajes").mensajes.single().id)
        }
    }
    @Test fun vacioNoEsError() = runBlocking {
        api("""{"mensajes":[],"siguiente":null}""").use { assertTrue(it.cargar("https://laboratorio.invalid/mensajes").mensajes.isEmpty()) }
    }
    @Test(expected = ServerResponseException::class) fun errorHttp() = runBlocking {
        api("{}", HttpStatusCode.ServiceUnavailable).use { it.cargar("https://laboratorio.invalid/mensajes") }; Unit
    }
    @Test(expected = Exception::class) fun contratoIncompatible() = runBlocking {
        api("""{"mensajes":"incompatible"}""").use { it.cargar("https://laboratorio.invalid/mensajes") }; Unit
    }
    @Test(expected = IllegalArgumentException::class) fun noEnviaCredencialesEnUrl() = runBlocking {
        api("{}").use { it.cargar("https://usuario:secreto@laboratorio.invalid/mensajes") }; Unit
    }
}
