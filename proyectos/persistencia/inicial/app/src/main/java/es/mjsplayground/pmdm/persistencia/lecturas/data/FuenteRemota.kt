package es.mjsplayground.pmdm.persistencia.lecturas.data

import kotlinx.serialization.Serializable
import retrofit2.http.GET
import javax.inject.Inject

@Serializable
data class LecturaDto(val id: Long, val userId: Long, val title: String)
interface LecturasApi {
    @GET("posts?_limit=5") suspend fun consultar(): List<LecturaDto>
}
class FuenteRemota @Inject constructor(private val api: LecturasApi) {
    suspend fun consultar() = api.consultar()
}
