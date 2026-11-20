# Práctica guiada: notas privadas en Firebase local

Crearás una cuenta local, observarás sus notas en tiempo real y completarás eliminación y descarga privada. Cuenta, alta, listener, reglas y subida de imagen están preparados. Alternaremos lectura de esos ejemplos con dos operaciones equivalentes. Lee [configuración](configuracion.md), [autenticación](autenticacion.md), [documentos](documentos.md) y [archivos](archivos.md).

[Proyecto inicial — ZIP](../descargas/proyectos/firebase-inicial.zip){ .md-button .md-button--primary }

???+ example "Preparación: servicios y primera cuenta"

    Descomprime, inicia los emuladores siguiendo el README y abre la carpeta con `settings.gradle.kts` en Android Studio. Ejecuta **app** en un emulador Android. No necesitas crear servicios de una cuenta real.

    **Observa el código existente.** Dentro de `app/src/main/java/es/mjsplayground/pmdm/firebase`, abre `LaboratorioApplication.kt`, `NubeRepository.kt` y `MainActivity.kt`. Application conserva el repositorio una vez por proceso; la Activity presenta estado y eventos; el repositorio utiliza los tres servicios. Localiza `useEmulator` antes de cualquier consulta.

    `SesionUsuario.kt` identifica a qué sesión pertenece una operación. Cada callback comprueba su turno antes de publicar resultados; cambiar de cuenta invalida los anteriores. Abrir Photo Picker no cambia la identidad. El borrador y la contraseña se reinician al cambiar de usuario. Esta infraestructura está preparada: no forma parte de los dos TODO.

    **Comprueba.** Introduce un correo de prueba terminado en `example.test` y una contraseña exclusiva de este laboratorio. Crea la cuenta: debe aparecer Cerrar sesión. En la interfaz local del emulador Auth debe existir esa cuenta. Cierra sesión e inicia de nuevo. Una clave equivocada debe mostrar un fallo recuperable, sin presentar la lista privada.

??? example "Notas: escritura y escucha resueltas"

    **Observa el código existente.** Sigue Guardar → `agregar` → `add` → callback, y después `observar` → `addSnapshotListener` → lista. La confirmación del envío y la recepción de una instantánea son operaciones diferentes. Al abandonar, la Activity retira el registro; al volver a primer plano, lo adquiere de nuevo.

    **Ahora te toca.** Cambia solo el texto del recurso `guardada`, conservando su nombre. Crea dos notas con títulos distintos y predice su orden. Identifica qué función impide enviar un título de dos caracteres y qué regla lo rechazaría si se omitiera esa validación local.

    **Comprueba.** Las dos notas deben aparecer ordenadas por título y los campos vaciarse después del éxito. En la UI local de Firestore modifica el título de una: la app debe actualizar la lista sin volver a pulsar Cargar. No publiques capturas con correos o información personal.

??? example "Ahora te toca: eliminar una nota"

    **Ejemplo resuelto.** `agregar` termina su tarea con `addOnCompleteListener` y comunica `isSuccessful` al callback de la pantalla. Esa misma estructura sirve para otra escritura que puede terminar bien o mal.

    **Ahora te toca — `nube-eliminar`.** Completa `eliminar(id, termina)`. La ruta debe pertenecer a las notas del usuario actual y afectar solo al documento elegido. Comunica el resultado real de la tarea, sin borrar manualmente otra lista en la Activity. La escucha existente se encargará de mostrar la nueva colección.

    ??? tip "Pista: colección y documento no son lo mismo"

        `notas()` devuelve la colección privada. A partir de ella, `document(id)` elige una referencia y `delete()` solicita el borrado. Reutiliza el patrón de finalización de `agregar`; no cierres la sesión para simular que la nota ha desaparecido.

    **Comprueba.** Crea dos notas, elimina una y verifica que la otra permanece tanto en la pantalla como en Firestore local. Reinicia la app manteniendo servidores activos: el registro eliminado no reaparece. Con servidores detenidos no debes confundir una escritura local pendiente con confirmación remota.

??? example "Subida resuelta y descarga equivalente"

    **Observa el código existente.** Seleccionar y subir abre Photo Picker. En la imagen API 37 del aula, elige la miniatura y confirma con **Done** para volver a la app. La lectura está acotada a 1 MiB más un byte para detectar exceso. `subir` verifica longitud y MIME, prepara metadatos y escribe en el objeto privado del UID. El resultado del selector no es la confirmación de subida.

    **Ahora te toca — `nube-descargar`.** Completa `descargar(termina)`: recupera la misma referencia de objeto del usuario, descarga como máximo `LIMITE_IMAGEN` bytes y entrega el array si termina bien o `null` si falla. La Activity ya decodifica una vista previa con dimensiones y muestreo controlados; no reemplaces esa parte por una lectura ilimitada.

    ??? tip "Pista: cambiar la operación sobre la referencia"

        Compara el UID y la ruta utilizados en `subir`. La referencia ofrece `getBytes` con máximo de tipo Long. Puedes separar `addOnSuccessListener` y `addOnFailureListener`, manteniendo un único resultado por tarea.

    **Comprueba.** Sube una imagen PNG/JPEG propia, pequeña y no personal. Descárgala: debe verse la vista previa y el mensaje de descarga. Prueba una cuenta nueva sin imagen: el fallo debe ser recuperable. Un archivo de más de 1 MiB debe rechazarse sin presentarse como subido.

??? example "Acceso entre cuentas y cierre"

    **Ahora te toca.** Prepara dos cuentas locales. Guarda una nota y una imagen con la primera; cierra sesión y accede con la segunda. Registra qué se muestra antes y después del cambio, sin copiar identificadores personales al repositorio.

    **Comprueba.** La segunda cuenta no debe ver la lista ni la imagen de la primera. Ejecuta las pruebas de reglas del README: deben probar además el acceso directo de otra identidad, que la pantalla por sí sola no puede demostrar. Distingue la prueba contra Emulator Suite de una validación de un proyecto Firebase de producción.

## Solución de referencia

[Solución completa — ZIP](../descargas/proyectos/firebase-solucion.zip){ .md-button }

Resuelve los dos TODO con los mismos contratos e incluye pruebas JVM, de reglas y de integración Android. La carpeta del proyecto inicial no contiene los métodos Kotlin resueltos.
