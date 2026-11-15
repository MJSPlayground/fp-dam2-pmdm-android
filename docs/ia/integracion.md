# Integración segura de una generación

## Separar cliente, backend y proveedor

El recorrido del laboratorio es Android → servidor local → proveedor opcional. El cliente no recibe una clave del proveedor ni decide libremente a qué URL reenviar entradas. El servidor fija su host de destino y limita el identificador del modelo a un formato permitido.

```text
Botón Consultar → ViewModel → cliente HTTP → servidor
                        ↑                     ↓
                  estado de UI ← JSON de respuesta
```

Guardar una clave en `strings.xml`, BuildConfig o un archivo Kotlin no la protege dentro del APK. Tampoco lo hace ofuscarla. Las credenciales del proveedor pertenecen al servidor; las del usuario requieren su propio flujo de autenticación. El servidor del ejemplo escucha solo en loopback y no está preparado para exponerse en Internet.

Como alternativa, [Firebase AI Logic](https://firebase.google.com/docs/ai-logic) proporciona SDK de cliente diseñados para ese servicio y controles como App Check, configuración de modelos y límites. Usarlo exige configurar el proyecto y revisar sus mecanismos de protección y costes; no equivale a incluir una clave administrativa en el móvil. No es necesario crear otra capa propia si el servicio cubre adecuadamente una necesidad aprobada.

## Un contrato de aplicación pequeño

Android envía `POST /generar` con una pregunta y, opcionalmente, una imagen JPEG codificada en Base64. El servidor devuelve `respuesta` y `simulada`. Este último campo permite mostrar con claridad si el resultado fue una demostración local o una respuesta remota.

La pregunta admite entre 3 y 2000 caracteres después de recortar espacios. La imagen tiene un máximo de 512 KiB antes de Base64. Se limita también el tamaño del cuerpo HTTP: Base64 aumenta el volumen y no es un cifrado. El JSON no contiene rutas privadas ni una URI que el proveedor no pueda abrir.

El modo simulado devuelve una frase que indica qué modalidades llegaron. `!error` provoca un fallo y `!demora` introduce espera. Sirve para repetir estados, pero no describe el contenido de una foto ni demuestra calidad de IA.

## El adaptador remoto

El servidor solo activa llamadas externas si se inicia con `--real`. Entonces necesita `GEMINI_API_KEY` y `GEMINI_MODEL` en su entorno, nunca en un archivo versionado. No se utiliza una credencial presente por accidente en el entorno durante las pruebas ordinarias: el indicador de arranque es obligatorio.

El adaptador utiliza la [API GenerateContent](https://ai.google.dev/api/generate-content), que admite partes de texto e imagen. La clave se coloca en una cabecera y la petición establece un límite de salida. La respuesta puede no tener candidatos o terminar por una condición que no permita mostrar contenido; eso se trata como resultado no disponible, sin inventar una respuesta.

Se mantiene el identificador de modelo fuera del APK para poder sustituirlo cuando cambie la disponibilidad. Activar un proveedor real requiere revisar condiciones, región, tratamiento de datos y cuotas de esa cuenta. Este material no crea la cuenta, activa facturación ni ejecuta peticiones de pago.

## Estado y cancelación

`ConsultaViewModel` conserva carga, respuesta, origen y mensaje. La pantalla llama a `consultar` por evento y observa su StateFlow. La petición bloqueante se ejecuta en `Dispatchers.IO`; no bloquea el hilo de Compose. Un trabajo previo se cancela al sustituir la consulta y `CancellationException` se relanza, como en la ampliación de [asincronía](../asincronia/corrutinas.md).

Cancelar la corrutina evita publicar su resultado al volver de la operación suspendida. Con un cliente HTTP bloqueante, la lectura subyacente puede continuar hasta completar o agotar su timeout. Eso no garantiza cancelar una inferencia ya recibida por el proveedor ni recuperar su coste. Un cliente que permita cerrar explícitamente la llamada puede mejorar ese comportamiento; la semántica de cancelación del servidor sigue siendo una decisión separada.

## Fallos distintos de una respuesta vacía

Un 400 indica entrada o respuesta no válida; 429 comunica saturación o cuota; 502/503, servicio no disponible; 504, tiempo agotado. El servidor no devuelve al cliente excepciones con credenciales ni cuerpos del proveedor. La app muestra un mensaje recuperable y conserva la posibilidad de corregir la pregunta.

Las pruebas del servidor utilizan un doble del proveedor para verificar cabecera, contrato y límite de salida sin una llamada real, y HTTP loopback para comprobar los estados del servicio local. Son evidencias de integración; no son una evaluación de la exactitud del modelo remoto.
