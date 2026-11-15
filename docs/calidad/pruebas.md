# Pruebas de lógica, Repository y ViewModel

## Qué comportamiento merece una prueba

Una prueba prepara un caso, ejecuta una acción y compara una observación con el contrato. Debe detectar un error que importe, no repetir el cuerpo de la función con otros nombres. Si un título debe admitir entre 3 y 80 caracteres tras recortar espacios, los límites 2, 3, 80 y 81 discriminan mejor que diez ejemplos del caso normal.

En Android hay pruebas locales en `app/src/test` que se ejecutan en la JVM del ordenador, y pruebas instrumentadas en `app/src/androidTest` que necesitan Android. Una función pura no requiere arrancar un emulador. Una interacción real de Compose sí requiere su entorno. La [guía de pruebas locales](https://developer.android.com/training/testing/local-tests) explica sus límites con APIs de Android.

En el laboratorio, `tituloValido` es una función pura. Este primer caso resuelto está en `RevisionTest`:

```kotlin
@Test fun tituloNormal() {
    assertTrue(tituloValido("Tres letras"))
}
```

`@Test` identifica el caso; `assertTrue` falla si su condición es falsa. El caso no demuestra límites, nulabilidad ni todos los escenarios. El resto se construye con entradas que podrían romper una implementación aparentemente correcta.

## Prueba real, fake y mock

El `DemoRepository` del laboratorio devuelve dos notas tras una demora. Probarlo directamente permite comprobar su contrato de IDs y espera. En Cuaderno, una prueba de Room necesita además una base de datos real en Android; no se acredita Room sustituyendo su DAO por un objeto que devuelve lo esperado.

Un **fake** es una implementación pequeña con comportamiento propio, por ejemplo un repositorio en memoria que guarda y consulta. Un **mock** permite definir respuestas y comprobar interacciones. Ambos son dobles; ninguno demuestra que un servidor o una base reales funcionen.

El laboratorio fija **MockK 1.14.6** como dependencia de pruebas JVM, no de la aplicación distribuida. Su [publicación oficial](https://github.com/mockk/mockk/releases/tag/1.14.6) y [documentación](https://mockk.io/) permiten contrastar compatibilidad. Una respuesta para una función suspendida utiliza `coEvery`; una verificación suspendida usa `coVerify`:

```kotlin
val repo = mockk<NotaRepository>()
coEvery { repo.obtener() } returns listOf(Nota(7, "Lista propia"))
val modelo = RevisionViewModel(repo)
```

Es un fragmento dentro de una prueba con Main configurado, no un reemplazo del repositorio de producción. `mockk` crea un doble estricto: debes declarar qué devuelve. El ViewModel recibe el contrato por constructor, por lo que la prueba puede entregar ese doble sin abrir conexiones ni cambiar código global.

Usa `coVerify(exactly = 1)` cuando una sola consulta forme parte del comportamiento que quieres proteger. No verifiques cada llamada interna solo porque puedes: refactorizar una implementación equivalente no debería romper decenas de pruebas de detalles privados.

## Controlar el tiempo de las corrutinas

No añadas `Thread.sleep` a una prueba para «dar tiempo». Se vuelve lenta y frágil en otros equipos. `kotlinx-coroutines-test`, con la misma versión que corrutinas, ofrece `runTest` y un reloj virtual. La [guía oficial](https://developer.android.com/kotlin/coroutines/test) explica su planificador y dispatchers.

`runTest` ejecuta la función suspendida y permite avanzar las demoras gestionadas por sus dispatchers. `runCurrent()` ejecuta lo pendiente en el instante actual. `advanceTimeBy(n)` avanza el reloj; una tarea situada justo en el instante final puede necesitar `runCurrent()`. `advanceUntilIdle()` procesa el trabajo programado hasta que no queda trabajo pendiente, siempre que no hayas creado una tarea infinita.

El ViewModel utiliza `viewModelScope`, que depende de `Dispatchers.Main`. En una prueba JVM normal no hay Main de Android. `MainDispatcherRule` instala un `StandardTestDispatcher` antes de cada caso y restaura Main al terminar. Es infraestructura preparada: cambiar Main globalmente y olvidarte de restaurarlo puede contaminar la siguiente prueba.

La regla y `runTest` comparten planificador. Crear otros dispatchers con otros relojes puede dejar tareas fuera del control del caso. `delay` simulado es controlable; una llamada bloqueante real o un hilo independiente no se vuelve virtual automáticamente.

## Carga, error y recuperación

`RevisionViewModel` inicia una carga y publica `RevisionState`. La prueba no debe mirar solo la lista final: comprueba que antes hay carga, después hay datos, y no quedan a la vez indicadores incompatibles.

Una respuesta vacía es un éxito sin notas; un fallo conocido produce error. Después de reintentar correctamente, el error anterior debe desaparecer. Para comprobarlo, cambia la respuesta del doble y llama a la acción pública `cargar`. La prueba debe observar estado, no editar `_estado` a través de reflexión.

Al recargar, el ViewModel cancela la tarea anterior. El resultado lento no debe sustituir al nuevo ni transformar su cancelación en error. La solución tiene un caso con demoras distintas que detecta esa regresión. No demuestra comunicación HTTP ni persistencia: esas dependencias deben tener sus propias pruebas cuando existan.
