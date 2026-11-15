# Bluetooth clásico y mensajes RFCOMM

## Emparejado no significa conectado al servicio

El emparejamiento establece una relación que el sistema puede utilizar para autenticar enlaces. Una conexión RFCOMM requiere además que un equipo ofrezca un servicio y el otro lo solicite. Tener el dispositivo en la lista de vinculados no demuestra que esté escuchando el UUID de nuestra aplicación.

El [ejemplo oficial de conexión Bluetooth](https://developer.android.com/develop/connectivity/bluetooth/connect-bluetooth-devices) distingue servidor y cliente. El laboratorio entrega ambos papeles en la misma app, para ejecutarlos en **dos equipos propios o autorizados**. No se empareja ni conecta automáticamente a terceros.

En Ajustes del sistema vincula previamente esos equipos si lo necesitan. Después, la app puede consultar los vinculados. Muestra nombres para escoger el correcto, sin guardar direcciones de hardware. Este laboratorio no inicia descubrimiento clásico; utilizar un dispositivo ya conocido acota el ejercicio.

## Permisos según versión

Desde API 31, consultar vínculos y conectar requiere `BLUETOOTH_CONNECT` en ejecución. Antes se utilizan los permisos Bluetooth del manifiesto; los antiguos están acotados con `maxSdkVersion=30`. La exploración BLE tiene requisitos distintos, explicados en su página.

La [documentación oficial de permisos](https://developer.android.com/develop/connectivity/bluetooth/bt-permissions) detalla esa separación. La Activity solicita lo necesario para la acción pulsada. El ayudante captura una revocación durante la operación. Una anotación de lint no concede autorización: el control de ejecución sigue siendo necesario.

Si Bluetooth está desactivado o no existe adaptador, la pantalla informa y ofrece abrir Ajustes. No intenta activarlo sin intervención. Denegar acceso no bloquea el cliente HTTP.

## Servidor: escuchar, aceptar e intercambiar

`listenUsingRfcommWithServiceRecord` crea un `BluetoothServerSocket` asociado al UUID del laboratorio. El cliente debe usar el mismo. `accept` espera una conexión y entrega un `BluetoothSocket`: el servidor de escucha y el socket aceptado son recursos distintos.

El ayudante `Enlaces` ejecuta las operaciones bloqueantes fuera del hilo de interfaz. Escucha como máximo 15 segundos. Tras aceptar cierra el servidor de escucha y utiliza el socket para una petición y una respuesta. Un límite adicional cierra la sesión si el otro equipo no completa el intercambio. En `onStop` se cierran todos los recursos activos.

Este fragmento ilustra la separación; la gestión completa de timeout y cierre está en el proyecto:

```kotlin title="Enlaces.kt — fragmento del servidor"
val servidor = adaptador.listenUsingRfcommWithServiceRecord(
    "PMDM-Laboratorio", SERVICIO
)
val conexion = servidor.accept(15000)
servidor.close()
// conexion tiene inputStream y outputStream para el intercambio.
```

No ejecutes `accept` o `connect` directamente en el clic de un botón. La pantalla debe poder seguir respondiendo y permitir detener la operación.

## Cliente y fronteras de mensajes

El cliente crea un socket con `createRfcommSocketToServiceRecord`, conecta y escribe una trama. La app envía HOLA o PING, no contenido libre. El servidor responde con su protocolo y cierra esa sesión. Una nueva pulsación crea otra conexión.

Un flujo puede entregar una escritura en varios fragmentos. El laboratorio delimita por salto de línea y limita la trama a 20 bytes. `leerTrama` lee hasta ese delimitador o cierre; rechaza exceso. **No** utiliza una sola llamada `read` como si entregara exactamente un mensaje.

`Protocolo.responder` separa la interpretación de la radio. HOLA produce OK HOLA; PING debe producir PONG tras completar el TODO. Un mensaje desconocido produce ERROR. Las pruebas JVM verifican este contrato y límites sin fingir un enlace físico.

## Probar y documentar el intercambio

En el equipo A pulsa Escuchar. En B consulta vinculados, escoge A y envía HOLA. Deben aparecer respuesta y confirmación correspondientes. Repite con PING, después sin servidor y finalmente deteniendo durante la espera. Registra ambos dispositivos, versiones y resultados, sin publicar nombres personales ni direcciones.

El UUID identifica un servicio; no constituye por sí mismo un sistema de autenticación de aplicación. El ejemplo no transmite datos sensibles ni se presenta como mensajería de producción. Si solo hay un emulador, quedan pendientes emparejamiento, conexión y transferencia reales.
