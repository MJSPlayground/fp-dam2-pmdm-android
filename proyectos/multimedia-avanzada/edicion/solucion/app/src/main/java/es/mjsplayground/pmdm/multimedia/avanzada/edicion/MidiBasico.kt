package es.mjsplayground.pmdm.multimedia.avanzada.edicion
/** Mensaje MIDI 1.0; no audio PCM. Canal numerado de 0 a 15. */
fun notaMidi(canal: Int, nota: Int, velocidad: Int, encendida: Boolean): ByteArray {
    require(canal in 0..15 && nota in 0..127 && velocidad in 0..127)
    val estado = (if (encendida) 0x90 else 0x80) or canal
    return byteArrayOf(estado.toByte(), nota.toByte(), velocidad.toByte())
}
