# Documentos y tiempo real

## Modelo de datos

Firestore organiza documentos en colecciones. Un documento tiene un identificador y campos; puede contener subcolecciones. No es una tabla SQL con joins implícitos. El laboratorio usa `usuarios/{uid}/notas/{id}` y cada nota contiene un título. La subcolección permite consultar las notas del usuario sin descargar documentos de otros.

`NotaNube` representa la salida para la pantalla. No presupone que el identificador exista como campo dentro del documento: se obtiene de `DocumentSnapshot.id`. En aplicaciones mayores conviene validar también los tipos y la ausencia de campos recibidos; datos antiguos pueden no tener la forma de la versión actual.

## Guardar y modificar

`collection.add` crea un documento con ID generado. `document(id).set` escribe en una dirección conocida; `update` modifica campos de un documento existente. Elegir una dirección estable permite actualizar una entidad, mientras llamar a `add` cada vez crea copias nuevas.

El fragmento de referencia presupone una colección privada ya construida; no es una clase completa:

```kotlin
coleccion.add(mapOf("titulo" to titulo.trim()))
    .addOnCompleteListener { tarea ->
        // Solo tarea.isSuccessful permite confirmar el guardado.
    }
```

La [guía de escritura de Firestore](https://firebase.google.com/docs/firestore/manage-data/add-data) diferencia esas operaciones, lotes y transacciones. Una transacción sirve cuando una escritura depende de una lectura consistente. No es necesario introducirla para una nota independiente, pero dos operaciones coordinadas no adquieren atomicidad solo por ejecutarse una después de otra.

Eliminar un documento utiliza su referencia y `delete()`. Borrar un documento padre no elimina automáticamente todos los documentos de sus subcolecciones ni los objetos de Storage: la política de eliminación debe recorrer los recursos que realmente posea la cuenta.

## Recibir cambios sin volver a pulsar Cargar

`addSnapshotListener` observa una consulta y devuelve un `ListenerRegistration`. Su callback recibe una instantánea o un error. En el proyecto, `orderBy("titulo")` establece un orden y el callback transforma cada documento en `NotaNube`.

No añadas un listener en cada recomposición. El propietario conserva el registro y llama a `remove()` al dejar de necesitarlo. Al cambiar de usuario se retira antes el anterior. La [guía de actualizaciones en tiempo real](https://firebase.google.com/docs/firestore/query-data/listen) explica el primer resultado y los eventos posteriores.

Una instantánea vacía es un éxito sin notas; un error de permisos es un fallo. No los conviertas en el mismo mensaje. Además, Firestore puede emitir datos de caché y escrituras locales pendientes: si una pantalla necesita distinguir confirmación del servidor, debe consultar los metadatos correspondientes. Ver aparecer un dato local no acredita por sí solo que el servidor lo haya autorizado.

## Consultas, índices y coste

Un filtro se define en la consulta, no después de descargar toda la base. Combinar condiciones y orden puede requerir índices compuestos; Firestore comunica qué índice falta. Antes de crearlo hay que revisar si la consulta responde a una necesidad y limitar resultados cuando puedan crecer.

Los listeners y las lecturas tienen coste en un servicio real. No observes toda la colección de todas las cuentas para mostrar cinco notas. Las reglas tampoco son filtros: una consulta debe ser compatible con las condiciones que sus reglas exigen. El laboratorio consulta directamente la ruta del usuario y se prueba que otra identidad no puede leerla.

La caché sin conexión mejora continuidad, pero también influye en privacidad en dispositivos compartidos. Diseña qué se muestra al cerrar sesión y qué datos locales deben retirarse; no interpretes cerrar una pantalla como borrar la persistencia interna del SDK.
