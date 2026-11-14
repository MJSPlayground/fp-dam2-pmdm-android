# Práctica: editor mínimo de imágenes

## Abrir el proyecto

Lee primero [permisos y URI](permisos.md), [selección y captura](imagenes.md) y [transformación de imágenes](transformaciones.md). Utiliza el mismo entorno Android del tema anterior.

[Proyecto inicial — ZIP](../descargas/proyectos/multimedia-imagenes-inicial.zip){ .md-button .md-button--primary }

Abre la carpeta con `settings.gradle.kts` y ejecuta **app**. Este laboratorio es independiente de Cuaderno: no utiliza su base de datos ni necesita una cuenta. La interfaz de partida permite identificar las acciones; los TODO del procesamiento delimitan las funciones que debes completar.

Necesitas una imagen que puedas utilizar y una aplicación de cámara disponible si quieres probar la captura. En un emulador sin fotos, incorpora un archivo propio al dispositivo o utiliza su cámara virtual. No solicites acceso a toda la galería para resolver que el selector está vacío.

## Seguir el recorrido de una imagen

En este proyecto, la Activity coordina los selectores del sistema; `ImagenViewModel` representa carga, imagen y resultado; `ProcesadorImagen` abre el contenido y transforma los píxeles. Una URI identifica el contenido al que puedes acceder; no debes convertir su texto en una supuesta ruta del disco.

Antes de modificar código, sigue este recorrido:

```text
Seleccionar o capturar → URI de entrada → carga acotada
                                      → Bitmap de trabajo
                                      → transformación → archivo nuevo → URI de salida
```

La imagen que se dibuja, el archivo codificado y la URI que se comparte son piezas diferentes. El objetivo no es únicamente mostrar una miniatura: debes poder comprobar dimensiones, formato y archivo resultante.

## Seleccionar y cargar sin bloquear

Localiza `imagen-carga`. La entrada será una URI del selector o de la cámara. Completa la apertura mediante `ContentResolver` y el procesador explicado en la teoría; conserva las APIs de Android correspondientes al nivel del dispositivo.

En API 28 o posterior, `ImageDecoder` conoce las dimensiones antes de decodificar. Fija un tamaño de salida acotado y utiliza un bitmap de software para procesarlo. En la rama compatible con API 26–27, una primera lectura obtiene dimensiones; otra lectura decodifica con muestreo. No reutilices un stream que ya has consumido o cerrado.

La orientación EXIF también forma parte de la lectura: una fotografía puede guardar los píxeles en una orientación y declarar otra para su presentación. La rama compatible debe contemplar rotación y reflejo. No vuelvas a aplicar esa orientación si el decodificador ya la ha corregido.

El ViewModel ejecuta el trabajo fuera del hilo de interfaz y publica un resultado nuevo. La pantalla debe mostrar carga durante la espera y un error recuperable si el contenido ya no existe, no se puede abrir o no es una imagen compatible. Cancelar el selector no es un error de lectura: conserva la imagen anterior.

Comprueba una imagen pequeña y otra de resolución grande. La imagen grande debe cargarse con dimensiones de trabajo acotadas, sin congelar la pantalla. Anota dimensiones y tamaño del archivo original cuando estén disponibles: ambos datos miden cosas distintas.

## Capturar una fotografía

Antes de lanzar `TakePicture`, prepara el archivo de destino y obtén su URI mediante FileProvider. Conserva la URI pendiente antes de abrir la aplicación externa: el resultado del contrato es un booleano, no los bytes de la fotografía.

Si el resultado confirma la captura, carga **esa URI** con el mismo recorrido de selección. Si se cancela, no presentes el archivo vacío como una imagen válida. La configuración del proveedor debe exponer solo los subdirectorios previstos, con `exported=false` y permisos temporales.

La cámara externa realiza la captura; este laboratorio no dibuja un visor propio ni necesita introducir CameraX. Si no existe una aplicación que resuelva la acción, presenta una explicación y permite seguir con Seleccionar. No añadas el permiso CAMERA por anticipado para esta delegación.

Comprueba captura y cancelación. En el emulador, una escena virtual demuestra el intercambio de URI con su aplicación de cámara; no verifica enfoque, calidad ni hardware de un teléfono real.

## Rotar y ajustar dimensiones

En `imagen-transformacion`, implementa la rotación de 90 grados creando el resultado correspondiente. Si la entrada mide 800 × 600, la imagen girada debe medir 600 × 800. Una rotación de la vista mediante un modificador visual no transforma el archivo que después exportarás.

`ajustarDimensiones` debe conservar la proporción y limitar el lado mayor sin ampliar imágenes pequeñas. Por ejemplo, 2400 × 1200 con límite 1024 produce 1024 × 512; 300 × 600 no necesita ampliación. Comprueba que ninguna dimensión sea cero y rechaza entradas inválidas antes de dividir.

Aplica la transformación sobre la imagen de trabajo, no sobre un archivo original reabierto a resolución completa sin límites. La interfaz debe actualizar la vista previa y las dimensiones que muestra. Repite la rotación y observa cómo cambia la orientación; no sobrescribas silenciosamente el archivo original.

## Convertir y comprobar el archivo

Completa `imagen-exportacion` para escribir un archivo nuevo en el formato elegido. Para PNG, usa extensión y MIME de PNG; para JPEG, los de JPEG. Cambiar `.png` por `.jpg` en un nombre no convierte sus bytes.

Si la imagen contiene transparencia, decide cómo se compone sobre un fondo opaco antes de JPEG y explícalo en el resultado. PNG puede conservar el canal alfa. La calidad de compresión no tiene el mismo significado en ambos formatos: no prometas reducir un PNG variando un parámetro que ese formato ignora.

Comprueba el booleano devuelto por la compresión, cierra el stream y vuelve a abrir el archivo exportado para verificar que se puede decodificar y que sus dimensiones son las esperadas. Muestra un resultado observable: nombre, formato, dimensiones y bytes. Compara el tamaño de dos salidas sin concluir que JPEG sea siempre más pequeño para cualquier contenido.

## Compartir el resultado

En `imagen-compartir`, comparte la URI del **archivo exportado**, con su MIME y un permiso temporal de lectura. Utiliza Sharesheet y FileProvider; no compartas una URI `file://` ni amplíes la visibilidad de toda la carpeta privada.

Abre la salida desde otra aplicación compatible. Debe verse la imagen transformada, no la original. Cancela también el selector: la exportación ya realizada debe seguir siendo un resultado válido. Una aplicación receptora ausente o incompatible necesita un mensaje, no un cierre inesperado.

## Consolidación y registro

- Selecciona, gira, exporta y abre el resultado desde otra aplicación.
- Repite con una entrada de otra proporción y compara dimensiones.
- Cancela selección y cámara; comprueba que no se presenta una operación cancelada como completada.
- Intenta abrir contenido no disponible y comprueba la recuperación mediante nueva selección.
- Registra qué se verificó en emulador y qué falta comprobar en cámara y dispositivo reales.

Para practicar el cálculo, predice el resultado de `ajustarDimensiones` para 1024 × 2048, 640 × 480 y 1 × 3000 antes de ejecutar las pruebas de referencia. Justifica por qué convertir a PNG no recupera el detalle que ya se perdió en un JPEG.

[Solución formativa completa — ZIP](../descargas/proyectos/multimedia-imagenes-solucion.zip){ .md-button }

La solución contiene Activity, ViewModel, procesador, proveedor y recursos. Compara la función de cada fase con tu implementación; su README identifica cómo abrirla y qué pruebas se incluyen.
