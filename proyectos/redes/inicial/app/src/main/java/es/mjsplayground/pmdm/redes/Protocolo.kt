package es.mjsplayground.pmdm.redes

object Protocolo {
    fun responder(datos: ByteArray): String {
        require(datos.size <= 20) { "Mensaje demasiado grande" }
        return when (datos.toString(Charsets.UTF_8).trim()) {
            "HOLA" -> "OK HOLA"
            // TODO redes-ping: responde PONG al mensaje PING, sin cambiar el ejemplo HOLA.
            else -> "ERROR"
        }
    }
}
