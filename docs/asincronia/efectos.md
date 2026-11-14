# Efectos y recomposición

## Dibujar no es iniciar trabajo

Compose puede volver a ejecutar una función para actualizar una parte de la pantalla. También puede descartar una composición. Si el cuerpo llama directamente a una petición, registra un listener o navega, puede repetir acciones que no corresponden a nuevos eventos.

La función composable describe qué se ve a partir del estado. Las acciones del usuario se conectan a callbacks; los trabajos que dependen de estar en la composición se conectan a APIs de efecto. La [guía de efectos de Compose](https://developer.android.com/develop/ui/compose/side-effects) explica sus propietarios y reinicios. No todo trabajo asíncrono necesita un efecto: la búsqueda del catálogo ya pertenece al ViewModel.

## `LaunchedEffect`: una tarea con clave

En la práctica hay un mensaje que indica si ha pasado un segundo sin cambiar el campo. Cada cambio de consulta debe reiniciar la espera. Dentro de un composable, la estructura necesaria es:

```kotlin
LaunchedEffect(consulta) {
    estable = false
    delay(1_000)
    estable = true
}
```

`consulta` es la clave. Al cambiar, Compose cancela el bloque anterior e inicia otro. Una recomposición por otra causa, con la misma clave, no reinicia ese bloque. Al salir de la composición también se cancela. En el proyecto, `estable` es estado local recordado; cambiarlo actualiza el texto de la UI.

No uses `Unit` como clave por costumbre. Significa que los cambios de consulta no reiniciarían ese trabajo. Tampoco uses el propio resultado `estable` como clave: lo modificas dentro del efecto y podrías producir reinicios no deseados. La clave debe representar aquello cuya variación vuelve inválida la tarea.

Este temporizador enseña un efecto local; no decide cuándo buscar. El `debounce` del ViewModel y el mensaje tienen relojes distintos y finalidades diferentes. Duplicar la consulta de datos en el efecto crearía dos caminos para el mismo estado.

## Eventos con `rememberCoroutineScope`

Un botón de Ayuda muestra un snackbar. `showSnackbar` es suspendida y el evento `onClick` es una lambda normal. Se obtiene un scope durante la composición y se utiliza al producirse el clic:

```kotlin
val scope = rememberCoroutineScope()
val mensajes = remember { SnackbarHostState() }

Button(onClick = {
    scope.launch { mensajes.showSnackbar(textoAyuda) }
}) {
    Text("Ayuda")
}
```

Es un fragmento ilustrativo; la aplicación completa usa recursos de texto y conecta `mensajes` con el `SnackbarHost` de Scaffold. La corrutina comienza **al pulsar**, no al dibujar el botón. Cuando desaparece ese punto de composición, su scope se cancela. Un snackbar local no necesita ocupar el ViewModel ni sobrevivir a la pantalla que lo muestra.

Llamar a `scope.launch` directamente en el cuerpo del composable sigue siendo un efecto sin control, aunque el scope esté recordado. El lugar del lanzamiento importa.

## `DisposableEffect`: registrar y retirar

Algunos sistemas notifican mediante listeners en lugar de funciones suspendidas. El laboratorio observa el ciclo de vida para contar entradas en estado iniciado. El observador no almacena datos personales ni envía analítica; es una demostración local de alta y baja simétricas.

```kotlin
DisposableEffect(propietario) {
    val observador = LifecycleEventObserver { _, evento ->
        if (evento == Lifecycle.Event.ON_START) retornos++
    }
    propietario.lifecycle.addObserver(observador)
    onDispose { propietario.lifecycle.removeObserver(observador) }
}
```

El fragmento está dentro de la pantalla; `propietario` se obtiene de `LocalLifecycleOwner` y `retornos` es estado recordado. Si cambia el propietario, se retira el listener viejo antes de registrar el nuevo. Si la pantalla desaparece, también se retira. Debe quitarse **la misma instancia** que se registró, no crear otro observador parecido dentro de `onDispose`.

`onDispose` no es una corrutina suspendida. Es el lugar para liberar el registro o recurso síncrono que ese efecto posee. Si no hay nada que liberar y solo necesitas suspender, probablemente `LaunchedEffect` expresa mejor la intención.

## Usar el valor reciente sin reiniciar

Puede ocurrir que una tarea larga deba conservarse, pero al terminar llame al callback más reciente. `rememberUpdatedState(callback)` ofrece una referencia actualizada sin usar el callback como clave del efecto. No se necesita en el temporizador del catálogo: queremos reiniciar al cambiar el texto. Conocer el patrón evita tanto reinicios caros como capturar valores obsoletos; úsalo solo cuando ese sea el contrato.

## Evitar trabajo repetido con criterio

`remember(clave)` conserva un valor mientras la clave no cambie; no lo guarda en disco ni sobrevive por sí mismo a recreaciones. `derivedStateOf` es útil cuando un estado cambia con mucha frecuencia y la UI solo necesita un resultado derivado que cambia menos. Por ejemplo, un botón «subir» que aparece al superar cierto índice de scroll.

No añadas `derivedStateOf` a cada concatenación de texto: tiene coste. Tampoco esperes que `remember` detecte modificaciones internas de una lista mutable normal. En nuestro buscador, los filtros y ordenación están fuera de la composición, y las filas usan `key = { it.id }`. Esto evita recalcular la consulta por cada dibujo y conserva la identidad de cada fila durante reordenaciones.

Para catálogos mayores, comienza midiendo con herramientas de composición y perfiles; la [guía de rendimiento](https://developer.android.com/develop/ui/compose/performance/bestpractices) incluye retrasar lecturas de estado y claves estables. Un contador de recomposiciones alto no demuestra por sí solo lentitud. Comprueba trabajo realizado, fluidez y resultado antes y después de cualquier optimización.
