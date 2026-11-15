# Práctica guiada: Mesa de edición

Recorta una imagen con una biblioteca mantenida, exporta un vídeo reducido sin audio y reproduce un efecto breve. El proyecto incluye datos sintéticos propios y una pantalla separada para reproducir y observar medios. Alternaremos código preparado, operaciones equivalentes y comprobaciones del archivo real.

Lee [recorte](edicion-imagen.md), [Transformer](transformer.md), [SoundPool/MIDI](sonido-midi.md) y [streaming](streaming.md).

[Proyecto inicial — ZIP](../descargas/proyectos/multimedia-avanzada-edicion-inicial.zip){ .md-button .md-button--primary }

???+ example "Preparación y lectura de responsabilidades"

    Abre en Android Studio la carpeta con `settings.gradle.kts` y ejecuta **app**. No necesita cámara ni permiso de micrófono. Los recursos de muestra están incluidos, así que puedes empezar sin Internet.

    Los archivos están en `app/src/main/java/es/mjsplayground/pmdm/multimedia/avanzada/edicion`:

    | Archivo | Preparado | Completarás |
    | --- | --- | --- |
    | `MainActivity.kt` | Picker, CropImageView, PNG y compartición | `recorte-proporcion` |
    | `Exportador.kt` | Efecto144p, eventos, progreso y cancelación | `media-audio` |
    | `EfectosSonoros.kt` | Carga asíncrona y liberación | `sonido-play` |
    | `StreamActivity.kt`, `Fuentes.kt` | Reproducción y métricas | Observarás y comprobarás |
    | `MidiBasico.kt` | Codificación de mensajes con rangos | Analizarás los bytes y sus límites |

    **Observa el código existente.** Carga el patrón propio y sigue el listener que cambia a Imagen lista. El recurso es un PNG de 800 × 600 de cuadrantes; no es una fotografía real. Guardar PNG ya funciona con un recorte libre. El botón de proporción todavía informa de etapa pendiente.

    **Comprueba.** Carga y guarda el patrón sin imponer proporción. Debe aparecer Recorte guardado y una salida decodificable. Elige otra imagen y cancela: se conserva la anterior. La exportación inicial de vídeo reduce altura, pero aún conserva audio; el texto de su botón identifica esa parte pendiente.

??? example "Adaptar la proporción del ejemplo"

    **Ejemplo resuelto de la técnica.** La [página de recorte](edicion-imagen.md#proporcion-y-ventana-visible) muestra una proporción 16:9 mediante `setAspectRatio`. La vista y el guardado asíncrono ya están conectados.

    **Ahora te toca — `recorte-proporcion`.** Completa `cuadrado` para fijar una relación 1:1. No cambies la resolución del patrón ni escales su bitmap a un cuadrado para simular el recorte.

    ??? tip "Pista — Un cociente, no un tamaño"

        Los dos argumentos expresan la relación entre ancho y alto. El límite de 1024 del exportador es otra decisión. La persona sigue escogiendo qué región conservar dentro de esa relación.

    **Comprueba.** Carga el patrón, activa Recorte cuadrado, desplaza la ventana, gira y guarda. El PNG debe tener ancho igual a alto y los cuadrantes correspondientes a la región elegida, con orientación modificada. No basta con ver una ventana cuadrada encima de la Preview.

??? example "Reutilizar una cadena de edición de vídeo"

    **Observa el código existente.** En `prepararVideo`, identifica MediaItem, EditedMediaItem y el efecto de altura 144. En `Exportador.iniciar`, sigue ruta nueva → listener → start. El callback, el porcentaje cuando está disponible y la limpieza de salida parcial están implementados.

    **Ahora te toca — `media-audio`.** Cambia la decisión del elemento editado para que el resultado no contenga una pista de audio. No silencies PlayerView y no modifiques el archivo de muestra original.

    ??? tip "Pista — Una pista se elimina antes de exportar"

        El builder de EditedMediaItem tiene una operación que decide si conservar audio. Es distinta de los efectos de vídeo y de los controles de volumen del reproductor.

    **Comprueba.** Pulsa Usar vídeo de muestra y exporta. Espera Vídeo exportado. La muestra de 320 × 240 con tono debe producir aproximadamente 192 × 144, duración positiva y ausencia de pista de audio; puede haber ajustes del codificador según el dispositivo. Abre la copia para comprobar imagen y silencio. El archivo original mantiene su tono.

    Elige después un vídeo propio más largo y cancela durante el trabajo. No debe compartirse una salida parcial. Sal de la Activity durante otra exportación: el controlador debe cancelar, sin seguir una operación invisible.

??? example "Del sonido cargado al stream"

    **Observa el código existente.** `EfectosSonoros` construye SoundPool, carga `R.raw.tono` y habilita la acción desde el callback. `onStart` crea y `onStop` libera ese propietario. La carga no debe repetirse por recomposición.

    **Ahora te toca — `sonido-play`.** Completa `reproducir`. Si la muestra no está lista, devuelve falso. Si está lista, solicita una reproducción con volumen 0,6 a ambos canales, prioridad 1, sin bucle y velocidad 1. Devuelve si se obtuvo un identificador de stream distinto de cero.

    ??? tip "Pista — El ID del archivo no es el de su reproducción"

        `sonido` identifica lo cargado. `play` devuelve otra identidad, o cero si no pudo solicitarse el stream. No anuncies haber escuchado el tono por el mero hecho de recibir ese número.

    **Comprueba.** La acción se habilita después de cargar y produce el tono sin reiniciar el motor. Repite, sal y vuelve. Comprueba tanto estado como salida audible disponible; si el host no permite escucharla, registra esa parte pendiente.

??? example "Observar una fuente y cerrar la integración"

    **Observa el código existente.** Abre `notaMidi` y predice los bytes para canal 2, nota 60 y velocidad 100. Compara con las pruebas locales. El botón Enumerar consulta el sistema; no necesita inventar un instrumento cuando la lista está vacía.

    **Ahora te toca comprobar.** Pulsa Reproducir muestra local. En la pantalla del reproductor debe aparecer Primera imagen recibida y verse el patrón. Volver libera el motor. Un valor cero en la estimación de red con ese recurso no acredita una conexión medida.

    Cuando el docente proporcione un recurso propio HTTPS/HLS o el emisor RTSP del README técnico, utiliza Abrir fuente configurada y registra preparación, primer frame, pausa, fin/error y salida. No introduzcas credenciales en la URL. Distingue reproducción local, transmisión de laboratorio y servicios de Internet.

    Retira `@org.junit.Ignore` de las clases Android al completar los tres TODO y ejecuta las cuatro pruebas normales. La prueba de stream usa el recurso local salvo que se configure otro mediante argumento técnico. Los tests JVM comprueban mensajes MIDI y contratos de URL; no requieren emulador.

    **Comprueba el conjunto.** Exporta y comparte PNG y MP4; cancela selectores y una exportación, abandona y vuelve. Revisa claro/oscuro, horizontal y fuente grande. No se garantiza restaurar una sesión de edición tras terminar el proceso; los archivos guardados son independientes de las referencias temporales de pantalla.

## Solución de referencia

[Mesa de edición completa — ZIP](../descargas/proyectos/multimedia-avanzada-edicion-solucion.zip){ .md-button }

Resuelve los tres TODO y mantiene recursos, clases y pruebas equivalentes. El README distingue la comprobación local de la prueba RTSP con servidor propio. Continúa con las [actividades autónomas](actividades.md).
