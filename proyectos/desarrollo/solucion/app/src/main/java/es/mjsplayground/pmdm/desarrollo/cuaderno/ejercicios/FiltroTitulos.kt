package es.mjsplayground.pmdm.desarrollo.cuaderno.ejercicios

fun filtrarTitulos(titulos: List<String>, consulta: String): List<String> {
    val texto = consulta.trim()
    return titulos.filter { it.contains(texto, ignoreCase = true) }
}
