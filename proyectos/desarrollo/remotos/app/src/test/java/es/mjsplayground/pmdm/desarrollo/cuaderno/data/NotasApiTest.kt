package es.mjsplayground.pmdm.desarrollo.cuaderno.data

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.*
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class NotasApiTest {
    private fun api(server: MockWebServer): NotasApi = Retrofit.Builder()
        .baseUrl(server.url("/"))
        .addConverterFactory(Json { ignoreUnknownKeys = true }
            .asConverterFactory("application/json".toMediaType()))
        .build().create(NotasApi::class.java)

    @Test fun convierteJson200YRespetaLaUrlCompleta() = runBlocking {
        MockWebServer().use { server ->
            server.enqueue(MockResponse().setHeader("Content-Type", "application/json")
                .setBody("""[{"userId":1,"id":2,"title":"Compra","body":"Fruta"}]"""))
            val notas = api(server).publicaciones(server.url("/posts?delay=0").toString())
            assertEquals(listOf(PublicacionDto(2, "Compra", "Fruta")), notas)
            assertEquals("/posts?delay=0", server.takeRequest().path)
        }
    }

    @Test fun respuesta503SeDiferenciaDeErrorDeFormato() = runBlocking {
        MockWebServer().use { server ->
            server.enqueue(MockResponse().setResponseCode(503).setBody("""{"error":"mantenimiento"}"""))
            try {
                api(server).publicaciones(server.url("/error").toString())
                fail("Se esperaba HttpException")
            } catch (error: HttpException) { assertEquals(503, error.code()) }
        }
    }

    @Test fun jsonIncompatibleProduceErrorDeSerializacion() = runBlocking {
        MockWebServer().use { server ->
            server.enqueue(MockResponse().setBody("""[{"titulo":"faltan id/title/body"}]"""))
            try {
                api(server).publicaciones(server.url("/posts").toString())
                fail("Se esperaba SerializationException")
            } catch (_: SerializationException) { /* Resultado esperado. */ }
        }
    }
}
