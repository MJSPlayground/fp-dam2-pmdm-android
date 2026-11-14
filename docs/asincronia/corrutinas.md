# Corrutinas: ámbitos, cancelación y errores

## Suspender y elegir dónde ejecutar

El tema principal introdujo `suspend`: una función puede detener su continuación y reanudarla después sin mantener bloqueado el hilo durante esa espera. No significa que cualquier código de su cuerpo deje de bloquear. `Thread.sleep` sigue ocupando el hilo; un bucle costoso sigue consumiendo CPU.

`delay(600)` es una espera suspendible. En el laboratorio simula una consulta lenta para hacer visibles los cambios de estado. No proporciona conexión a un servidor ni representa una prueba de HTTP. Para una API que ya suspende, como Retrofit, no hace falta envolver automáticamente cada llamada en otro dispatcher.

Cuando debas ejecutar trabajo bloqueante de entrada/salida o cálculo intensivo, usa el contexto adecuado con `withContext`, siguiendo la [guía de corrutinas en Android](https://developer.android.com/kotlin/coroutines/coroutines-adv). Este fragmento ilustrativo devuelve un cálculo dentro de una función suspendida:

```kotlin
suspend fun calcularResumen(libros: List<Libro>): Map<String, Int> =
    withContext(Dispatchers.Default) {
        libros.groupingBy { it.categoria }.eachCount()
    }
```

`Default` está orientado a CPU; `IO`, a operaciones bloqueantes de entrada/salida. No cambies al dispatcher IO solo porque una función sea `suspend`. Para cinco libros, cambiar de contexto ni siquiera es necesario: aquí se muestra el mecanismo, no una optimización imprescindible.

## El ámbito tiene un propietario

Una corrutina necesita un `CoroutineScope` que determine contexto y duración. En Android, `viewModelScope` pertenece al ViewModel; al destruir definitivamente ese propietario se cancelan sus tareas. Girar la pantalla normalmente conserva el ViewModel, así que no equivale a abandonarlo.

| Propietario | Uso razonable | Finalización |
| --- | --- | --- |
| `viewModelScope` | Cargar o transformar estado de aplicación | Cuando se limpia el ViewModel |
| `lifecycleScope` | Trabajo ligado a un propietario de ciclo de vida | Al destruir ese propietario |
| `rememberCoroutineScope` | Snackbar o interacción local de Compose | Al abandonar esa parte de la composición |
| `coroutineScope` | Coordinar subtareas dentro de una función suspendida | Cuando terminan todas, o se cancela/falla el conjunto |

No utilices `GlobalScope` para evitar una cancelación que molesta. Una tarea sin propietario puede seguir accediendo a objetos que ya no deben existir. Tampoco crees un scope nuevo en cada recomposición. Para trabajo persistente diferido existe otra ampliación; un scope en memoria no garantiza ejecución tras terminar el proceso.

La [concurrencia estructurada](https://kotlinlang.org/docs/coroutines-basics.html) mantiene la relación padre/hijos. Una función que abre `coroutineScope` no finaliza mientras sus hijos sigan trabajando. Esto facilita saber quién cancela, quién espera y dónde llegan las excepciones.

## `launch` y `async`

`launch` inicia trabajo y devuelve un `Job`. Sirve para esperar su terminación con `join` o pedir cancelación con `cancel`; no devuelve un resultado de negocio. `async` devuelve `Deferred<T>` y `await` obtiene su resultado o propaga su fallo.

Para consultar dos proveedores independientes podemos iniciarlos antes de esperar. Fragmento dentro de una función suspendida que recibe `primero` y `segundo` como funciones:

```kotlin
return coroutineScope {
    val a = async { primero() }
    val b = async { segundo() }
    a.await() + b.await()
}
```

Ambas consultas pueden avanzar durante las esperas de la otra. Si escribieras `async { primero() }.await()` antes de crear `b`, habrías esperado la primera por completo. Concurrencia significa progreso solapado; no garantiza que dos instrucciones se ejecuten físicamente a la vez. Las pruebas del laboratorio usan demoras distintas y reloj virtual para distinguir ambos recorridos.

Cuando una hija falla dentro de este `coroutineScope`, se cancela la operación conjunta. Es apropiado si necesitamos ambos resultados. Si el producto admite resultados parciales, hay que diseñar qué fallos se toleran; `supervisorScope` aísla fallos de hijos, pero **no captura sus excepciones por ti**. Consulta [composición de funciones suspendidas](https://kotlinlang.org/docs/composing-suspending-functions.html) antes de introducir supervisión sin un contrato claro.

## La cancelación es cooperativa

`cancel()` pide detener una tarea. Las funciones suspendibles de corrutinas reconocen esa petición; un bucle de CPU que nunca suspende debe comprobar `ensureActive()` o `isActive`. Una API bloqueante ajena no se convierte en cancelable por estar rodeada de una corrutina.

Una `CancellationException` no significa «el servidor falló». Se utiliza para terminar el trabajo de manera normal cuando su propietario ya no lo necesita. Si capturas `Exception`, debes conservar ese caso:

```kotlin
try {
    consultar()
} catch (cancelada: CancellationException) {
    throw cancelada
} catch (fallo: IOException) {
    mostrarErrorDeConexion()
}
```

Es un fragmento conceptual: `consultar` y `mostrarErrorDeConexion` representan operaciones de tu aplicación. Captura las excepciones esperadas, no todos los fallos para esconder errores de programación. En nuestro catálogo solo se simula `IllegalArgumentException` al escribir `!error`.

Los recursos deben liberarse en `finally` cuando los posees. La cancelación también recorre ese bloque. No inicies allí un trabajo suspendible largo sin entender el contexto cancelado; cerrar un recurso síncrono suele ser lo necesario. La [referencia de cancelación](https://kotlinlang.org/docs/cancellation-and-timeouts.html) desarrolla estas reglas y los timeouts.

## Errores con una consecuencia visible

Decide dónde se convierte una excepción en estado para la UI. Si ocurre antes de completar una búsqueda, la pantalla debe salir de carga y permitir otra consulta. Si capturas un fallo fuera de un flujo de búsquedas y finalizas ese flujo, escribir después puede dejar de tener efecto.

En [Flow](flujos.md) veremos cómo cada consulta controla sus errores sin consumir la cancelación ni terminar la observación de las siguientes. Esa es una decisión funcional: «falló esta búsqueda» y «el buscador ha dejado de observar» tienen resultados muy diferentes.
