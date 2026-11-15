# Caché y funcionamiento sin conexión

## Una fuente observable y dos orígenes de datos

Una aplicación puede consultar una API y mostrar su respuesta directamente, pero al fallar la siguiente consulta no tendrá necesariamente nada que enseñar. Lecturas hace otra elección: la pantalla observa siempre Room. La API solo actualiza esa fuente local.

```text
UI ← StateFlow ← Repository ← Flow de Room
                       ↑
             respuesta válida de API
                       ↑
                 acción Actualizar
```

La [arquitectura offline-first](https://developer.android.com/topic/architecture/data-layer/offline-first) utiliza almacenamiento local para poder leer datos aunque la red no esté disponible. Esto no significa que todos los datos estén siempre actualizados ni que cualquier escritura pueda sincronizarse automáticamente.

En Lecturas el catálogo remoto es de consulta y la marca leída es local. No subimos esa marca al servicio de demostración. No hay cola de envíos, resolución de conflictos entre teléfonos ni sincronización bidireccional. Esas capacidades necesitarían reglas propias.

## El recorrido de actualización

`FuenteRemota` utiliza Retrofit contra el recurso HTTPS `posts?_limit=5` de JSONPlaceholder. El DTO aprovecha `id`, `userId` y `title`; son publicaciones adaptadas a fichas de lectura para el laboratorio, no libros de una API bibliográfica.

El repositorio sigue este orden:

1. Solicita los datos fuera de una transacción Room.
2. Valida identificadores, títulos y ausencia de IDs duplicados.
3. Abre una transacción local.
4. Guarda autores y etiquetas necesarios, después lecturas y vínculos.
5. Deja que Room emita el nuevo catálogo a la interfaz.

Si falla la red, la transacción ni siquiera empieza. El ViewModel comunica el error y conserva las fichas ya observadas. No reemplaza la lista por `emptyList()` para representar falta de conexión: vacío y error son estados distintos.

## Conservar cambios locales

Una respuesta remota no sabe si la persona ya marcó una lectura como leída. El DTO no tiene ese campo. Al preparar cada fila, el repositorio conserva la marca local del mismo ID y utiliza falso solo para una lectura nueva.

```kotlin title="Fragmento de mapeo dentro de la transacción"
Lectura(dto.id, dto.title.trim(), dto.userId,
    dao.buscar(dto.id)?.leida ?: false)
```

`@Upsert` no deduce por sí solo qué campos son remotos o locales: escribir un objeto nuevo con `leida = false` sobrescribiría la decisión anterior. La regla debe estar en nuestro código y tener una prueba que actualice el título sin perder esa marca.

Tampoco interpretamos que una respuesta parcial de cinco elementos representa todo el catálogo del servidor. Las filas ausentes se conservan. Borrarlas supondría confundir un recurso limitado con una instantánea completa y perder lecturas locales. Los registros de demostración utilizan IDs apartados del recurso limitado.

## Caché, frescura y eliminación

Una caché necesita una política explícita. Aquí se actualiza al pulsar un botón y no caduca automáticamente. Tras un error se siguen mostrando los datos guardados, acompañados del mensaje. No afirmamos que sean los más recientes.

En una aplicación real se puede guardar un instante de actualización, elegir una antigüedad máxima o invalidar recursos determinados. Cada política responde al coste y a la necesidad del producto. Actualizar sin parar porque Compose se recompone no es una estrategia de frescura.

Las escrituras de este laboratorio son serializadas por la acción del ViewModel y las transacciones de Room. En sistemas con ediciones concurrentes, múltiples procesos o varios dispositivos habría que definir versiones, conflictos y qué cambios tienen prioridad. Añadir un `Repository` no resuelve esos problemas por su nombre.

## Probar sin depender siempre de Internet

La demostración local permite poblar dos lecturas y sus relaciones sin red. Es un recurso de aprendizaje explícito, no una respuesta fingida de la API. Al pulsar Actualizar se consulta el servicio real y puede fallar si no hay conectividad.

Las pruebas sustituyen únicamente `LecturasApi` por una implementación de prueba. Una devuelve datos controlados y otra lanza `IOException`. Así se comprueba de manera repetible que el fallo conserva la caché y que una actualización respeta `leida`. Esto no acredita el acceso HTTPS real: esa comprobación se registra por separado al ejecutar la app.
