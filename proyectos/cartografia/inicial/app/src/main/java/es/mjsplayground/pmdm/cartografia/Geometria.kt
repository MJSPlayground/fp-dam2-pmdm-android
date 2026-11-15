package es.mjsplayground.pmdm.cartografia

import kotlin.math.*

data class Punto(val longitud: Double, val latitud: Double) {
    init { require(longitud.isFinite() && latitud.isFinite() && longitud in -180.0..180.0 && latitud in -90.0..90.0) }
}
enum class Zona { DENTRO, FUERA, INCIERTA }

object Geometria {
    val centro = Punto(-3.7038, 40.4168)
    const val radioMetros = 500.0
    // Demostración resuelta: distancia geodésica esférica, no longitud de una ruta por calles.
    fun distancia(a: Punto, b: Punto): Double {
        val latA = Math.toRadians(a.latitud)
        val latB = Math.toRadians(b.latitud)
        val deltaLat = latB - latA
        val deltaLon = Math.toRadians(b.longitud - a.longitud)
        val h = sin(deltaLat / 2).pow(2) + cos(latA) * cos(latB) * sin(deltaLon / 2).pow(2)
        return 6371000.0 * 2.0 * asin(sqrt(h.coerceIn(0.0, 1.0)))
    }

    fun clasificar(distancia: Double, precision: Double, radio: Double): Zona {
        require(distancia.isFinite() && precision.isFinite() && radio.isFinite())
        require(distancia >= 0 && precision >= 0 && radio > 0)
        // TODO cartografia-zona: clasifica el intervalo distancia ± precisión respecto al radio.
        // Mientras está pendiente, no afirmes que una posición pertenece a la zona.
        return Zona.INCIERTA
    }
}
