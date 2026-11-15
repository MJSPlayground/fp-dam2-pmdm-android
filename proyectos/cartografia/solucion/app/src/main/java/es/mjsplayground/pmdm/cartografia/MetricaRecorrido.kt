package es.mjsplayground.pmdm.cartografia

/** Solución formativa del ejercicio autónomo: no presupone transitabilidad por calles. */
object MetricaRecorrido {
    fun longitud(puntos: List<Punto>): Double =
        puntos.zipWithNext().sumOf { (origen, destino) -> Geometria.distancia(origen, destino) }
}
