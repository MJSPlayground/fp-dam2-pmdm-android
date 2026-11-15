package es.mjsplayground.pmdm.calidad.revision

/** Ejercicio local: la huella identifica el certificado de firma, no una clave privada. */
data class VersionInstalable(val paquete: String, val huellaFirma: String, val codigo: Long, val nombre: String)

fun candidataCompatible(instalada: VersionInstalable, candidata: VersionInstalable): Boolean =
    candidata.paquete == instalada.paquete &&
        candidata.huellaFirma == instalada.huellaFirma &&
        candidata.codigo > instalada.codigo
