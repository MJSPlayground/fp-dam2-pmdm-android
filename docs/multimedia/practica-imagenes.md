# Práctica guiada: editor mínimo de imágenes

Selecciona o captura una foto, transforma sus píxeles y genera un archivo PNG o JPEG que otra aplicación pueda abrir. Utilizaremos Activity Result, Bitmap, un ViewModel y FileProvider. La selección y la exportación PNG son ejemplos preparados; tú completarás el giro, la conversión JPEG y la compartición.

Lee [permisos](permisos.md), [imágenes](imagenes.md) y [transformaciones](transformaciones.md).

[Proyecto inicial — ZIP](../descargas/proyectos/multimedia-imagenes-inicial.zip){ .md-button .md-button--primary }

???+ example "Preparación y primera lectura"

    Abre en Android Studio la carpeta con `settings.gradle.kts`, espera la sincronización y ejecuta **app**. Incorpora una foto propia al emulador si el selector está vacío. Puedes empezar sin cámara.

    **Observa el código existente.** En `app/src/main/java/es/mjsplayground/pmdm/multimedia/imagenes`, sigue `MainActivity.kt` → `ImagenViewModel.kt` → `ProcesadorImagen.kt`. La Activity abre selectores; el ViewModel coordina carga y errores; el procesador lee y escribe archivos. La interfaz está preparada para concentrarnos en multimedia.

    | Archivo | Preparado | Completarás |
    | --- | --- | --- |
    | `MainActivity.kt` | Selector, cámara y vista previa | `imagen-compartir` |
    | `ImagenViewModel.kt` | Trabajo asíncrono y estado | Nada |
    | `ProcesadorImagen.kt` | Lectura acotada, EXIF y PNG | `imagen-transformacion`, `imagen-jpeg` |

    **Comprueba.** Selecciona una imagen y confirma con **Done** si aparece en Photo Picker: debe verse con sus dimensiones. Cancela otra selección: debe permanecer la imagen anterior. Exporta PNG: debe aparecer nombre, MIME y bytes. Giro y JPEG muestran un error recuperable hasta completar sus TODO; Compartir muestra el aviso de pendiente.

??? example "Selección y captura: comprender lo resuelto"

    **Observa el código existente.** Localiza el registro de `PickVisualMedia` y su callback. Solo una URI no nula llega a `vm.cargar`. En `TakePicture`, el resultado es un booleano; la URI de destino se conserva antes de lanzar la cámara.

    `prepararCaptura` crea un archivo privado. Comprueba que coinciden la autoridad del manifiesto y la usada en FileProvider. `res/xml/file_paths.xml` expone únicamente `imagenes/`.

    **Ejemplo resuelto.** En `ImagenViewModel.cargar`, `withContext(Dispatchers.IO)` realiza la lectura fuera del hilo de interfaz. Después el bitmap pasa al estado observado por la pantalla. Una URI no es una ruta que debas abrir con `File(uri.path)`.

    **Ahora te toca.** Predice qué sucederá si cancelas la cámara y si giras el dispositivo antes de aceptar la fotografía. Localiza qué propiedad conserva la URI pendiente y explica por qué no se guarda el Bitmap en un Bundle.

    **Comprueba.** Solo una captura confirmada reemplaza la imagen. Si no existe app de cámara debe aparecer un aviso y seguir disponible Seleccionar. La cámara virtual no acredita el hardware de un teléfono.

??? example "Transformar los píxeles"

    La lectura ya limita el tamaño y corrige EXIF. El giro del usuario es una transformación adicional.

    **Ejemplo resuelto de la técnica.** Esta matriz refleja horizontalmente una imagen:

    ```kotlin
    val matriz = Matrix().apply { postScale(-1f, 1f) }
    val reflejada = Bitmap.createBitmap(
        original, 0, 0, original.width, original.height, matriz, true
    )
    ```

    `Bitmap` y `Matrix` pertenecen a `android.graphics`; están importados en el procesador. El rectángulo abarca todos los píxeles; la matriz determina la transformación. El fragmento supone una variable `original`; no es un archivo independiente.

    **Ahora te toca.** Sustituye la excepción de `rotar90` en `imagen-transformacion`. Devuelve un bitmap girado 90 grados. El ViewModel ya publicará tu devolución. Girar solo el componente `Image` no transforma el archivo.

    ??? tip "Pista: otra operación de Matrix"

        Conserva la selección de píxeles de `createBitmap`. Revisa `postRotate`, que recibe grados. No recicles una imagen que la interfaz todavía utiliza.

    **Comprueba.** Una entrada de 800 × 600 debe pasar a 600 × 800. Usa letras para observar el giro. Cuatro giros recuperan la orientación inicial. Exporta PNG y comprueba las dimensiones resultantes.

??? example "Del ejemplo PNG a JPEG"

    **Observa el código existente.** En `exportar`, sigue archivo nuevo → stream cerrado con `use` → `compress` → verificación del MIME y dimensiones. No basta con cambiar la extensión.

    **Ahora te toca.** Completa `imagen-jpeg`: conserva PNG y añade JPEG. Retira la restricción que rechaza JPEG cuando ya lo implementes. Elige el compresor adecuado y prepara fondo blanco cuando haya alfa. Conserva la verificación y libera únicamente la copia temporal creada para esa conversión.

    `Canvas(destino)` dibuja sobre un bitmap nuevo: `drawColor(Color.WHITE)` pinta el fondo; `drawBitmap(origen, 0f, 0f, null)` superpone la imagen. Los imports están preparados. La [teoría](transformaciones.md#convertir-a-png-o-jpeg) explica transparencia y calidad.

    ??? tip "Pista: identidad de los bitmaps"

        Para PNG puedes comprimir la entrada. Para JPEG transparente necesitas otra imagen. Compara su identidad antes de liberar recursos en `finally`. La verificación de MIME detectará escribir bytes PNG bajo un nombre JPG.

    **Comprueba.** Exporta una imagen transparente a ambos formatos: PNG conserva alfa y JPEG muestra fondo blanco. Reabre los archivos y compara dimensiones y MIME. No presupongas que JPEG siempre ocupará menos bytes.

??? example "Compartir e integrar"

    **Ahora te toca.** En `imagen-compartir`, sustituye el aviso por un Intent de envío del resultado exportado: MIME, URI, Sharesheet y lectura temporal. Usa el patrón de [Intents de Cuaderno](../desarrollo/intents.md) y la [explicación de URI multimedia](transformaciones.md#compartir-una-uri-con-acceso-temporal). Los imports están preparados.

    ??? tip "Pista: qué archivo viaja"

        Utiliza `resultado.uri` y `resultado.mime`. Revisa `EXTRA_STREAM`, `ClipData` y `FLAG_GRANT_READ_URI_PERMISSION`. Captura `ActivityNotFoundException` para mostrar el recurso `sin_destino` si falta un receptor.

    **Comprueba.** Selecciona, gira, convierte y abre desde otra app: debe recibir el archivo girado. Cancela Compartir: la exportación debe permanecer. Repite con una entrada inaccesible y recupera mediante otra selección.

    Los archivos quedan en `filesDir/imagenes`; no se añaden a la galería y desaparecen al borrar datos de la app. Registra API, dimensiones, formatos y acciones observadas; separa emulación de cámara y receptores físicos.

## Solución de referencia

[Solución formativa completa — ZIP](../descargas/proyectos/multimedia-imagenes-solucion.zip){ .md-button }

Contiene las mismas clases y los tres TODO resueltos. Ábrela en otra carpeta para conservar tu trabajo. Los ejercicios autónomos están en [consolidación](consolidacion.md).
