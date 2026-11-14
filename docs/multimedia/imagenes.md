# Seleccionar y capturar imágenes

## Archivo, píxeles y URI

Una imagen comprimida es un archivo con un formato, como JPEG o PNG. Para dibujarla o modificarla solemos decodificarla a píxeles en memoria. Una **URI** identifica dónde pedir el contenido, pero no contiene por sí sola la imagen ni indica necesariamente una ruta que podamos abrir con `File`.

Una URI `content://...` se consulta mediante `ContentResolver`, que comunica con el proveedor. Puede proceder del selector de medios, de un proveedor de documentos o de nuestro FileProvider. No extraigas el campo `path` y lo trates como una ruta del sistema de archivos: una URI de contenido tiene otro contrato.

```text
URI seleccionada → ContentResolver → bytes comprimidos
                                      ↓ decodificación
                                  Bitmap en memoria
```

Un stream permite leer los bytes. Debe cerrarse cuando terminamos; en Kotlin, `use { ... }` realiza ese cierre incluso si el bloque falla. El proveedor puede no entregar el contenido, devolver un formato que no podamos decodificar o retirar el acceso, así que hay que tratar los errores.

## Elegir con Photo Picker

Photo Picker presenta una interfaz del sistema para que la persona elija imágenes y vídeos. La app recibe únicamente las selecciones autorizadas. No necesitamos pedir acceso general a toda la biblioteca para ofrecer este botón.

`ActivityResultContracts.PickVisualMedia` adapta la selección a las capacidades disponibles del dispositivo. En sistemas donde el selector no esté disponible puede utilizar una alternativa compatible del contrato. No programes una búsqueda manual por rutas de galería para cubrir versiones antiguas.

Este fragmento ilustra el registro en una Activity. `uriSeleccionada` representa el estado que la pantalla utilizará para mostrar o procesar la imagen:

```kotlin title="Fragmento de registro del selector"
private val seleccionar = registerForActivityResult(
    ActivityResultContracts.PickVisualMedia()
) { uri ->
    if (uri != null) {
        uriSeleccionada = uri
        // Decodificar de forma acotada fuera del hilo principal.
    }
}
```

La acción del botón lanza una solicitud de imágenes. Sus imports proceden de `androidx.activity.result` y `androidx.activity.result.contract`:

```kotlin title="Fragmento de la acción Seleccionar"
seleccionar.launch(
    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
)
```

El callback puede recibir `null` si se cancela. Conservar la imagen anterior o mostrar que no hay selección son decisiones razonables; inventar un archivo vacío para sustituir la cancelación no lo es. La [documentación de Photo Picker](https://developer.android.com/training/data-storage/shared/photo-picker) incluye disponibilidad, compatibilidad y duración de las autorizaciones.

## Preparar una captura con otra aplicación

Para una fotografía sencilla no necesitamos construir un visor propio. `TakePicture` solicita a una aplicación de cámara que capture una imagen completa y la guarde en una URI de salida que preparamos. Esto se diferencia de los ejemplos antiguos que recuperan una pequeña miniatura de los extras del Intent.

Antes de lanzar la cámara, crea un archivo de salida en un directorio privado que esté permitido por nuestro FileProvider. Conserva su URI como **captura pendiente**. La cámara puede tardar y la Activity puede recrearse mientras está fuera; si esa URI solo existía como variable local del clic, no sabremos qué abrir cuando vuelva el resultado.

El contrato recibe la URI y responde con éxito o cancelación/fallo. El booleano no sustituye la URI:

```kotlin title="Fragmento del callback de captura"
private val capturar = registerForActivityResult(
    ActivityResultContracts.TakePicture()
) { correcta ->
    val uri = capturaPendiente
    if (correcta && uri != null) {
        uriSeleccionada = uri
        // Leer y comprobar el archivo capturado.
    } else {
        // La captura no se completó: conservar un estado de pantalla coherente.
    }
}
```

`capturar.launch(uriDeSalida)` se ejecuta después de preparar y conservar el destino. La implementación completa debe gestionar que no exista una app capaz de capturar y no presentar la cancelación como una fotografía válida. La [referencia de TakePicture](https://developer.android.com/reference/androidx/activity/result/contract/ActivityResultContracts.TakePicture) define precisamente sus tipos de entrada y salida.

## Qué aporta FileProvider

Nuestros archivos privados no son directamente legibles por otra aplicación. FileProvider expone URIs `content://` para rutas que declaramos expresamente. El manifiesto identifica el proveedor mediante una autoridad única, normalmente el identificador de la aplicación seguido de `.fileprovider`, y permite conceder acceso temporal.

La configuración tiene tres piezas que deben coincidir: autoridad del manifiesto, autoridad usada en `FileProvider.getUriForFile` y rutas XML autorizadas. Si una no coincide, el fallo se produce antes de que la cámara pueda escribir o la app receptora pueda leer.

En la etiqueta `application` se declara un proveedor no exportado con `grantUriPermissions="true"`. La metadata `android.support.FILE_PROVIDER_PATHS` referencia un XML en `res/xml`. Ese XML limita la zona que se puede compartir; nuestro laboratorio utiliza un subdirectorio de archivos privados:

```xml title="Ejemplo de res/xml/file_paths.xml — ruta limitada"
<?xml version="1.0" encoding="utf-8"?>
<paths xmlns:android="http://schemas.android.com/apk/res/android">
    <files-path name="imagenes" path="imagenes/" />
</paths>
```

El ejemplo solo admite archivos dentro de `filesDir/imagenes/`. No expone todos los archivos del dispositivo. En el laboratorio la autoridad es `${applicationId}.fileprovider`, donde Gradle sustituye el identificador de esa app. Si modificas la carpeta o la autoridad, actualiza la configuración y el código de forma coherente. Estos resultados privados no se añaden automáticamente a la galería; permanecen en el almacenamiento de la app hasta que se borren, se eliminen sus datos o se desinstale.

`getUriForFile` no crea un archivo inexistente ni comprime una imagen: crea una URI para el archivo que has preparado. Permitir concesiones temporales tampoco hace que todo el mundo pueda explorar el proveedor. La [guía oficial de compartición segura](https://developer.android.com/training/secure-file-sharing/setup-sharing) explica esa configuración.

## Decodificar sin agotar memoria

Una foto de 6000 × 4000 píxeles puede requerir unos 96 MB para sus píxeles en ARGB de cuatro bytes, aunque el JPEG ocupe pocos megabytes. Si además conservas una copia rotada y otra escalada, el consumo aumenta. Reducir un bitmap después de haber decodificado la fotografía completa no evita el pico inicial.

Por eso leeremos primero sus dimensiones y elegiremos un tamaño de decodificación acotado. En API 28 o posterior, `ImageDecoder` permite elegir el tamaño durante la decodificación. En las versiones 26 y 27 del mínimo del curso utilizaremos `BitmapFactory.Options` para leer límites y aplicar muestreo antes de materializar los píxeles.

El límite de la práctica define el lado máximo de trabajo, no un formato distinto. Se mantiene la proporción; una foto panorámica no debe convertirse en un cuadrado estirado. Las operaciones de lectura y procesado se ejecutan fuera del hilo principal para que la pantalla pueda mostrar carga y continuar respondiendo.

En el proyecto, `ProcesadorImagen.cargar(uri)` concentra esta lectura y limita el lado de trabajo a 1024 píxeles. `ImagenViewModel` coordina la operación y expone el estado. Las llamadas a Photo Picker y cámara permanecen en `MainActivity`, porque necesitan los contratos de Activity Result; el procesador no abre pantallas del sistema por su cuenta.

La [referencia ImageDecoder](https://developer.android.com/reference/android/graphics/ImageDecoder) y la [guía de carga eficiente de bitmaps](https://developer.android.com/topic/performance/graphics/load-bitmap) explican estas herramientas. En la [página de transformaciones](transformaciones.md) veremos orientación EXIF, rotación y conversión sin confundir esos pasos.

## Mostrar y conservar una imagen

Compose puede dibujar un bitmap mediante `asImageBitmap()` y `Image`. La pantalla debe distinguir imagen no seleccionada, carga, imagen disponible y error. La descripción de accesibilidad debe comunicar qué muestra o qué función cumple, sin repetir información decorativa innecesaria.

Un Bitmap no debe guardarse directamente en `rememberSaveable` ni en un Bundle: es un recurso potencialmente grande. Para restaurar una pantalla utiliza una referencia adecuada y vuelve a cargar de forma controlada. La URI solo es útil mientras conserves acceso a su contenido; un archivo de caché también puede ser eliminado por el sistema.

La fotografía original seleccionada no se modifica por el hecho de decodificarla o dibujarla. Al transformar, guardaremos un resultado propio. Si se ofrece compartirlo, la persona debe reconocer cuál es el archivo convertido y qué permisos temporales recibirá el destinatario.
