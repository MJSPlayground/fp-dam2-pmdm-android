# Preferencias con DataStore

## Configuración y registros no son lo mismo

Cuaderno guardaba notas en Room. Una preferencia como «mostrar solo pendientes» tiene otro significado: cambia la presentación, no representa una lectura. No necesita una tabla relacionada con autores. **Preferences DataStore** almacena pequeñas opciones mediante claves tipadas y permite observarlas como Flow.

Room sigue siendo apropiado para entidades relacionadas, búsquedas y cambios parciales de muchas filas. DataStore no es una base SQL ni un lugar para bitmaps, listas enormes o secretos sin protección adicional. Para un objeto de configuración estructurado existe DataStore tipado con un serializador; aquí elegimos Preferences porque solo necesitamos un booleano.

La [guía oficial](https://developer.android.com/topic/libraries/architecture/datastore) explica ambos modelos. El proyecto fija `datastore-preferences` 1.2.1, versión estable compatible con la configuración del curso. Su [registro de versiones](https://developer.android.com/jetpack/androidx/releases/datastore#1.2.1) distingue esta combinación de otras versiones más recientes.

## Una instancia por archivo

El delegado se declara una sola vez, fuera de la clase, en `data/Preferencias.kt`:

```kotlin title="Fragmento de declaración del almacén"
private val Context.preferencias by preferencesDataStore(name = "lecturas")
```

El nombre identifica el archivo de preferencias. La extensión pertenece al contexto; el delegado conserva la instancia necesaria. Crear varias instancias independientes para el mismo archivo puede producir fallos y contradicciones. Utilizamos contexto de aplicación, que no retiene una Activity destruida.

La clase `Preferencias` recibe ese contexto con Hilt. Esto no cambia el funcionamiento de DataStore: simplemente determina quién construye el objeto. El archivo completo del proyecto incluye `preferencesDataStore`, las claves de `androidx.datastore.preferences.core` y los imports de Flow.

## Leer de manera reactiva

Una clave tipada relaciona un nombre persistente con un tipo Kotlin. `booleanPreferencesKey("solo_pendientes")` permite escribir y leer booleanos. Cambiar su nombre crea otra clave; no renombra automáticamente el dato anterior.

El flujo de lectura transforma preferencias en el valor que necesita la pantalla:

```kotlin title="Dentro de Preferencias — lectura de una opción"
private val pendientes = booleanPreferencesKey("solo_pendientes")
val soloPendientes = almacen.data.catch { error ->
    if (error is IOException) emit(emptyPreferences()) else throw error
}.map { it[pendientes] ?: false }
```

`almacen` es la instancia obtenida del delegado. Si la clave no existe, elegimos `false`: el primer arranque muestra todas las lecturas. La captura trata errores de entrada/salida; no convierte cualquier fallo de programación en una preferencia falsa ni consume la cancelación de corrutinas.

El ViewModel combina este flujo con los registros de Room. La interfaz no llama a una lectura bloqueante cada vez que se recompone. Cambiar la preferencia emite otro valor y vuelve a calcular las filas visibles.

## Editar como una transacción

`edit` recibe una copia mutable de las preferencias y confirma el cambio de forma atómica. Este ejemplo guarda una clave distinta, útil para comprender la operación que completarás en la práctica:

```kotlin title="Fragmento de una escritura de preferencia"
val vistaCompacta = booleanPreferencesKey("vista_compacta")
almacen.edit { preferencias -> preferencias[vistaCompacta] = true }
```

La llamada es suspendida. El evento se recibe en el ViewModel y se lanza en su ámbito; no se introduce un bloqueo con `runBlocking` en la UI. Cuando termina, el flujo de lectura comunica el nuevo valor. No necesitamos escribir también una segunda variable permanente en la pantalla.

Si falla la escritura, la aplicación debe comunicarlo y conservar los datos de trabajo. DataStore no coordina una transacción conjunta con Room: actualizar una preferencia y modificar una lectura son operaciones distintas, con contratos distintos.

## Persistencia y comprobación

Una casilla que cambia visualmente no demuestra que se haya guardado. Cambia el filtro, abandona y finaliza el proceso, vuelve a abrir y comprueba su valor. Cambia además el estado de una lectura: ocultarla con el filtro no debe eliminarla de Room.

La prueba `datastoreConservaPreferenciaEntreLectores` verifica escritura y observación mediante otra instancia de la clase envolvente, que utiliza el mismo delegado. La comprobación manual de reinicio añade la evidencia del recorrido completo de la pantalla. No se guardan coordenadas, fotografías ni cuentas en este laboratorio.
