# Recorte, rotación y edición de imágenes

## Tres operaciones que producen resultados distintos

Recortar selecciona una región; redimensionar cambia las dimensiones; rotar cambia orientación. Un recorte cuadrado no se obtiene aplastando una foto rectangular hasta que ancho y alto coincidan: eso deforma sus objetos. La decisión es qué parte conservar y cómo representarla.

En el tema principal ya utilizamos Bitmap y Matrix. Para un recorte interactivo con arrastre, zoom, orientación y selección visible conviene valorar una biblioteca mantenida: implementar de nuevo toda esa interacción tiene un coste que no ayuda a aprender la operación de datos.

El laboratorio usa **Android Image Cropper 4.7.0**, bajo Apache-2.0, desde el [repositorio del mantenedor](https://github.com/CanHub/Android-Image-Cropper/tree/4.7.0). Se fija `com.vanniktech:android-image-cropper:4.7.0`, aunque el paquete de sus clases siga siendo `com.canhub.cropper`. Grupo Maven y paquete Kotlin no tienen por qué coincidir.

## Elegir la integración mantenida

El propio proyecto señala como obsoletas su Activity preconstruida y la forma de abrirla mediante contrato. Por eso nuestra Activity aloja **CropImageView** y administra selección y URI. No se enseña una integración antigua únicamente porque un tutorial permita copiarla en menos líneas.

Photo Picker entrega una URI concreta sin acceso general a la galería. `setImageUriAsync(uri)` inicia su lectura; el listener de finalización indica si la imagen puede editarse. La app mantiene Guardar deshabilitado hasta que esa preparación termine. Cancelar el selector conserva la entrada anterior.

`CropImageView` se coloca dentro de `AndroidView`, con altura acotada. El resto de botones pertenece a una columna desplazable. La biblioteca administra el contenido y la ventana de recorte; Compose administra la pantalla que la contiene.

## Proporción y ventana visible

La técnica para una proporción fija utiliza dos enteros. Este ejemplo corresponde a un recorte panorámico:

```kotlin title="Fragmento de configuración de CropImageView"
recorte.setAspectRatio(16, 9)
```

La llamada fija la relación entre ancho y alto; no asigna una resolución de 16 × 9 píxeles ni guarda un archivo. El usuario sigue pudiendo desplazar la ventana sobre el contenido. En la práctica adaptarás esa operación a una proporción cuadrada.

`rotateImage(90)` gira la imagen dentro del editor. Su efecto se incorpora al recorte exportado; no es solo un `Modifier` que gira el control visual. La [clase de la versión fijada](https://github.com/CanHub/Android-Image-Cropper/blob/4.7.0/cropper/src/main/kotlin/com/canhub/cropper/CropImageView.kt) permite contrastar estos contratos.

## Guardar sin bloquear la pantalla

La exportación usa `croppedImageAsync`, el nombre real del método en esta versión. Recibe compresor, calidad cuando proceda, límites de tamaño y una URI de salida. `setOnCropImageCompleteListener` recibe el resultado posterior; solo entonces mostramos el archivo como disponible.

El proyecto elige PNG y limita el resultado a 1024 × 1024 sin forzar una ampliación ni deformar. La relación del recorte y el límite de resolución cumplen tareas diferentes. La calidad 100 no hace que PNG sea «más fiel» que otro valor de calidad JPEG: el significado depende del compresor, como vimos en [transformaciones](../multimedia/transformaciones.md).

La salida es un archivo privado nuevo con una URI FileProvider propia. Se comprueban ancho, alto y MIME al decodificar su cabecera. El nombre `.png` por sí solo no demostraría que sus bytes tengan ese formato.

## Compresión y efectos después del recorte

Un editor puede encadenar selección de región, giro, escalado y ajuste de color. Cada paso debe trabajar sobre una entrada y producir una salida definida. Si se necesita JPEG, hay que elegir un fondo para la transparencia y recodificar; no basta con cambiar la extensión del PNG.

Para un efecto de color de Bitmap, un `Paint` con `ColorMatrixColorFilter` puede aplicar una matriz al dibujar sobre un Canvas de destino. Por ejemplo, una saturación cero produce escala de grises. Esa técnica altera los píxeles de la copia; dibujar un filtro únicamente sobre la Preview no garantiza que el archivo guardado lo contenga. En vídeo se utiliza una cadena de efectos de Media3, estudiada en [Transformer](transformer.md).

No acumules sin límite originales decodificados y copias intermedias. Usa tamaños de trabajo razonables, libera únicamente bitmaps que ya no pertenecen a la UI y retira listeners al destruir el propietario. Un archivo original se mantiene separado del resultado; editar no requiere sobrescribir la fotografía seleccionada.

## Qué permite comprobar el patrón incluido

`patron.png` es una imagen sintética propia de 800 × 600 con cuadrantes de colores. No es una captura de cámara. Permite comprobar que un cuadrado conserva su proporción, que el giro cambia las regiones y que exportar produce un PNG real.

La comprobación con fotografías propias añade formatos, tamaños y EXIF diferentes. Una prueba con el patrón no certifica que todas las cámaras o todas las imágenes enormes puedan procesarse. Registra por separado esas entradas cuando pruebes el editor.
