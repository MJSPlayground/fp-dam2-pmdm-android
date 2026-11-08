package es.mjsplayground.pmdm.desarrollo.cuaderno.data

import android.content.Context
import androidx.room.Room
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

/** Un contenedor manual por proceso; no hace falta un framework de inyección. */
class Contenedor(context: Context) {
    private val database = Room.databaseBuilder(
        context.applicationContext, NotaDatabase::class.java, "cuaderno.db"
    ).build()
    private val json = Json { ignoreUnknownKeys = true }
    private val cliente = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()
    private val api = Retrofit.Builder()
        .baseUrl("https://jsonplaceholder.typicode.com/")
        .client(cliente)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build().create(NotasApi::class.java)
    val repository = NotaRepository(database.notas(), api)
}
