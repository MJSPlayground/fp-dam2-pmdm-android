# Práctica: reproductor y animación de estado

## Preparar una reproducción reproducible

Lee [reproducción multimedia](reproduccion.md) y [animaciones](animaciones.md). El laboratorio utiliza un sonido propio incluido para poder empezar sin conexión ni cuentas; también permite seleccionar contenido de vídeo compatible.

[Proyecto inicial — ZIP](../descargas/proyectos/multimedia-reproductor-inicial.zip){ .md-button .md-button--primary }

Abre el proyecto en Android Studio y ejecuta **app**. Identifica los controles, el área de vídeo y el texto de estado. El sonido de ejemplo es un recurso del laboratorio; su procedencia está documentada en el proyecto. No necesitas descargar una canción o un vídeo con derechos inciertos.

## Crear un reproductor y un propietario

En `player-crear`, construye ExoPlayer y relaciona su duración con la Activity. El controlador debe permitir cargar, reproducir, pausar, detener y liberar. Una recomposición de Compose no debe construir otra instancia por el simple hecho de actualizar un texto.

Al trabajar con vídeo, `PlayerView` presenta la salida del reproductor dentro de `AndroidView`. Ese puente permite alojar una vista Android en Compose; no requiere reescribir el motor de vídeo. Mantén todas las dependencias Media3 en la misma versión del curso.

Observa la diferencia entre tener un objeto Player, tener un elemento de medio y haber preparado su lectura. Antes de cargar un medio válido, pulsar Reproducir no demuestra que exista contenido.

## Cargar y preparar

Completa `player-cargar`. Crea un `MediaItem` con la URI elegida, entrégalo al reproductor y llama a `prepare`. El sonido local evita depender de una respuesta de red para comprobar este recorrido.

La URI del selector de vídeo se maneja como contenido autorizado, no como una ruta construida a partir de su nombre. Cancelar el selector debe conservar la selección anterior. Si eliges un archivo que el dispositivo no puede decodificar, el reproductor debe comunicarlo mediante su evento de error.

Ejecuta la aplicación, carga el recurso de audio y pulsa Reproducir. Comprueba que cambia el estado y que la posición avanza. La comprobación automatizada de esos eventos no verifica por sí sola la salida audible de un altavoz físico.

## Responder a eventos

En `player-eventos`, registra un `Player.Listener` para recibir cambios. Distingue estado de preparación (`IDLE`, `BUFFERING`, `READY`, `ENDED`) de `isPlaying`: un reproductor preparado puede estar pausado.

La interfaz presenta el estado recibido del motor; no debe afirmar «reproduciendo» únicamente porque se haya pulsado un botón. Prueba esta secuencia:

1. Cargar y esperar hasta preparado.
2. Reproducir y observar avance.
3. Pausar: la posición debe dejar de avanzar.
4. Volver a reproducir y alcanzar el final.
5. Detener y preparar de nuevo cuando sea necesario para reanudar.

Incluye un caso de URI no disponible o contenido inválido. Debe aparecer un mensaje comprensible y mantenerse la posibilidad de elegir otro medio. No conviertas un error en «fin normal de reproducción».

## Animar una información real

Asocia `AnimatedVisibility` a una propiedad observable del reproductor, como `isPlaying`. El indicador aparece al reproducir y desaparece al pausar o terminar; no uses un contador independiente que pueda quedar desincronizado.

Una animación describe la transición entre dos estados. No necesita un bucle manual que modifique la interfaz sin descanso. Comprueba que pausar durante la transición acaba dejando el indicador en el estado correcto y que la lectura del estado no depende solo de su color o movimiento: conserva también un texto.

## Liberar al abandonar la pantalla

Completa `player-liberar` y revisa el ciclo de vida. Este laboratorio reproduce mientras la app está visible; no implementa reproducción de fondo ni MediaSession. Al ir a Inicio o a otra app deben liberarse los recursos previstos y no continuar el audio de forma accidental.

Vuelve a abrir la aplicación y comprueba que puede crear y preparar otra instancia sin reutilizar un reproductor ya liberado. Si conservas la posición para restaurarla, diferencia ese dato sencillo de conservar el objeto Player: un objeto liberado no se guarda como estado restaurable.

La Activity debe reenviar los eventos del ciclo de vida al controlador. Dejar la limpieza únicamente en el código de un botón Stop no cubre que el usuario abandone la aplicación con un gesto del sistema.

## Consolidación

Selecciona un vídeo propio, reproduce y pausa; comprueba imagen, controles y proporción. Repite el paso de fondo y vuelta. Después explica qué eventos permitirían distinguir «preparado y pausado», «esperando datos», «terminado» y «fallo».

Anota la API, el medio utilizado, los estados observados y la prueba audible/visual realmente realizada. No escribas «todos los formatos son compatibles» por haber reproducido un único archivo: formato contenedor, códec y soporte del dispositivo son factores diferentes.

[Solución formativa completa — ZIP](../descargas/proyectos/multimedia-reproductor-solucion.zip){ .md-button }

El controlador, los recursos y el código de ciclo de vida están incluidos. Revisa su README para ejecutar las pruebas disponibles y diferenciar las verificaciones automatizadas de las manuales.
