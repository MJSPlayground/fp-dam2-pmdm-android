# Práctica guiada: Captura propia

Integra un visor CameraX y genera fotografías y vídeos silenciosos compartibles. El ejemplo de foto y la infraestructura de permisos están resueltos; completarás comienzo y detención de vídeo y reutilizarás lo aprendido sobre URI. Lee [CameraX](camara.md) y [archivos](archivos.md).

[Proyecto inicial — ZIP](../descargas/proyectos/multimedia-avanzada-captura-inicial.zip){ .md-button .md-button--primary }

???+ example "Preparación y foto ya implementada"

    Abre la carpeta con `settings.gradle.kts` en Android Studio, sincroniza y ejecuta **app**. Pulsa **Activar cámara**. Deniega primero si quieres comprobar ese camino; la pantalla debe explicar la decisión. Con permiso y cámara trasera disponible aparece el visor.

    **Observa el código existente.** En `app/src/main/java/es/mjsplayground/pmdm/multimedia/avanzada/captura/MainActivity.kt`, sigue `solicitarCamara` → `iniciarCamara`. Identifica proveedor, Preview, superficie y vínculo al ciclo de vida. El manifiesto no exige una cámara para instalar en todos los dispositivos y no declara micrófono.

    **Ejemplo resuelto.** Lee `capturarFoto`: crea un JPEG privado, configura orientación, construye OutputFileOptions y espera `onImageSaved`. `archivo(extension)` ya prepara el directorio y un nombre nuevo. No necesitas programar otra utilidad de almacenamiento.

    **Comprueba.** Captura una fotografía: debe aparecer Archivo guardado, nombre y bytes. El JPEG debe tener dimensiones positivas al abrirlo con un visor o inspeccionarlo durante depuración. Todavía no uses Compartir como prueba: su acción es uno de los TODO. Cambia a modo vídeo y vuelve: el visor debe seguir utilizable. La imagen del emulador es una escena virtual, no evidencia de una cámara física.

??? example "Del destino de foto al comienzo de vídeo"

    La preparación de un destino ya está resuelta para foto. En vídeo necesitas otras opciones y un PendingRecording. Este fragmento de referencia muestra el paso equivalente; presupone `captura` de tipo `VideoCapture<Recorder>` y el archivo `salida`:

    ```kotlin
    val opciones = FileOutputOptions.Builder(salida).build()
    val pendiente = captura.output.prepareRecording(this, opciones)
    ```

    El objeto pendiente todavía no graba. `start(executor, callback)` devuelve el `Recording`; su callback recibirá eventos posteriores. El helper `procesarEvento(evento, salida)` ya está preparado y distingue Start y Finalize.

    **Ahora te toca — `video-iniciar`.** Completa `grabar`. Obtén el caso `video` si existe, crea un MP4 con `archivo`, publica la preparación y solicita la grabación. Conserva el objeto devuelto en `grabacion` y entrega cada evento al helper preparado junto con el archivo. El executor principal se obtiene como en la foto. No actives el micrófono: este laboratorio es silencioso.

    ??? tip "Pista — Una intención no es un archivo terminado"

        `ocupada` evita otro envío durante la preparación. Start cambia a grabando; Finalize confirma o rechaza el archivo. No pongas Archivo guardado dentro del botón ni antes de ese evento. Conserva el destino para que el callback sepa qué archivo está terminando.

    **Comprueba hasta aquí.** Al pulsar Grabar debe aparecer primero la preparación y después Grabando vídeo sin audio. Si no llega Start, revisa modo, permisos y callback. Aún falta completar el botón Detener; continúa con el siguiente paso de esta misma etapa.

    **Ahora te toca — `video-detener`.** Completa `detener`: solicita terminar sobre la grabación conservada y comunica que la operación está finalizando. Deja que el helper interprete Finalize. No liberes el proveedor de cámara para simular una detención normal.

    ??? tip "Pista — El propietario ya tiene la operación"

        No construyas otro Recorder. La referencia que devolvió `start` representa esta grabación. El método de parada no devuelve el MP4; el evento finaliza el recorrido.

    **Comprueba.** Graba unos segundos, detén y espera Archivo guardado. El resultado debe ser un MP4 con duración positiva y sin pista de audio. Repite otra grabación: debe producir otro archivo. Sal mientras grabas y vuelve: no debe continuar una grabación invisible ni cerrarse inesperadamente la app.

??? example "Compartir con autonomía creciente"

    **Ahora te toca — `captura-compartir`.** Completa `compartir` para el archivo `ultimo`. La técnica ya se practicó con imágenes: autoridad del proveedor, URI, MIME, EXTRA_STREAM, ClipData y lectura temporal. Distingue JPEG de MP4 y utiliza Sharesheet. Si no hay receptor, muestra `sin_destino`.

    ??? tip "Pista — La extensión tiene un uso acotado aquí"

        Ambos archivos los creó nuestra aplicación con operaciones conocidas. El tipo es `image/jpeg` o `video/mp4`. No estás intentando detectar el formato de un archivo externo desconocido solamente por su nombre.

    **Comprueba.** Comparte primero la foto y después el vídeo. El receptor debe abrir el archivo correspondiente, con imagen visible y vídeo silencioso. Cancelar el selector conserva el último resultado. Ver una URI en el depurador no sustituye abrirla desde otro proceso.

??? example "Comprobación conjunta y registro"

    Retira el `@org.junit.Ignore` de `CapturaTest`, en `src/androidTest`, cuando estén completos los TODO. Desde Android Studio ejecuta la prueba con un emulador que tenga cámara virtual configurada. Comprueba JPEG real y metadatos del MP4, no solo un mensaje de pantalla.

    Haz además un recorrido manual: denegación, concesión, cambio de modo, foto, vídeo, detención, salida y regreso. Comprueba horizontal, fuente grande y tema oscuro. Distingue cámara virtual y física, ausencia de receptor y archivo incorrecto.

    Documenta modelo/API, entradas, orientación, dimensiones y duración observadas. El proyecto no restaura un proyecto de edición ni cataloga todos los archivos al reiniciar; conserva los archivos privados hasta que se eliminen datos o se desinstale.

## Solución de referencia

[Captura propia completa — ZIP](../descargas/proyectos/multimedia-avanzada-captura-solucion.zip){ .md-button }

Resuelve los tres TODO en la misma Activity. No requiere micrófono, cuentas ni servidor. La [consolidación](actividades.md) plantea los comportamientos sin repetir estos pasos.
