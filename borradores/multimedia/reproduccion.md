# Reproducción de audio y vídeo

## Contenedor, códec y origen

Un archivo audiovisual reúne información que el reproductor debe interpretar. El **contenedor** organiza pistas y metadatos; el **códec** define cómo se representa una pista comprimida. Que un nombre termine en `.mp4` no identifica por sí solo el códec del vídeo ni garantiza que cualquier dispositivo pueda decodificarlo.

El audio incluye muestras sonoras; el vídeo añade imágenes sincronizadas en el tiempo y puede contener audio y subtítulos. Un WAV sencillo puede transportar audio PCM sin compresión; un vídeo MP4 puede combinar pistas codificadas de distintas maneras. Las capacidades reales dependen del formato y del dispositivo, no solo de la extensión.

La fuente también importa. Un recurso incluido en `res/raw` está disponible sin conexión. Una URI elegida mediante Photo Picker depende del acceso al proveedor. Una fuente HTTPS necesita red, autorización cuando proceda y gestión de demoras. Empezaremos con un audio propio incluido en el proyecto y después un vídeo seleccionado por el usuario para separar fallos de reproducción de fallos de conectividad.

La [documentación de formatos compatibles](https://developer.android.com/media/platform/supported-formats) sirve para contrastar capacidades; el resultado debe comprobarse en el dispositivo objetivo.

## Media3 y ExoPlayer

**Media3** es el conjunto de bibliotecas Jetpack para reproducción, interfaces y sesiones multimedia. **ExoPlayer** es su implementación de reproductor. **PlayerView** proporciona una vista con superficie de vídeo y controles. Son piezas relacionadas, pero crear la vista no prepara automáticamente una fuente.

El laboratorio fija Media3 1.9.4 y utiliza los artefactos de reproducción y de interfaz de esa misma versión. No mezcles imports antiguos `com.google.android.exoplayer2` con los de `androidx.media3`: los nombres de algunas clases son parecidos, pero pertenecen a otra generación de bibliotecas.

El recorrido mínimo de una fuente es construir un `MediaItem`, asignarlo al reproductor, prepararlo y solicitar reproducción. Este fragmento pertenece al controlador preparado de la práctica:

```kotlin title="Núcleo de carga de una fuente"
player.setMediaItem(MediaItem.fromUri(uri))
player.prepare()
player.play()
```

`MediaItem` se importa de `androidx.media3.common`. La URI puede identificar el audio local o el vídeo seleccionado. `prepare` inicia el trabajo de preparación; no significa que la reproducción haya comenzado en el mismo instante. `play` expresa la intención de reproducir cuando el reproductor pueda hacerlo.

El archivo `ControlReproductor` concentra la instancia ExoPlayer y sus operaciones. La pantalla no construye otro reproductor en cada recomposición ni ejecuta esa secuencia continuamente mientras se dibuja. La [guía inicial de ExoPlayer](https://developer.android.com/media/media3/exoplayer/hello-world) describe la creación y la preparación de medios.

## Reproducir, pausar, detener y liberar

Estas acciones tienen significados diferentes:

| Acción | Qué representa |
| --- | --- |
| Reproducir | Solicitar avance del medio cuando esté preparado |
| Pausar | Conservar el medio y la posición sin continuar avanzando |
| Detener | Pasar a estado detenido/inactivo y dejar de preparar o reproducir |
| Liberar | Devolver recursos del reproductor; esa instancia no se reutiliza |

`stop()` no sustituye a `release()`. Tampoco debes suponer que Detener implica siempre volver a cero sin expresarlo en el controlador: posición y estado de preparación son decisiones separadas. Si el laboratorio reinicia la reproducción, el código debe solicitar la preparación o el desplazamiento que corresponda.

Al terminar el medio, pulsar reproducir puede necesitar reiniciar su posición según el comportamiento previsto. Prueba también esa transición: una app que funciona solo la primera vez no demuestra un control correcto de la vida del reproductor.

## Estados y eventos observables

El estado de reproducción describe si el reproductor puede producir contenido. Sus constantes principales son:

| Estado | Interpretación |
| --- | --- |
| `Player.STATE_IDLE` | Sin preparación activa; situación inicial o tras detener |
| `Player.STATE_BUFFERING` | Necesita preparar u obtener datos antes de avanzar |
| `Player.STATE_READY` | Tiene lo necesario para reproducir, aunque puede estar pausado |
| `Player.STATE_ENDED` | Se alcanzó el final del medio |

Estar en READY no equivale a estar reproduciendo. Para saber si avanza realmente, utilizaremos `isPlaying`. La intención `playWhenReady` puede ser verdadera mientras se almacena en búfer o existe otra condición que impide reproducir.

Un `Player.Listener` recibe eventos. Este ejemplo muestra las dos respuestas que utilizamos para ligar reproducción y pantalla; los comentarios señalan dónde el controlador publica su estado:

```kotlin title="Fragmento de registro de eventos"
player.addListener(object : Player.Listener {
    override fun onIsPlayingChanged(isPlaying: Boolean) {
        // Publicar isPlaying para actualizar texto y animación.
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        // Representar preparación, disponibilidad y final según este valor.
    }

    override fun onPlayerError(error: PlaybackException) {
        // Presentar un fallo comprensible y conservar una acción de recuperación.
    }
})
```

`Player` y `PlaybackException` proceden de `androidx.media3.common`. No actualices la animación a `true` simplemente porque se pulsó Reproducir: puede que la fuente no se abra. El evento permite reflejar lo que ha ocurrido en el reproductor.

La [guía de eventos Media3](https://developer.android.com/media/media3/exoplayer/listening-to-player-events) explica también la relación entre cambios individuales y eventos agrupados. En el laboratorio basta con interpretar las respuestas que necesita la pantalla.

## Integrar PlayerView en Compose

Una vista Android existente puede alojarse dentro de Compose mediante `AndroidView`. Su `factory` crea la vista; `update` conecta los valores que cambian. En este caso la vista necesita recibir la instancia de `Player` que posee el controlador:

```kotlin title="Fragmento del contenedor de vídeo"
AndroidView(
    factory = { context -> PlayerView(context) },
    update = { view -> view.player = player },
    modifier = Modifier.fillMaxWidth().height(220.dp)
)
```

`AndroidView` se importa de `androidx.compose.ui.viewinterop`, y `PlayerView`, de `androidx.media3.ui`. Las unidades y modificadores son los que ya conocemos. La altura del ejemplo es una decisión de presentación, no la resolución a la que se recodifica el vídeo.

La vista y el reproductor tienen identidades distintas: recrear la vista no justifica abrir otro decodificador; liberar el reproductor obliga a no seguir usándolo como si estuviera activo. El código preparado mantiene ese vínculo y gestiona las anotaciones de API de Media3 que correspondan.

Consulta [PlayerView](https://developer.android.com/media/media3/ui/playerview) y la [interoperabilidad de vistas en Compose](https://developer.android.com/develop/ui/compose/migrate/interoperability-apis/views-in-compose) para comprender el contenedor. No necesitas aprender todavía efectos avanzados de Compose para utilizar este wrapper preparado.

## El ciclo de vida protege recursos

Un reproductor conserva decodificadores, memoria y recursos asociados a la salida audiovisual. Dejarlo activo cuando la pantalla abandona el primer plano puede producir sonido inesperado y consumo innecesario. Nuestro laboratorio es un reproductor de primer plano: al dejar de ser visible, se liberará.

`MainActivity` coordina esa duración mediante el ciclo de vida. `onStart` dispone el controlador/reproductor necesario para la pantalla; `onStop` libera los recursos de reproducción. Cuando vuelve a empezar, se crea una instancia válida en vez de intentar utilizar una que ya recibió `release`.

El wrapper mantiene el orden de las llamadas heredadas y actualiza la referencia que recibe la vista. No sobrescribas una llamada de ciclo de vida aislada sin leer las otras: iniciar sin liberar produce fugas; liberar sin volver a preparar deja controles que parecen disponibles pero ya no tienen reproductor.

Abrir Photo Picker también puede sacar nuestra actividad de primer plano. El resultado de selección debe llegar a un controlador vigente al regresar, o quedar pendiente para que se cargue cuando vuelva a estar disponible. Ese recorrido se comprueba aparte de pulsar un botón de audio local.

La reproducción en segundo plano requiere otro diseño, normalmente con MediaSession y el servicio correspondiente. Se desarrolla como ampliación; no se consigue omitiendo `release` en este laboratorio.

## Elegir un vídeo y gestionar errores

Para vídeo utilizamos el mismo contrato Photo Picker aprendido con imágenes, seleccionando `VideoOnly`. Una cancelación no es un error del reproductor. Una URI recibida sí puede fallar después al abrirse o contener una codificación no compatible.

Ante un error, la interfaz debe retirar el indicador de reproducción, explicar que no pudo reproducirse el medio y permitir elegir otro o volver al recurso local. Durante depuración registra la categoría del fallo, el origen y el dispositivo, sin publicar el contenido privado de la selección.

Las fuentes remotas añaden errores HTTP, tiempos de espera y caídas de red. No pruebes una API de control usando únicamente un archivo remoto desconocido: primero demuestra que el audio local funciona y después incorpora variables de red.

## Qué hay que comprobar realmente

Observa comienzo, pausa, reanudación, detención, final, error y salida de la actividad. Comprueba que el vídeo tenga imagen y que los controles correspondan a la fuente seleccionada. Un contador o una etiqueta «Reproduciendo» no prueban que se emita sonido: también hay que escucharlo o verificar la salida con un procedimiento apropiado.

Un emulador puede tener el sonido silenciado o una salida del host diferente. Un teléfono puede interrumpirse por otras aplicaciones y tener capacidades de códec distintas. Registra cada caso probado y evita afirmar que todos los formatos funcionan porque un WAV local se ha preparado correctamente.
