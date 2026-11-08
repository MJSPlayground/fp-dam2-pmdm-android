package es.mjsplayground.pmdm.desarrollo.cuaderno.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notas")
data class Nota(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val titulo: String,
    val contenido: String
)

fun esNotaValida(titulo: String, contenido: String): Boolean =
    titulo.trim().length in 3..80 && contenido.isNotBlank()
