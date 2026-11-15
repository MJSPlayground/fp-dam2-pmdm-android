package es.mjsplayground.pmdm.firebase

data class TurnoUsuario(val uid: String, val version: Long)

/** Una sesión cambia de versión al cambiar de identidad, no al abrir un selector. */
class SesionUsuario {
    private var uid: String? = null
    private var version = 0L
    fun actualizar(nuevo: String?) {
        if (nuevo != uid) { uid = nuevo; version++ }
    }
    fun capturar(): TurnoUsuario? = uid?.let { TurnoUsuario(it, version) }
    fun restaurar(turno: TurnoUsuario, actual: String?) {
        if (uid == null && turno.uid == actual) { uid = turno.uid; version = turno.version }
    }
    fun vigente(turno: TurnoUsuario?, actual: String?): Boolean =
        turno != null && turno.uid == uid && turno.uid == actual && turno.version == version
}
