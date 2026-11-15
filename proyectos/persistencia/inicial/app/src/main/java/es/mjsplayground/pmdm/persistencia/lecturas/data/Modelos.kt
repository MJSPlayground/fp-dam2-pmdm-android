package es.mjsplayground.pmdm.persistencia.lecturas.data

import androidx.room.*

@Entity(tableName = "autores")
data class Autor(@PrimaryKey val autorId: Long, val nombre: String)

@Entity(tableName = "lecturas", foreignKeys = [ForeignKey(entity = Autor::class,
    parentColumns = ["autorId"], childColumns = ["autorId"], onDelete = ForeignKey.RESTRICT)],
    indices = [Index("autorId")])
data class Lectura(@PrimaryKey val lecturaId: Long, val titulo: String, val autorId: Long,
    @ColumnInfo(defaultValue = "0") val leida: Boolean = false)

@Entity(tableName = "etiquetas")
data class Etiqueta(@PrimaryKey val etiquetaId: Long, val nombre: String)

@Entity(tableName = "lectura_etiqueta", primaryKeys = ["lecturaId", "etiquetaId"],
    indices = [Index("etiquetaId")], foreignKeys = [
        ForeignKey(entity = Lectura::class, parentColumns = ["lecturaId"], childColumns = ["lecturaId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Etiqueta::class, parentColumns = ["etiquetaId"], childColumns = ["etiquetaId"], onDelete = ForeignKey.CASCADE)])
data class LecturaEtiqueta(val lecturaId: Long, val etiquetaId: Long)

data class AutorConLecturas(@Embedded val autor: Autor,
    @Relation(parentColumn = "autorId", entityColumn = "autorId") val lecturas: List<Lectura>)

data class LecturaCompleta(@Embedded val lectura: Lectura,
    @Relation(parentColumn = "autorId", entityColumn = "autorId") val autor: Autor,
    @Relation(parentColumn = "lecturaId", entityColumn = "etiquetaId", associateBy = Junction(LecturaEtiqueta::class))
    val etiquetas: List<Etiqueta>)

data class ResumenAutor(val nombre: String, val total: Int, val pendientes: Int)
