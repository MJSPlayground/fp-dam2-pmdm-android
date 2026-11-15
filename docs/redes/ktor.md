# HTTP y respuestas complejas con Ktor

## Otra forma de construir el cliente

Retrofit, usado en el itinerario, describe endpoints con interfaces y anotaciones. [Ktor Client](https://ktor.io/docs/client-create-new-application.html) permite expresar la petición directamente en Kotlin y escoger un motor que realiza el transporte. No necesitas combinar ambos clientes para una petición.

El proyecto fija **Ktor 3.3.3**, [publicación oficial](https://github.com/ktorio/ktor/releases/tag/3.3.3), con el Kotlin y la serialización del curso. Usa core, motor Android, ContentNegotiation y serialización JSON. El catálogo contiene las dependencias concretas y el plugin Kotlin Serialization. No se afirma que sean las últimas versiones disponibles; cambiar solo un artefacto puede romper la combinación.

## Un sobre con lista y metadatos

Una respuesta real puede ser un objeto con datos y metadatos. El servidor del laboratorio devuelve `mensajes` y `siguiente`. Cada mensaje tiene identificador, texto y estado de lectura. `@Serializable` permite convertir esa estructura a Kotlin:

```kotlin title="ApiMensajes.kt — modelos incluidos"
@Serializable data class Mensaje(
    val id: Long,
    val texto: String,
    val leido: Boolean
)
@Serializable data class Pagina(
    val mensajes: List<Mensaje>,
    val siguiente: String? = null
)
```

`List<Mensaje>` no es una lista de cadenas: cada elemento tiene campos propios. `siguiente = null` indica ausencia de la siguiente página. La app de laboratorio muestra una página; no simula haber recorrido paginación que no ha solicitado. Una implementación paginada debe evitar repetir páginas, validar el destino siguiente y conservar orden y estados al anexar datos.

## Motor, plugins y ciclo de vida

`HttpClient(Android)` utiliza el motor Android. ContentNegotiation configura JSON; `ignoreUnknownKeys` permite campos adicionales sin aceptar que falten los obligatorios o que `mensajes` sea una cadena. HttpTimeout limita la espera y `expectSuccess = true` convierte respuestas HTTP de error en excepciones apropiadas.

```kotlin title="ApiMensajes.kt — configuración resuelta"
val cliente = HttpClient(Android) {
    expectSuccess = true
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
    install(HttpTimeout) {
        requestTimeoutMillis = 8000
        connectTimeoutMillis = 5000
    }
}
```

Los imports y dependencias están en el proyecto. Conserva una instancia por propietario y ciérrala al terminar. Crear un cliente nuevo por recomposición mantiene recursos innecesarios y no controla cuándo finalizarán las peticiones.

## Petición suspendida y presentación

`ApiMensajes.cargar` valida la dirección y llama a `cliente.get(direccion).body<Pagina>()`. El valor no llega de forma inmediata a la composición: la Activity inicia una corrutina, muestra carga y actualiza su estado al recibirlo. Al abandonar la pantalla cancela la petición; en `onDestroy` cierra el cliente.

La URL debe ser HTTPS salvo el servidor local de depuración. No se aceptan credenciales embebidas en ella. Una respuesta vacía es éxito sin mensajes; un error HTTP del recurso, un fallo del servidor y un contrato incompatible tienen recorridos distintos. La app retira la lista antigua al fallar para no hacer pasar esos datos por una nueva respuesta.

## Servidor propio y pruebas sin Internet

`servidor.py`, incluido en ambos ZIP, escucha en el ordenador en `127.0.0.1:8092`. El emulador usa `http://10.0.2.2:8092/mensajes`. Las variantes `?vacio=1`, `?error=1` y `?roto=1` permiten comprobar lista vacía, HTTP 503 y JSON incompatible. No envía ni recibe mensajes personales.

Las pruebas `ApiMensajesTest` utilizan [MockEngine de Ktor](https://ktor.io/docs/client-testing.html) para entregar respuestas controladas al cliente y su conversor real. Comprueban campos adicionales, vacío, error HTTP y contrato incorrecto. Esto no verifica un servidor remoto ni el transporte Android: la prueba desde la app contra el servidor propio cubre esa integración por separado.

La actividad guiada deja el cliente preparado y pide una operación equivalente de colecciones: seleccionar los mensajes no leídos sin alterar el orden ni duplicarlos. Ya conoces `filter` del itinerario; el concepto nuevo aquí es interpretar el contrato y comunicar su estado.
