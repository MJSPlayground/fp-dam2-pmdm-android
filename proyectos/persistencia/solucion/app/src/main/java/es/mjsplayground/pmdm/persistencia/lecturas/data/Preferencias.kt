package es.mjsplayground.pmdm.persistencia.lecturas.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.preferencias by preferencesDataStore(name = "lecturas")
@Singleton
class Preferencias @Inject constructor(@ApplicationContext contexto: Context) {
    private val almacen = contexto.preferencias
    private val pendientes = booleanPreferencesKey("solo_pendientes")
    val soloPendientes = almacen.data.catch { error ->
        if (error is IOException) emit(emptyPreferences()) else throw error
    }.map { it[pendientes] ?: false }
    suspend fun cambiarSoloPendientes(valor: Boolean) {
        almacen.edit { it[pendientes] = valor }
    }
}
