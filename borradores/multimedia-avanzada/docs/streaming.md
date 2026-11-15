# Reproducción remota y streaming

## Archivo, descarga progresiva y directo

Un recurso local se lee del dispositivo. Un archivo remoto por HTTPS puede reproducirse conforme llegan bytes, sin esperar a descargarlo entero: descarga progresiva. Una transmisión en directo incorpora contenido que se está produciendo; que la URL empiece por HTTPS no determina por sí solo si es directo o un archivo almacenado.

Un formato adaptativo como HLS describe variantes y segmentos mediante manifiestos. El reproductor puede elegir una variante según capacidad y condiciones de red. No es una lista de imágenes sueltas ni una orden de «descargar todo el vídeo». La [guía de HLS en Media3](https://developer.android.com/media/media3/exoplayer/hls) explica el módulo y los formatos admitidos.

El laboratorio incluye reproducción progresiva, HLS y RTSP con módulos Media3 1.9.4. DASH necesitaría su módulo específico; no se activa por añadir HLS. Todas las bibliotecas Media3 deben mantenerse en la misma versión.

## Una pantalla dedicada al reproductor

Mesa de edición abre `StreamActivity` para reproducir una muestra local o una dirección configurada. La Activity crea ExoPlayer al comenzar, conecta PlayerView y lo libera al dejar de estar visible. No compite con una exportación que continúe indefinidamente al fondo: salir del editor cancela esa exportación.

La [gestión de medios de ExoPlayer](https://developer.android.com/media/media3/exoplayer/media-items) utiliza MediaItem para describir una entrada. El recorrido básico sigue siendo asignar, preparar y solicitar reproducción; ahora pueden existir demoras y errores de transporte además de fallos de decodificación.

La dirección del laboratorio admite HTTPS y RTSP sin credenciales incrustadas. La muestra propia utiliza internamente `android.resource`. Se rechazan `file://`, esquemas ajenos y direcciones con usuario/contraseña. La validación de sintaxis no certifica que un servidor exista ni que su contenido tenga permiso de uso.

## Preparación no significa reproducción efectiva

Un recurso puede estar en BUFFERING porque todavía no dispone de datos suficientes. READY indica que puede reproducirse, pero la persona puede haber pulsado Pausa. ENDED significa que alcanzó el final; un directo normalmente no termina igual que un archivo de dos segundos.

`onPlayerError` comunica un fallo de reproducción que la UI debe presentar. No se interpreta cada error transitorio de carga de AnalyticsListener como si el medio hubiese fallado definitivamente: el motor puede recuperarse internamente. Registra categorías y estados sin publicar URI privadas ni credenciales.

El laboratorio añade una señal de **primera imagen recibida** desde el evento real de renderizado. Esa señal demuestra que se presentó un frame de esa sesión, aunque no prueba que toda la reproducción vaya a mantenerse estable ni que el audio se haya escuchado.

## Qué mide la pantalla

`AnalyticsListener` permite observar estimación de ancho de banda y fotogramas descartados. Los callbacks concretos se contrastan con el [código de la versión](https://github.com/androidx/media/blob/1.9.4/libraries/exoplayer/src/main/java/androidx/media3/exoplayer/analytics/AnalyticsListener.java).

La estimación se expresa en bit/s; la pantalla la divide entre 1000 para mostrar kbit/s. No es el bitrate del archivo ni una velocidad garantizada de la conexión. Una muestra local puede no generar una estimación de red útil, y RTSP no ofrece necesariamente los mismos eventos que una descarga HTTP.

Los fotogramas descartados describen la presentación de vídeo. No equivalen al número de paquetes RTP perdidos. Un equipo lento puede descartar frames con una red perfecta; una red con pérdidas puede recuperarse antes de que el renderizador descarte alguno. La [página de RTP](rtp.md) separa esas capas.

## Preparar una comprobación de red reproducible

Empieza con Reproducir muestra local. Debe aparecer la imagen del patrón y terminar; así compruebas vista, motor y códec sin añadir un servidor a la primera prueba.

Para HTTPS/HLS, utiliza una URL válida de un recurso propio o autorizado y que el docente haya preparado. Comprueba carga, reproducción, pausa, red interrumpida, error y recuperación. No uses una página web de YouTube como si fuera una URL de MP4: su integración tiene otro contrato.

Para RTSP se ofrece un procedimiento técnico de emisor local en el README del editor. Se utiliza el vídeo sintético incluido y un servidor propio, sin depender de una cámara ajena. En el emulador `10.0.2.2` alcanza al host; una prueba entre ordenadores o con teléfono necesita la dirección y red reales y una configuración de acceso específica.

El permiso INTERNET es normal, no un diálogo de cámara o galería. RTSP de laboratorio no implica un canal cifrado para contenido privado; usa material propio no sensible y una red controlada. Ninguna prueba local se presenta como verificación de todos los servicios de Internet.
