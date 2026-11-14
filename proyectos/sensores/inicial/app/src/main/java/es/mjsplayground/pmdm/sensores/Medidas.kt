package es.mjsplayground.pmdm.sensores

import kotlin.math.sqrt

/** El acelerómetro incluye gravedad; esta magnitud no es una velocidad. */
fun magnitud(x: Float, y: Float, z: Float): Double {
    require(x.isFinite() && y.isFinite() && z.isFinite())
    return sqrt(x.toDouble()*x + y.toDouble()*y + z.toDouble()*z)
}

fun radianesAGrados(valor: Float): Double {
    require(valor.isFinite())
    // TODO sensor-unidades: convertir radianes por segundo a grados por segundo.
    return 0.0
}
