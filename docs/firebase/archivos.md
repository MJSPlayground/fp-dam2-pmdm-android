# Archivos, reglas y privacidad

## Un objeto y su ruta

Storage administra bytes y metadatos. El laboratorio utiliza `usuarios/{uid}/imagen`: volver a subir reemplaza ese objeto del usuario, no crea un documento Firestore. Se utiliza una ruta estable para que el ejercicio de descarga sea inequívoco.

`putBytes` recibe un array y metadatos; `putFile` puede subir una URI autorizada sin cargarla entera mediante código propio. El ejemplo utiliza bytes para aplicar un límite de 1 MiB antes del envío. La lectura se realiza fuera del hilo principal y se detiene tras el máximo más un byte, de modo que detecta exceso sin leer un archivo ilimitado.

```kotlin
val metadata = StorageMetadata.Builder().setContentType(mime).build()
referencia.putBytes(bytes, metadata)
```

`StorageMetadata` pertenece a `com.google.firebase.storage`. La [guía de subida](https://firebase.google.com/docs/storage/android/upload-files) explica progreso, tareas, cancelación y errores. No publiques el mensaje «subido» al elegir un archivo: elegir, leer y completar la subida son pasos distintos.

## Descargar sin un tamaño ilimitado

`getBytes(maximo)` descarga el objeto y falla si supera el límite. También existe descarga a archivo para recursos mayores. En este laboratorio se devuelve un array acotado y después se decodifica una vista previa con límites de dimensiones y muestreo.

Un archivo comprimido de pocos bytes puede representar muchos píxeles: limitar bytes no evita por sí solo agotar memoria al decodificar. La [lectura acotada de imágenes](../multimedia/transformaciones.md) explica por qué se consultan dimensiones antes de crear un bitmap completo. Un formato incompatible debe dar error y permitir seleccionar otro.

Las URL de descarga con token pueden funcionar como enlaces de acceso para quien las conozca. No las pegues en informes públicos como si fueran rutas privadas protegidas por tu pantalla. El laboratorio descarga mediante el cliente autenticado y su referencia; no publica enlaces de archivos del usuario.

## Reglas de propiedad

Las reglas de Firestore comprueban usuario, campos admitidos, tipo y longitud. Las de Storage comprueban propietario, tamaño y MIME declarado. Ambos ficheros están en `emuladores` y el servidor local los carga; no son comentarios decorativos.

La condición común es:

```text
request.auth != null && request.auth.uid == uid
```

`uid` procede de la ruta que se está autorizando. Sin autenticación, o con una identidad diferente, se deniega. La [documentación de reglas de Firestore](https://firebase.google.com/docs/firestore/security/get-started) y de [reglas de Storage](https://firebase.google.com/docs/storage/security) detalla sus objetos y operaciones. No habilites `allow read, write: if true` para resolver un error de permisos.

El MIME de metadatos es una declaración, no un análisis antivirus ni una prueba de que los bytes sean una imagen válida. Las reglas pueden limitar esa declaración y el tamaño; la decodificación del cliente debe seguir tratando contenido inválido. Si un producto necesita inspección central, tendrá que diseñarla en un entorno de servidor apropiado.

## Pruebas de acceso que discriminan

Una prueba positiva con la cuenta propietaria es insuficiente. El laboratorio prueba además lectura anónima, otra identidad, campos extra, título inválido, tamaño excesivo y MIME distinto. También verifica un cambio observado por un listener y creación/inicio de sesión contra el emulador Auth.

Desde `emuladores`, `npm test` inicia servicios temporales y ejecuta esas pruebas. Un rechazo esperado cuenta como comprobación correcta; un fallo inesperado debe investigarse. Las herramientas fijan sus versiones en `package.json` y `package-lock.json`, y no se incluyen `node_modules` en los ZIP.

En producción añade revisión de permisos, pruebas con configuración real controlada, cuotas, App Check cuando corresponda y un procedimiento de borrado/retención. El Admin SDK de un servidor privilegiado no demuestra que las reglas del cliente funcionen: puede omitir esas reglas. Por eso las pruebas usan contextos de cliente autenticados y anónimos.
