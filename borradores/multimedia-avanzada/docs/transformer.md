# Transformación de vídeo con Media3

## Reproducir y exportar son trabajos distintos

ExoPlayer presenta un medio; **Transformer** crea un archivo nuevo a partir de una edición. Su entrada puede ser un vídeo propio seleccionado por URI. La salida del laboratorio es MP4, con vídeo H.264 y sin audio. El original permanece intacto.

Un **códec** representa una pista comprimida; el contenedor organiza las pistas. Transcodificar implica decodificar y volver a codificar. Cuando las pistas ya son compatibles y no hay cambios que exijan procesarlas, una herramienta puede reempaquetarlas sin recodificar. Renombrar una extensión no realiza ninguna de esas operaciones.

El laboratorio fija Media3 1.9.4 para Transformer, efectos y reproductor. No mezcla versiones entre módulos. La [guía inicial](https://developer.android.com/media/media3/transformer/getting-started) describe entrada editada, exportador y finalización. El [código de esa versión](https://github.com/androidx/media/tree/1.9.4/libraries/transformer) permite contrastar firmas cuando una guía posterior utiliza otra combinación.

## Describir la edición antes de ejecutarla

`MediaItem` identifica el recurso. `EditedMediaItem` añade las decisiones de edición: retirar pistas y aplicar efectos. `Effects` contiene procesadores de audio y efectos de vídeo; no introduce una transformación automáticamente por el mero hecho de crear una lista vacía.

El ejemplo resuelto aplica un cambio de altura mediante `Presentation.createForHeight(144)`. Se elige una resolución pequeña para que el patrón de prueba pueda transformarse rápidamente y mostrar un cambio observable de 320 × 240 a aproximadamente192 ×144, manteniendo proporción. No es una recomendación de calidad para vídeos de producción.

```kotlin title="Fragmento de un efecto de presentación"
val efectos = Effects(
    emptyList(),
    listOf(Presentation.createForHeight(144))
)
```

El primer argumento es la lista de procesadores de audio; el segundo, la de efectos de vídeo. Se utilizan argumentos posicionales porque el constructor pertenece a una clase Java. Los efectos de [transformación](https://developer.android.com/media/media3/transformer/transformations) también permiten estudiar recorte, giro y cambios de color sin confundirlos con el tamaño del control PlayerView.

La decisión de retirar audio se expresa en el constructor del elemento editado. En la práctica completarás esa decisión después de estudiar cómo se conecta el efecto ya preparado. No se intenta bajar el volumen de un reproductor para fingir que desapareció una pista del archivo.

## Iniciar, observar y terminar

`Transformer.Builder` elige los MIME de salida y registra un `Transformer.Listener`. `start` recibe el elemento editado y una ruta de salida nueva. Este trabajo es asíncrono: el método puede devolver antes de que exista un MP4 completo.

La app interpreta `onCompleted` como finalización válida y comprueba que hay archivo no vacío. `onError` comunica el fallo; la URI de entrada puede haber dejado de ser accesible o el dispositivo puede no admitir la combinación de decodificación y codificación.

Al consultar progreso, `getProgress` devuelve un estado además de llenar un `ProgressHolder` cuando es posible. Puede no haber porcentaje disponible. La UI muestra una espera sin inventar un avance numérico; un porcentaje obtenido describe trabajo del exportador y no una medida de calidad de vídeo.

Transformer debe utilizarse desde el hilo de aplicación con el que se creó. El controlador del ejemplo lo construye, inicia y cancela en el hilo principal; el procesamiento interno no se ejecuta como un bucle bloqueante de nuestra UI. No lo traslades arbitrariamente a un hilo y después llames a cancelar desde otro.

## Cancelación, errores y ciclo de vida

`cancel()` detiene la operación, pero la biblioteca puede dejar el archivo parcial. El controlador elimina exclusivamente su destino temporal. Al salir del editor, la exportación se cancela. Si el producto necesitase seguir trabajando fuera de esa pantalla, habría que definir otro propietario y cumplir las restricciones de trabajos de fondo; no se logra ignorando el ciclo de vida.

Una exportación nueva no borra resultados anteriores válidos. Tras un error la persona puede elegir otra entrada o reintentar. La app no promete admitir cualquier códec, HDR, tamaño o duración: esas capacidades dependen del motor y del dispositivo.

## Comprobar el archivo, no solo la etiqueta

El recurso propio `muestra.mp4` tiene patrón visual y tono, duración de2s y dimensiones320 ×240. La prueba instrumentada ejecuta Transformer real y utiliza `MediaMetadataRetriever` para comprobar altura144, duración positiva y ausencia de pista de audio. No se limita a buscar un texto de éxito.

Para verificar el códec puede utilizarse también `MediaExtractor`: selecciona el track de vídeo y lee `MediaFormat.KEY_MIME`. Para una comprobación funcional completa, abre el resultado en otro reproductor y confirma imagen y silencio. Una salida aceptada por el muxer no demuestra por sí sola calidad visual, sincronización ni compatibilidad con todos los receptores.

Repite después con un vídeo propio más largo y una cancelación durante el trabajo. Un archivo de muestra sintético no acredita cámaras físicas, contenido HDR ni codificadores de otros modelos.
