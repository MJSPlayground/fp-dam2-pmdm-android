package es.mjsplayground.pmdm.cartografia

import java.net.HttpURLConnection
import java.net.URI
import java.net.URLEncoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.*

object Respuestas {
    fun lugar(texto: String): Punto? {
        val resultados = Json.parseToJsonElement(texto).jsonArray
        if (resultados.isEmpty()) return null
        val primero = resultados.first().jsonObject
        return Punto(primero.getValue("lon").jsonPrimitive.content.toDouble(),
            primero.getValue("lat").jsonPrimitive.content.toDouble())
    }

    fun ruta(texto: String): List<Punto> {
        val cuerpo = Json.parseToJsonElement(texto).jsonObject
        require(cuerpo["code"]?.jsonPrimitive?.content == "Ok") { "Sin ruta válida" }
        val ruta = cuerpo.getValue("routes").jsonArray.firstOrNull()?.jsonObject ?: error("Ruta vacía")
        val geometria = ruta.getValue("geometry").jsonObject
        require(geometria["type"]?.jsonPrimitive?.content == "LineString")
        val puntos = geometria.getValue("coordinates").jsonArray.map {
            val par = it.jsonArray
            require(par.size >= 2)
            Punto(par[0].jsonPrimitive.double, par[1].jsonPrimitive.double)
        }
        require(puntos.size >= 2) { "La ruta necesita dos puntos" }
        return puntos
    }
}

class ServicioCartografico {
    // Caché del laboratorio en memoria: evita repetir la misma consulta durante la sesión.
    private val cache = mutableMapOf<String, String>()
    private var ultimoInicio = 0L

    suspend fun buscar(base: String, consulta: String): Punto? =
        obtener(base, "/search?format=jsonv2&limit=1&q=" + URLEncoder.encode(consulta, "UTF-8"), Respuestas::lugar)

    suspend fun ruta(base: String): List<Punto> = obtener(base,
        "/route/v1/driving/-3.7038,40.4168;-3.69,40.42?overview=full&geometries=geojson", Respuestas::ruta)

    private suspend fun <T> obtener(base: String, recurso: String, interpretar: (String) -> T): T = withContext(Dispatchers.IO) {
        val uri = URI(base.trim())
        val local = uri.host in setOf("10.0.2.2", "localhost", "127.0.0.1")
        require(uri.scheme == "https" || (BuildConfig.DEBUG && local && uri.scheme == "http")) {
            "Usa HTTPS o el servidor local de depuración"
        }
        require(uri.userInfo == null && uri.query == null && uri.fragment == null)
        val direccion = base.trim().trimEnd('/') + recurso
        cache[direccion]?.let { return@withContext interpretar(it) }
        val ahora = System.nanoTime()
        require(ahora - ultimoInicio >= 1_100_000_000L || ultimoInicio == 0L) { "Espera antes de otra consulta" }
        ultimoInicio = ahora
        val conexion = URI(direccion).toURL().openConnection() as HttpURLConnection
        try {
            conexion.connectTimeout = 8000
            conexion.readTimeout = 8000
            conexion.instanceFollowRedirects = false
            conexion.setRequestProperty("User-Agent", "PMDM-Cartografia-Laboratorio/1.0 (+https://github.com/MJSPlayground/fp-dam2-pmdm-android)")
            conexion.setRequestProperty("Accept", "application/json")
            check(conexion.responseCode == 200) { "HTTP ${conexion.responseCode}" }
            val texto = conexion.inputStream.bufferedReader(Charsets.UTF_8).use { lector ->
                val buffer = CharArray(2048)
                val salida = StringBuilder()
                while (true) {
                    val leidos = lector.read(buffer)
                    if (leidos < 0) break
                    check(salida.length + leidos <= 65536) { "Respuesta demasiado grande" }
                    salida.append(buffer, 0, leidos)
                }
                salida.toString()
            }
            val resultado = interpretar(texto)
            if (cache.size >= 16) cache.remove(cache.keys.first())
            cache[direccion] = texto
            resultado
        } finally { conexion.disconnect() }
    }
}
