package es.mjsplayground.pmdm.persistencia.lecturas.di

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import es.mjsplayground.pmdm.persistencia.lecturas.data.*
import es.mjsplayground.pmdm.persistencia.lecturas.domain.LecturasRepository
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class Enlaces {
    @Binds abstract fun repository(implementacion: DatosRepository): LecturasRepository
}
@Module
@InstallIn(SingletonComponent::class)
object Recursos {
    @Provides @Singleton
    fun base(@ApplicationContext contexto: Context): LecturasDatabase =
        Room.databaseBuilder(contexto, LecturasDatabase::class.java, "lecturas.db")
            .addMigrations(LecturasDatabase.MIGRACION_1_2).build()
    @Provides @Singleton
    fun api(): LecturasApi {
        val cliente = OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS).build()
        val json = Json { ignoreUnknownKeys = true }
        return Retrofit.Builder().baseUrl("https://jsonplaceholder.typicode.com/")
            .client(cliente).addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build().create(LecturasApi::class.java)
    }
}
