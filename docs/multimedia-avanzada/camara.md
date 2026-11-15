# Visor y captura con CameraX

## De delegar la cámara a integrarla

`TakePicture` pedía a otra aplicación que hiciera una fotografía. **CameraX** permite que el visor y las acciones de captura formen parte de nuestra pantalla. A cambio, nuestra app debe gestionar permiso, cámara disponible y duración de los recursos. CameraX se apoya en la infraestructura de cámara de Android e incorpora compatibilidad entre dispositivos; no sustituye el sensor físico ni garantiza las mismas capacidades en todos.

El laboratorio fija CameraX 1.5.3 y utiliza `camera-camera2`, `camera-lifecycle`, `camera-view` y `camera-video`. Todos los artefactos comparten versión. Se mantiene `minSdk = 26` y el stack común del curso. La [publicación de esta versión](https://developer.android.com/jetpack/androidx/releases/camera#1.5.3) incluye correcciones de lectura de JPEG; el proyecto no necesita adoptar una versión preliminar para integrar un visor.

## Casos de uso con responsabilidades distintas

CameraX denomina **caso de uso** a una salida o trabajo de cámara. No es el mismo significado que una clase de dominio como `AlternarLectura`.

| Caso | Responsabilidad en el laboratorio |
| --- | --- |
| `Preview` | Entregar el flujo visible al visor |
| `ImageCapture` | Solicitar una fotografía y comunicar su guardado o error |
| `VideoCapture<Recorder>` | Proporcionar la salida de vídeo que administra un Recorder |

`PreviewView` es una vista Android que presenta el flujo. Compose la aloja con `AndroidView`. `ProcessCameraProvider` conecta los casos con una cámara y el ciclo de vida de la Activity. Su obtención es asíncrona: no uses el proveedor como si `getInstance` lo devolviese ya preparado de forma inmediata.

La [arquitectura CameraX](https://developer.android.com/media/camera/camerax/architecture) explica los casos y el vínculo con `LifecycleOwner`. Nuestro `MainActivity` mantiene una vista por Activity y no construye otro proveedor por recomposición.

## Permiso y preparación del visor

El manifiesto declara CAMERA y la característica de cámara como opcional. Al pulsar Activar cámara se comprueba el permiso; si falta, un contrato de Activity Result lo solicita. La denegación deja una explicación y no fabrica una imagen de sustitución que se presente como captura real.

Cuando el proveedor está disponible, comprobamos una cámara trasera. El caso Preview recibe la superficie del visor. Este fragmento pertenece al callback preparado del proyecto:

```kotlin title="Fragmento de conexión del visor"
val preview = Preview.Builder().build().also {
    it.surfaceProvider = visor.surfaceProvider
}
proveedor.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview, captura)
```

`captura` representa el caso de fotografía o vídeo seleccionado. La app utiliza dos modos: Preview + ImageCapture, o Preview + VideoCapture. No presupone que cualquier cámara admita los tres casos simultáneamente. Cambiar de modo retira las conexiones anteriores y solo está permitido fuera de una captura o grabación activa.

## Fotografiar y confirmar el resultado

`ImageCapture` puede guardar directamente en un archivo. El proyecto crea un JPEG nuevo dentro de `filesDir/medios` y configura `OutputFileOptions`; no obtiene el archivo de una captura de pantalla del visor.

`takePicture` recibe las opciones, el executor de respuesta y un callback. `onImageSaved` confirma que la operación terminó; `onError` comunica un fallo de captura. Mostrar «Guardado» inmediatamente después del clic sería adelantar un resultado que todavía no existe.

Antes de capturar se actualiza `targetRotation` con la rotación de la pantalla. Esto comunica la orientación deseada, pero no elimina la necesidad de interpretar EXIF al procesar el JPEG. Comprueba una imagen asimétrica: una escena uniforme no permite apreciar si se ha girado correctamente. La [guía de captura](https://developer.android.com/media/camera/camerax/take-photo) describe las alternativas en memoria y en archivo.

## Grabar vídeo tiene un comienzo y un final asíncronos

`Recorder` configura la grabación. `prepareRecording` crea un `PendingRecording` con destino; todavía no está grabando. `start` devuelve un `Recording` y comienza a comunicar eventos. La app mantiene ese objeto para detener la operación y espera `VideoRecordEvent.Finalize` antes de anunciar un archivo final.

La [guía de vídeo](https://developer.android.com/media/camera/camerax/video-capture) distingue esos objetos. El ejemplo elige calidad SD con una estrategia de alternativa cuando el dispositivo no puede ofrecerla. La calidad no garantiza un tamaño de archivo fijo ni los mismos píxeles en todas las cámaras.

El laboratorio graba **vídeo sin micrófono**: no utiliza `withAudioEnabled()` ni solicita RECORD_AUDIO. Esto permite estudiar el contenedor y la imagen sin recoger voces. Para incorporar audio habría que justificar y solicitar ese permiso al activar la función, comprobar su concesión y configurar la grabación con `withAudioEnabled`; no basta con declarar un permiso que nunca se usa.

`stop()` solicita terminar. El callback Finalize informa después de éxito o error; en caso de error se elimina únicamente la salida incompleta creada por esta operación. Al abandonar la Activity se solicita también la detención. La cámara sigue su ciclo de vida y al destruirse se retiran sus casos; omitir esos límites no es una forma válida de conseguir grabación de fondo.

## Evidencia virtual y evidencia física

La prueba del proyecto decodifica el JPEG y comprueba duración, dimensiones y ausencia de audio del MP4 capturado. En un emulador demuestra el recorrido con su cámara virtual. No acredita enfoque, exposición, colores o estabilización de una cámara física.

En un teléfono repite permiso denegado, cámara ocupada, foto vertical/horizontal, vídeo breve, detención y salida durante la grabación. Conserva el original y comprueba el resultado en otra aplicación antes de afirmar que el archivo compartido es legible.
