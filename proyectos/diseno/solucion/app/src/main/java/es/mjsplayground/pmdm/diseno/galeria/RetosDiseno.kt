package es.mjsplayground.pmdm.diseno.galeria

/** Contrato autónomo: primera aparición por ID y favoritos antes que no favoritos. */
fun ordenarSeleccion(datos: List<Lamina>, favoritos: Set<Int>): List<Lamina> =
    datos.distinctBy { it.id }.sortedWith(compareByDescending<Lamina> { it.id in favoritos }.thenBy { it.id })

fun contraste(primerPlano: Int, fondo: Int): Double {
    fun luminancia(color: Int): Double {
        fun lineal(canal: Int): Double {
            val v = canal / 255.0
            return if (v <= .04045) v / 12.92 else Math.pow((v + .055) / 1.055, 2.4)
        }
        return .2126 * lineal(color shr 16 and 255) + .7152 * lineal(color shr 8 and 255) + .0722 * lineal(color and 255)
    }
    val a = luminancia(primerPlano)
    val b = luminancia(fondo)
    return (maxOf(a, b) + .05) / (minOf(a, b) + .05)
}
