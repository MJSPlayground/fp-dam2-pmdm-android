package es.mjsplayground.pmdm.segundoplano.estudio

enum class Decision { COMPLETAR, REINTENTAR, FALLAR }

fun decidir(completado: Boolean, falloTemporal: Boolean, intento: Int, maxReintentos: Int): Decision {
    require(intento >= 0 && maxReintentos >= 0)
    return when {
        completado -> Decision.COMPLETAR
        falloTemporal && intento < maxReintentos -> Decision.REINTENTAR
        else -> Decision.FALLAR
    }
}
