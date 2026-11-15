# Práctica guiada: Mensajería cercana

Construirás un filtro de mensajes y completarás un pequeño protocolo compartido por Bluetooth clásico y BLE. La pantalla HTTP, el motor Ktor, los permisos y el establecimiento de enlaces están preparados para concentrar el trabajo en tres operaciones concretas. Primero analizarás HTTP y el saludo HOLA resueltos; después implementarás equivalentes y una escritura GATT con compatibilidad de API.

Lee [Ktor](ktor.md), [clásico](clasico.md) y [BLE](ble.md). Necesitas dos dispositivos propios compatibles únicamente para comprobar la radio: el proyecto abre y permite HTTP sin Bluetooth.

[Descargar proyecto inicial — ZIP](../descargas/proyectos/redes-inicial.zip){ .md-button .md-button--primary }

???+ example "Preparación y HTTP resuelto"

    Abre la carpeta con `settings.gradle.kts` en Android Studio, sincroniza y ejecuta **app**. Al abrir no debe empezar ninguna operación Bluetooth. El servidor HTTP `servidor.py` está incluido; su README indica cómo arrancarlo con Python. En el emulador utiliza `http://10.0.2.2:8092/mensajes`.

    **Observa el código existente.** Abre `ApiMensajes.kt`: localiza los modelos `Mensaje` y `Pagina`, el motor Android, el plugin JSON y los límites de espera. Sigue `MainActivity.cargar` desde el botón hasta la corrutina y sus estados. La respuesta es un objeto que contiene una lista, no una lista desnuda.

    **Comprueba.** Cargar muestra tres mensajes. Con `?vacio=1`, muestra vacío; con `?error=1`, error del servidor; con `?roto=1`, error de contrato. La pantalla sigue respondiendo. Consultar transporte de red no asegura que esos recursos HTTP estén disponibles.

??? example "Ahora te toca: seleccionar mensajes pendientes"

    **Ahora te toca — `redes-pendientes`.** Completa `Bandeja.pendientes`. Recibe los mensajes ya interpretados y debe devolver únicamente los que tienen `leido = false`, conservando su orden. No modifica el cliente, no vuelve a pedir la página y no duplica registros. `filter`, estudiado en Kotlin, permite expresar esta selección.

    ??? hint "Pista: un predicado por elemento"

        El predicado decide si el elemento permanece. El campo `leido` ya es Boolean; no lo compares con la cadena `"false"`. Una lista sin pendientes produce otra lista vacía, sin excepción.

    **Comprueba.** Carga el servidor y marca **Mostrar solo mensajes no leídos**: aparecen los mensajes 1 y 3, en ese orden. Desmarca: reaparecen los tres sin otra petición. En la ventana Gradle puedes activar las pruebas de los TODO con `:app:testDebugUnitTest -Paprendizaje.redes=true`; al principio también fallará PING, que completarás a continuación. Las dos pruebas omitidas por defecto no cuentan como aprobadas.

??? example "Analizar un protocolo y completar otra respuesta"

    **Ejemplo resuelto.** `Protocolo.responder` recibe bytes, limita su longitud y reconoce HOLA para devolver OK HOLA. No conoce Activities, sockets ni GATT. Por eso se puede probar con JUnit antes de disponer de dispositivos. `Enlaces` utiliza la misma función en el servidor RFCOMM y al recibir una escritura BLE.

    **Ahora te toca — `redes-ping`.** Añade la respuesta PONG para el mensaje PING. Conserva el saludo resuelto, el límite y ERROR para comandos desconocidos. No añadas datos personales ni conviertas el ejemplo en ejecución de comandos.

    ??? hint "Pista: otra rama del mismo contrato"

        Analiza la rama HOLA del `when`. El resultado de PING es otra cadena del protocolo; no necesita acceder a la red ni a un dispositivo. Revisa mayúsculas y espacios que elimina `trim`.

    **Comprueba.** Activa las pruebas de los TODO. Deben superar saludo, PING, desconocido, límite de 20 bytes y filtro. Esto verifica reglas, todavía no la transferencia por radio.

??? example "Comprobar RFCOMM con dos equipos propios"

    **Observa el código existente.** En `Enlaces`, sigue `escucharClasico`, `enviarClasico` y `leerTrama`. Las operaciones bloqueantes están fuera del hilo de interfaz, las tramas terminan en salto de línea y se cierran sockets al terminar, agotar tiempo o abandonar la pantalla. No reescribas esta infraestructura.

    Usa únicamente dispositivos del laboratorio autorizados. Vincúlalos desde Ajustes cuando corresponda. En A pulsa **Escuchar RFCOMM durante 15 s**. En B consulta vinculados, identifica A por su nombre, selecciónalo y envía HOLA. Repite la escucha para probar PING. Cada intercambio abre una sesión nueva.

    **Comprueba.** HOLA debe obtener OK HOLA; PING, PONG. Sin escucha activa se informa de fallo, sin congelar la pantalla. Al salir durante una espera se cierran los recursos. Si no hay dos equipos compatibles, registra estas comprobaciones como pendientes; no las sustituyas por los tests JVM.

??? example "Leer GATT resuelto y completar una escritura"

    **Observa el código existente.** En un dispositivo compatible, **Anunciar laboratorio BLE** crea el servicio propio y su característica antes de anunciar el UUID. En el otro, Explorar filtra ese UUID durante diez segundos; después seleccionas un resultado para conectar. El callback espera la conexión y el descubrimiento antes de indicar que está listo.

    `leerBle` ya inicia una lectura y `onCharacteristicRead` presenta los bytes al recibirlos. El método solo acepta una operación pendiente. En el inicial, `escribirBle` conserva comprobaciones, estado y errores, pero **todavía no inicia una escritura**.

    **Ahora te toca — `redes-escritura`.** Sustituye `val aceptada = false` por el inicio compatible de escritura de `mensaje` en `dato`. Desde API 33 utiliza la API que recibe el valor y el tipo `WRITE_TYPE_DEFAULT`; compara su código con `BluetoothStatusCodes.SUCCESS`. Antes de 33 prepara el valor y utiliza la API antigua, conservando su resultado Boolean. No marques éxito definitivo en ese punto: ya hay un callback `onCharacteristicWrite`.

    ??? hint "Pista: iniciar y terminar son momentos distintos"

        La página [BLE](ble.md#escribir-y-comprobar-la-respuesta) muestra el contrato moderno. En la rama antigua, la característica conserva el valor antes de llamar a `writeCharacteristic(dato)`. El bloque posterior ya restaura el estado cuando no se acepta iniciar; el callback lo restaura al terminar.

    **Comprueba físicamente.** Anuncia A y conecta B. Lee: el valor inicial es HOLA. Escribe PING, espera confirmación y vuelve a leer: debe llegar PONG. Desconecta o detén A: B debe comunicar desconexión o fallo y permitir detener sin cierre inesperado. No escribas características de otros dispositivos ni acredites callbacks de versiones que no hayas ejecutado.

??? example "Cierre y registro de evidencias"

    - HTTP: lista, filtro, vacío, 503, contrato incorrecto y cancelación.
    - Permisos: denegación sin bloquear las demás funciones.
    - Clásico: dos equipos, identidad correcta, tramas y cierre.
    - BLE: soporte de anunciante, filtro, descubrimiento, lectura/escritura y desconexión.
    - Interfaz: claro/oscuro, horizontal, fuente grande y desplazamiento hasta Detener.

    Distingue lo que comprobaste en emulador, pruebas JVM y equipos físicos. El código maneja dos familias de API; compilar ambas ramas no demuestra haberlas ejecutado. No publiques direcciones de hardware, nombres personales ni mensajes ajenos en el registro.

[Descargar solución formativa — ZIP](../descargas/proyectos/redes-solucion.zip){ .md-button }

La solución completa los tres TODO y añade el comando del ejercicio autónomo de repaso. Sus fuentes están separadas del inicial; el servidor propio está disponible en ambas descargas.
