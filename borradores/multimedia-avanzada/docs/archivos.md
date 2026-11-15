# Archivos, URI y permisos de lectura

## Dónde vive cada archivo

Ambos laboratorios crean resultados bajo `filesDir/medios`. Esa carpeta pertenece a la aplicación. No se anuncia como una carpeta pública de galería ni se pide acceso completo al almacenamiento para escribir allí. Borrar los datos o desinstalar elimina ese contenido.

La app que recibe una imagen no puede abrir automáticamente la ruta privada de quien la creó. Una ruta como `/data/user/0/...` no es un contrato de intercambio entre aplicaciones. Compartimos una URI `content://` y una autorización temporal.

## Un proveedor con una superficie limitada

El manifiesto registra FileProvider con autoridad `${applicationId}.archivos`, `exported=false` y concesiones de URI. `res/xml/file_paths.xml` expone únicamente la subcarpeta necesaria:

```xml title="file_paths.xml del laboratorio"
<paths>
    <files-path name="medios" path="medios/" />
</paths>
```

`name` participa en la URI pública; `path` decide qué región privada puede representar el proveedor. No utilizamos `path="."` para abrir toda la carpeta de archivos por comodidad. La [guía oficial de FileProvider](https://developer.android.com/reference/androidx/core/content/FileProvider) explica esta relación entre declaración y rutas.

`getUriForFile(contexto, autoridad, archivo)` exige que el archivo esté dentro de una ruta configurada. Si las autoridades no coinciden, falla aunque el JPEG exista. Primero comprueba ese contrato; añadir permisos de galería no repara un proveedor mal registrado.

## Construir una compartición coherente

El Intent utiliza `ACTION_SEND`, MIME y `EXTRA_STREAM`. `ClipData` transporta la misma URI y el flag concede lectura. Este fragmento muestra el patrón común, dentro de un método que ya tiene `archivo` y `mime`:

```kotlin title="Fragmento del intercambio"
val uri = FileProvider.getUriForFile(this, "$packageName.archivos", archivo)
val envio = Intent(Intent.ACTION_SEND).apply {
    type = mime
    putExtra(Intent.EXTRA_STREAM, uri)
    clipData = ClipData.newRawUri("resultado", uri)
    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
}
startActivity(Intent.createChooser(envio, getString(R.string.compartir)))
```

El receptor obtiene acceso a esa URI; no se le concede exploración de todos los resultados. No se utiliza `Uri.fromFile` como sustituto de FileProvider. El código completo captura ausencia de aplicación receptora y mantiene el resultado al cancelar Sharesheet.

En captura, el MIME se elige según JPEG o MP4. En edición se conoce el formato por la operación realizada: PNG o MP4. Extensión, MIME y bytes deben coincidir. La [guía de intercambio seguro](https://developer.android.com/training/secure-file-sharing/share-file) documenta el alcance temporal de los permisos.

## URI de entrada y URI de salida no son intercambiables

Photo Picker concede acceso al elemento elegido. El editor lee esa entrada y crea otra salida. Compartir por error la URI original enviaría una imagen sin el recorte, aunque la pantalla mostrase el resultado correcto.

El laboratorio de cámara conserva la referencia al último archivo de la sesión. El editor conserva la salida del recorte o del exportador. No promete restaurar todo el trabajo de edición después de terminar el proceso; los archivos pueden seguir existiendo aunque una nueva sesión no reconstruya sus referencias de UI. Un editor de producción necesitaría un catálogo o un guardado de proyecto explícito.

No mantengas un permiso de lectura sobre cualquier URI externa para siempre por suponer que el usuario volverá a ella. Si un flujo necesita acceso persistente, comprueba el contrato que lo ofrece y la política adecuada; no todos los proveedores ni todos los selectores tienen el mismo comportamiento.

## Errores y archivos parciales

Crear el nombre de salida no significa terminar la captura. La fotografía espera su callback; el vídeo espera Finalize; Transformer espera onCompleted. Solo esos resultados habilitan la compartición correspondiente.

Transformer puede dejar una salida parcial al fallar o cancelar. El laboratorio la elimina y conserva las salidas anteriores válidas. El borrado se limita al archivo temporal creado para esa operación; no se limpian directorios completos ni originales seleccionados.

Para comprobar permisos reales de intercambio utiliza otra aplicación y abre el archivo recibido. Que `getUriForFile` devuelva una URI demuestra configuración del proveedor, pero no prueba por sí solo que un visor externo haya podido decodificarla.
