# Efectos sonoros y MIDI

## Elegir el motor según el trabajo

Un tono breve de interfaz, una canción larga y un mensaje MIDI no son el mismo tipo de contenido. Elegir una API depende de duración, concurrencia, control y origen, no de que todos produzcan algo relacionado con sonido.

| Necesidad | Opción adecuada para estudiar |
| --- | --- |
| Sonido breve que puede repetirse o solaparse | SoundPool |
| Audio/vídeo largo, streaming y eventos | Media3/ExoPlayer |
| Reproducción sencilla con API de plataforma | MediaPlayer, con su ciclo de vida y formatos admitidos |
| Notas, controles y comunicación con instrumentos | API MIDI; necesita un receptor que interprete eventos |

No cargues una película en SoundPool ni abras un ExoPlayer nuevo por cada pulsación de un efecto corto. Las capacidades y formatos de [audio Android](https://developer.android.com/media/platform/supported-formats) deben comprobarse en los dispositivos objetivo.

## SoundPool: cargar y después solicitar un stream

SoundPool prepara muestras para reproducirlas con latencia reducida y permite varias reproducciones simultáneas. `maxStreams` limita streams activos, no el número de archivos diferentes que podrían cargarse. El proyecto configura dos y utiliza atributos de sonificación para describir la intención del audio.

`load` devuelve un ID de sonido, pero la carga no ha terminado necesariamente. `setOnLoadCompleteListener` confirma que el resultado está listo cuando su código de estado es cero. La pantalla mantiene la acción deshabilitada hasta ese momento.

```kotlin title="Fragmento del ejemplo de carga"
pool.setOnLoadCompleteListener { _, _, resultado -> listo = resultado == 0 }
sonido = pool.load(contexto, R.raw.tono, 1)
```

El [contrato SoundPool](https://developer.android.com/reference/android/media/SoundPool) distingue ID de muestra e ID de stream. `play` devuelve el de una reproducción concreta, o cero si no pudo iniciarse. La muestra se puede reutilizar; dos llamadas no representan necesariamente el mismo stream.

Los argumentos de `play` incluyen volumen izquierdo y derecho, prioridad, repeticiones y velocidad. Volumen usa 0…1; cero repeticiones significa una reproducción y −1, bucle. La tasa altera la velocidad dentro del intervalo admitido. En la práctica comenzarás con volumen moderado, sin bucles y velocidad normal.

`release` devuelve los recursos. El laboratorio crea SoundPool al entrar en primer plano y lo libera al salir. No se intenta reproducir con una instancia liberada. Que se acepte un stream no prueba que el altavoz esté activo: hay que comprobar salida y volumen.

## Un archivo de audio frente a una secuencia MIDI

PCM contiene muestras que representan la onda sonora. Un MP3 o AAC codifica audio comprimido. MIDI representa eventos como «nota 60, velocidad 100» o cambios de controlador. El timbre final depende del instrumento o sintetizador que recibe esos eventos; no se recupera una grabación original de voz a partir de una nota MIDI.

En MIDI 1.0, un mensaje de canal utiliza un byte de estado y bytes de datos. Note On tiene familia hexadecimal `0x90`; el nibble inferior indica el canal de 0 a 15. Nota y velocidad utilizan siete bits, con valores 0…127. Note Off utiliza la familia `0x80`.

El helper `notaMidi` del proyecto construye esos tres bytes y valida rangos. Para canal 2, nota 60 y velocidad 100 genera `92 3C 64` en hexadecimal. No envía ni sintetiza ese mensaje por sí solo. Las pruebas JVM comprueban bytes, cambio a Note Off y rechazo de valores que desbordarían el contrato.

## Dispositivos, puertos y recepción

`MidiManager` descubre dispositivos y permite abrirlos de forma asíncrona. Un dispositivo tiene puertos de entrada y salida según su perspectiva: enviar a un instrumento utiliza su puerto de entrada, aunque nuestra app actúe como emisor. Los puertos y el dispositivo deben cerrarse cuando dejan de usarse.

Este fragmento presupone un `MidiInputPort` ya abierto y autorizado; no es un sintetizador completo:

```kotlin title="Fragmento de envío MIDI 1.0"
val mensaje = notaMidi(canal = 0, nota = 60, velocidad = 100, encendida = true)
puerto.send(mensaje, 0, mensaje.size)
```

El API de [MidiManager](https://developer.android.com/reference/android/media/midi/MidiManager) documenta el descubrimiento y los transportes. Una conexión USB puede requerir autorización y un instrumento compatible. Bluetooth MIDI tiene su propio contexto de dispositivos y permisos; no se acredita utilizando solamente esta función de bytes.

El botón del laboratorio enumera dispositivos mediante el API compatible con el mínimo26. La compatibilidad con MIDI 2.0 y Universal MIDI Packets requiere distinguir transporte y versión; no se representa con estos tres bytes ni se da por comprobada con la enumeración del ejemplo.

## Comprobar sin inventar un instrumento

El tono WAV es original y está incluido; permite observar la carga de SoundPool sin Internet. En el emulador puede verificarse la petición y sus estados, pero la escucha depende también de la salida del host.

Para MIDI, una lista vacía es un resultado válido. La prueba de codificación no demuestra que haya llegado una nota a un instrumento físico. Si dispones de uno, registra dispositivo, puerto, canal y resultado, e incluye Note Off para no dejar una nota sostenida. Sin él, la comunicación física queda pendiente; no se muestra una cifra de dispositivos inventada.
