package es.mjsplayground.pmdm.desarrollo.cuaderno.ejercicios

data class Tarea(val titulo: String, val terminada: Boolean = false)

/** Devuelve el nombre recortado, o "Sin nombre" si es null o sólo espacios. */
fun etiqueta(nombre: String?): String = TODO("Kotlin: nulabilidad y cadenas")

/** Devuelve los títulos de las tareas sin terminar, conservando su orden. */
fun pendientes(tareas: List<Tarea>): List<String> = TODO("Kotlin: filter y map")

/** Describe el número de pendientes: "Pendientes: 2", por ejemplo. */
fun resumen(tareas: List<Tarea>): String = TODO("Kotlin: función y plantilla")

/** Clasifica una nota: <0 o >10 Inválida; <5 Revisar; resto Superada. */
fun clasificar(nota: Int): String = TODO("Kotlin: when como expresión")
