# Captura propia — solucion

Abre **esta carpeta** mediante Open en Android Studio, espera la sincronización y ejecuta **app**. SDK 37, mínimo API 26 y JBR 25 del IDE para Gradle. No abras el directorio que contiene inicial y solución como un solo proyecto Gradle.

## Funcionamiento

Pulsa Activar cámara. La foto está implementada en el inicial; vídeo y compartición se completan siguiendo la guía. El modo vídeo usa Preview+VideoCapture y el modo foto Preview+ImageCapture: no necesita las tres salidas simultáneas.

El vídeo es silencioso y no solicita micrófono. Los archivos privados nuevos quedan en `filesDir/medios`; la autoridad FileProvider termina en `.archivos`. El resultado de fotografía se confirma en onImageSaved y el de vídeo en Finalize. Al salir se solicita detener y al destruir se retiran los casos de cámara.

## Trabajo del alumnado

Los tres TODO del inicial están resueltos en MainActivity; el resto de clases y recursos corresponde a la misma base.

## Pruebas y alcance

CapturaTest pide permiso en el entorno de pruebas, captura JPEG y MP4 y comprueba decodificación, dimensiones, duración y ausencia de pista de audio. Necesita una cámara virtual configurada o hardware compatible; una prueba sin cámara no puede fingirse superada.

Resultado observado en este encargo: solución compila y pasa lint; prueba Android 1/1 en emulador API 36 con cámara virtual. Eso no acredita enfoque, exposición, sonido físico ni cámaras de todos los dispositivos. La apertura de Sharesheet no prueba por sí sola la decodificación por cualquier receptor externo.

Versiones específicas: CameraX 1.5.3, misma versión en camera2/lifecycle/view/video. [Documentación CameraX](https://developer.android.com/media/camera/camerax/video-capture).

## Compilación y pruebas técnicas

La ruta habitual del alumnado es Android Studio y el botón Run. Comprobación técnica opcional desde esta carpeta:

```powershell
.\gradlew.bat :app:assembleDebug :app:lintDebug :app:assembleDebugAndroidTest --console=plain
.\gradlew.bat :app:connectedDebugAndroidTest --console=plain
```

La solución entrega las instrumentadas activas. Ambas etapas comparten applicationId: instalar una sustituye la otra. Conserva tus archivos en otra carpeta antes de reemplazar tu trabajo.

Código y recursos originales bajo Apache-2.0; LICENSE y avisos del wrapper incluidos. Las dependencias mantienen sus licencias. No hay claves, cuentas ni contenidos de alumnado.
