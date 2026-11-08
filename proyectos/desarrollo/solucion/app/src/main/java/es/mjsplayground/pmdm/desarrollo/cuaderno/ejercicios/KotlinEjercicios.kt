package es.mjsplayground.pmdm.desarrollo.cuaderno.ejercicios

data class Tarea(val titulo: String, val terminada: Boolean = false)

fun etiqueta(nombre: String?): String {
    val limpio = nombre?.trim() ?: ""
    return if (limpio.isEmpty()) "Sin nombre" else limpio
}

fun pendientes(tareas: List<Tarea>): List<String> =
    tareas.filter { !it.terminada }.map { it.titulo }

fun resumen(tareas: List<Tarea>): String = "Pendientes: ${pendientes(tareas).size}"

fun clasificar(nota: Int): String = when {
    nota !in 0..10 -> "Inválida"
    nota < 5 -> "Revisar"
    else -> "Superada"
}
