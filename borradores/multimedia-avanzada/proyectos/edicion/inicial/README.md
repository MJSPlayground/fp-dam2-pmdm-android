# Mesa de edición — inicial

Abre **esta carpeta** mediante Open en Android Studio, espera la sincronización y ejecuta **app**. SDK 36, mínimo API 26 y JDK de Gradle 17 o JBR 21 del IDE. No abras el directorio que contiene inicial y solución como un solo proyecto Gradle.

## Funcionamiento

Carga el patrón propio y usa el editor de recorte. El clip de muestra está incluido para exportar sin depender de red. La pantalla de reproducción permite usar la muestra local o una dirección HTTPS/HLS/RTSP configurada; no es un extractor de YouTube.

El editor usa CropImageView 4.7.0, evitando la Activity/contrato obsoletos señalados por el mantenedor. Transformer 1.9.4 reduce la altura a 144 y exporta MP4; los eventos, progreso cuando existe y cancelación están implementados. SoundPool carga el tono de forma asíncrona y libera al salir. El inventario MIDI no presupone un instrumento y el helper de bytes no produce sonido por sí solo.

## Trabajo del alumnado

Tres TODO reales: `recorte-proporcion`, `media-audio`, `sonido-play`. El recorte libre y el efecto de altura ya funcionan, pero la exportación inicial todavía conserva audio y SoundPool no solicita el stream hasta completar su método.

## Pruebas y alcance

- JVM: tres casos de prueba para bytes/rangos MIDI y contratos de fuente, sin red ni emulador.
- Android: PNG realmente cuadrado, exportación real con altura 144 y sin audio, carga/solicitud/liberación SoundPool y primer frame de reproducción.
- La suite normal usa recursos propios incluidos. El test de stream acepta opcionalmente `fuentePrueba` para una dirección preparada; no obliga a tener servidor siempre activo.
- Resultado observado: solución con build y lint correctos, tres tests JVM, cuatro instrumentados normales y una ejecución adicional del stream con emisor local RTSP/TCP. La salida exportada también se decodificó con FFmpeg: H.264 Baseline 192×144, sin audio.
- Cámara física, instrumento MIDI, escucha física del altavoz y servicios HTTPS/HLS/YouTube arbitrarios no quedan certificados por esas pruebas. Consultar el estado global del repositorio para el informe integrado.

Versiones específicas: Media3 1.9.4 en todos sus módulos, Android Image Cropper 4.7.0 (`com.vanniktech:android-image-cropper`, Apache-2.0). [Fuente del mantenedor](https://github.com/CanHub/Android-Image-Cropper/tree/4.7.0) y [Transformer](https://developer.android.com/media/media3/transformer/getting-started). Los recursos sintéticos y su generación se documentan en RECURSOS.md.

## Emisor técnico RTSP opcional

Es una preparación del equipo que facilita el recurso, no un requisito para abrir el proyecto Android. Se comprobó con MediaMTX 1.21.3 (MIT) y FFmpeg 9.0.2, usando solo el clip propio incluido.

1. Descarga la distribución Windows amd64 de [MediaMTX 1.21.3](https://github.com/bluenviron/mediamtx/releases/tag/v1.21.3) en una carpeta de herramientas propia. No se incorpora su binario a este ZIP.
2. Copia `servidor-rtsp.yml` al directorio de la herramienta y arranca `mediamtx.exe servidor-rtsp.yml` desde allí. El archivo desactiva otros protocolos y limita RTSP/TCP a loopback, puerto 8558. No es una configuración de publicación en Internet.
3. En otra terminal técnica publica el clip incluido mediante FFmpeg:

```text
ffmpeg -re -stream_loop -1 -i muestra.mp4 -c copy -rtsp_transport tcp -f rtsp rtsp://127.0.0.1:8558/aula
```

En el emulador usa `rtsp://10.0.2.2:8558/aula`. El servidor registra un lector TCP y el cliente debe recibir una primera imagen. La prueba adicional del encargo verificó ese recorrido y el cierre de sesión; no midió pérdidas UDP ni retardo de cámara física. Un teléfono necesitaría otra configuración de dirección, red y acceso explícitamente preparada.

Al terminar, detén FFmpeg y el servidor. No dejes un emisor en bucle sin necesidad ni expongas su puerto fuera del laboratorio.

## Compilación y pruebas técnicas

La ruta habitual del alumnado es Android Studio y el botón Run. Comprobación técnica opcional desde esta carpeta:

```powershell
.\gradlew.bat :app:assembleDebug :app:lintDebug :app:assembleDebugAndroidTest --console=plain
.\gradlew.bat :app:connectedDebugAndroidTest --console=plain
```

En el inicial las clases instrumentadas llevan @Ignore; retíralo al completar la guía. Omitir una prueba no equivale a superarla. Ambas etapas comparten applicationId: instalar una sustituye la otra. Conserva tus archivos en otra carpeta antes de reemplazar tu trabajo.

Código y recursos originales bajo Apache-2.0; LICENSE y avisos del wrapper incluidos. Las dependencias mantienen sus licencias. No hay claves, cuentas ni contenidos de alumnado.

L?mite de sesi?n: al girar la pantalla o cambiar el tema, la actividad se recrea y hay que volver a cargar la entrada. Los archivos ya exportados permanecen en el almacenamiento privado; no se implementa un cat?logo ni la recuperaci?n del proyecto de edici?n.
