# Transformar, convertir y guardar imágenes

## Una cadena de procesamiento comprensible

Seleccionar, decodificar, transformar y convertir son operaciones distintas. Una URI no es un Bitmap; girar sus píxeles no guarda un archivo; cambiar la extensión no convierte el formato. Para poder comprobar el resultado necesitamos saber qué entrada utiliza cada paso y qué salida produce.

```text
Foto seleccionada o capturada
            ↓ lectura acotada y orientación
       Bitmap de trabajo
            ↓ giro o cambio de tamaño
       Bitmap transformado
            ↓ codificación PNG o JPEG
 Archivo nuevo + URI + datos del resultado
            ↓
     Abrir o compartir para verificar
```

`ProcesadorImagen` organiza esa cadena. `cargar(uri)` obtiene un bitmap; `rotar90(bitmap)` devuelve la transformación; `exportar(bitmap, formato)` escribe un resultado propio. El ViewModel coordina esas operaciones y comunica carga, imagen disponible o error a la interfaz. El código completo de la práctica permite seguir estos métodos sin convertir cada botón en una implementación de lectura diferente.

## Decodificar con un límite de memoria

En API 28 o posterior utilizaremos `ImageDecoder`. Su callback de cabecera permite consultar el tamaño antes de obtener el Bitmap y seleccionar dimensiones objetivo. También se solicita un bitmap de software para que las operaciones de procesamiento previstas puedan leer o dibujar sus píxeles sin depender de una asignación de hardware incompatible.

Este fragmento pertenece al método de lectura, que se ejecuta fuera del hilo principal. `ajustarDimensiones` es una función propia del procesador que mantiene la proporción y devuelve una pareja ancho/alto con lado máximo de 1024:

```kotlin title="Esquema de lectura con ImageDecoder, API 28 o posterior"
val source = ImageDecoder.createSource(context.contentResolver, uri)
val bitmap = ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
    val (ancho, alto) = ajustarDimensiones(info.size.width, info.size.height)
    decoder.setTargetSize(ancho, alto)
    decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
}
```

`ImageDecoder` pertenece a `android.graphics`. La llamada termina con un bitmap o un fallo: no debe ejecutarse una vez por recomposición. Su [referencia oficial](https://developer.android.com/reference/android/graphics/ImageDecoder) documenta formatos, tamaño y asignación de memoria.

En API 26 y 27, `decodeCompat26(uri)` usa `BitmapFactory`. Primero abre el contenido con `inJustDecodeBounds = true` para conocer `outWidth` y `outHeight`, sin materializar todos los píxeles. Después vuelve a abrir un stream y decodifica con un `inSampleSize` apropiado. El muestreo elegido deja ambos lados dentro del límite, aunque puede reducir algo más que la rama moderna. No reutilices un stream ya consumido esperando que vuelva al comienzo por sí solo.

La lectura de límites debe comprobar dimensiones válidas: un archivo incompatible o vacío puede no producirlas. El muestreo reduce el tamaño de la decodificación; un escalado opcional permitiría fijar un tamaño exacto; el laboratorio conserva el resultado muestreado. La [guía de bitmaps eficientes](https://developer.android.com/topic/performance/graphics/load-bitmap) explica esta diferencia.

## Mantener la proporción

Para limitar el lado mayor no necesitamos elegir ancho y alto de forma independiente. Si una imagen mide 4000 × 3000 y el máximo es 1024, la proporción es 4:3 y el resultado será aproximadamente 1024 × 768. Forzar 1024 × 1024 deformaría los objetos.

La función `ajustarDimensiones(ancho, alto, max = 1024)` calcula un factor común y evita producir una dimensión cero al redondear imágenes muy estrechas. Una imagen ya pequeña no necesita ampliarse para cumplir un límite máximo. Reducir antes de dibujarla no significa que el archivo original haya cambiado: seguimos trabajando con una copia decodificada.

El tamaño en píxeles del resultado y el tamaño ocupado en bytes son dos medidas distintas. Dos imágenes de 1024 × 768 pueden producir archivos muy diferentes según contenido y formato; un fondo plano se comprime de otra manera que una fotografía con mucho detalle.

## Orientación EXIF: no siempre hay que girar los mismos píxeles

Algunas cámaras almacenan píxeles en una orientación y añaden un dato EXIF que indica cómo presentarlos. Un visor puede mostrar la foto derecha aunque sus píxeles originales estén colocados de otra forma. Si ignoramos esa metadata, una captura aparece tumbada o reflejada después de procesarla.

`ExifInterface` permite leer `TAG_ORIENTATION`. El valor no se reduce a «vertical» u «horizontal»: contempla también reflejos. Esta tabla identifica los casos que debe cubrir la lectura compatible:

| Valor EXIF | Presentación indicada |
| --- | --- |
| Normal | Sin cambio de orientación |
| Flip horizontal | Reflejo horizontal |
| Rotate 180 | Giro de 180 grados |
| Flip vertical | Reflejo vertical |
| Transpose | Intercambio de ejes con reflexión |
| Rotate 90 | Giro de 90 grados |
| Transverse | Intercambio de ejes con la reflexión opuesta |
| Rotate 270 | Giro de 270 grados |

La rama `BitmapFactory` del laboratorio aplica la transformación indicada por EXIF después de decodificar. La rama `ImageDecoder` ya interpreta la orientación que soporta ese decodificador; no aplicamos otra vez el mismo giro. Una rotación doble puede dejar mal una imagen que se había leído correctamente.

La [referencia ExifInterface](https://developer.android.com/reference/androidx/exifinterface/media/ExifInterface) enumera las constantes. Para comprobar reflejos utiliza una imagen con letras o un dibujo asimétrico: un círculo centrado no permite detectar que se ha invertido.

## Rotación de trabajo

Una vez orientada la entrada, el botón de giro aplica una transformación adicional elegida por la persona. `Matrix` representa esa transformación geométrica. Una rotación de 90 grados intercambia las dimensiones de una imagen no cuadrada.

Este fragmento ilustra el núcleo de `rotar90`, sin la coordinación de estado que realiza el ViewModel:

```kotlin title="Núcleo de una rotación con Bitmap y Matrix"
val matriz = Matrix().apply { postRotate(90f) }
val girada = Bitmap.createBitmap(
    original, 0, 0, original.width, original.height, matriz, true
)
```

`Bitmap` y `Matrix` son clases de `android.graphics`. Los valores `0, 0` y las dimensiones seleccionan toda la imagen original; la matriz transforma ese rectángulo; el último argumento solicita filtrado cuando la transformación lo necesita. El resultado se entrega a la pantalla como nuevo estado.

No llames a `recycle()` sobre un bitmap que la interfaz todavía utiliza. Tampoco acumules sin límite todas las versiones anteriores: cada una puede ocupar memoria considerable. La arquitectura del laboratorio conserva el bitmap de trabajo actual y bloquea acciones incompatibles mientras una operación está en curso.

## Convertir a PNG o JPEG

La conversión codifica los píxeles en otro formato. PNG utiliza compresión sin pérdida y puede conservar transparencia. JPEG utiliza compresión con pérdida, resulta habitual en fotografías y no representa un canal alfa de transparencia.

`Bitmap.compress(formato, calidad, stream)` escribe los bytes codificados y devuelve un booleano. Hay que comprobarlo y cerrar el stream. La calidad afecta a JPEG; para PNG no es un control de pérdida de detalle equivalente. No prometas que cualquier conversión reducirá el tamaño del archivo.

Al convertir una imagen transparente a JPEG, el laboratorio compone primero los píxeles sobre un fondo opaco. Debemos decidir ese color de forma explícita; no confiar en que las zonas transparentes aparezcan mágicamente blancas. La [referencia de formatos de compresión Bitmap](https://developer.android.com/reference/android/graphics/Bitmap.CompressFormat) permite contrastar esas capacidades.

La extensión, el formato real y el tipo MIME deben ser coherentes:

| Codificación | Nombre de archivo | MIME |
| --- | --- | --- |
| PNG | `resultado.png` | `image/png` |
| JPEG | `resultado.jpg` | `image/jpeg` |

Renombrar `foto.png` a `foto.jpg` sin recodificar solo cambia su nombre. Una app receptora puede rechazarlo o interpretarlo de manera diferente. La solución exporta un archivo nuevo con su formato correspondiente y conserva la entrada seleccionada sin sobrescribirla.

## Guardar y comprobar el resultado

`exportar` devuelve `ResultadoExportacion`, con URI, nombre, MIME, ancho, alto y bytes. Ese resultado permite comprobar algo más que «se ve una imagen»: después de una rotación esperamos dimensiones intercambiadas; después de limitar el tamaño, el lado mayor no debería superar el máximo; al elegir PNG o JPEG, el MIME y la extensión deben coincidir.

Los archivos se guardan en `filesDir/imagenes`, una carpeta privada de la app. No aparecer en la galería no significa que no se hayan escrito. Son accesibles para nuestra aplicación y se pueden entregar a otra mediante FileProvider. Desinstalar la app o borrar sus datos elimina ese almacenamiento privado; exportar a una colección compartida del dispositivo sería otra decisión.

Para verificar el archivo, vuelve a abrir su URI o compártelo con una app compatible. Comprueba que el receptor lee el resultado nuevo y no la fotografía original. Registra dimensiones, formato y tamaño observados, sin inventar una reducción porcentual de calidad a partir del valor del parámetro JPEG.

## Compartir una URI con acceso temporal

La app receptora necesita una URI de contenido y permiso temporal de lectura. El Intent lleva el MIME correcto y el archivo en `EXTRA_STREAM`; `FLAG_GRANT_READ_URI_PERMISSION` concede la lectura al destinatario del intercambio. `ClipData` transporta también la URI de forma que el sistema pueda aplicar la concesión.

```kotlin title="Fragmento de compartición del resultado exportado"
val envio = Intent(Intent.ACTION_SEND).apply {
    type = resultado.mime
    putExtra(Intent.EXTRA_STREAM, resultado.uri)
    clipData = ClipData.newRawUri("imagen", resultado.uri)
    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
}
context.startActivity(Intent.createChooser(envio, null))
```

`Intent` y `ClipData` se importan de `android.content`. El código completo trata la ausencia de una app receptora y utiliza recursos para los mensajes. La concesión de lectura no permite explorar todas las notas ni modificar la fotografía original. Consulta la [guía de envío de archivos](https://developer.android.com/training/secure-file-sharing/share-file) para seguir la relación entre proveedor, URI y permisos.

## Distinguir una transformación de su demostración

Una Preview que muestra una foto girada no prueba el archivo exportado. Un test que comprueba ancho y alto no prueba por sí solo que una reflexión EXIF se haya aplicado bien. Una compilación correcta tampoco verifica la cámara externa de un teléfono.

Combina una imagen preparada con marcas asimétricas, dimensiones conocidas, inspección visual y apertura del resultado. En el emulador puedes utilizar archivos controlados para repetir la transformación. La captura física, los formatos producidos por otras cámaras y las apps receptoras reales requieren comprobaciones adicionales que deben registrarse por separado.
