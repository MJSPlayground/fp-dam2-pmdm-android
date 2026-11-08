package es.mjsplayground.pmdm.desarrollo.cuaderno.data

import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Url
import es.mjsplayground.pmdm.desarrollo.cuaderno.model.Nota

@Serializable
data class PublicacionDto(val id: Int, val title: String, val body: String)

interface NotasApi {
    @GET
    suspend fun publicaciones(@Url url: String): List<PublicacionDto>
}

fun PublicacionDto.aNota(): Nota = Nota(titulo = title.trim().take(80), contenido = body.trim())
