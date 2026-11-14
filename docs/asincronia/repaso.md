# Repaso de asincronía y efectos

## Comprender los datos

1. Una función filtra `List<Libro>` y aplica `map { it.titulo }`. ¿Cuál es el tipo final y por qué ya no puedes utilizar directamente `it.id` sobre sus elementos?
2. ¿Qué sucede si dos elementos tienen la misma clave en `associateBy`? ¿Sirve para advertir de un ID duplicado?
3. ¿Convertir una lista en secuencia traslada su cálculo a un hilo de fondo? ¿Cuándo empieza a recorrerse?

## Razonar sobre tareas

4. Una función marcada `suspend` ejecuta `Thread.sleep(1000)` en Main. ¿Deja libre el hilo durante ese segundo? ¿Qué cambia al usar `delay`?
5. Dos `async` se crean uno después de otro, pero se espera el primero antes de crear el segundo. ¿Hay solapamiento útil entre las consultas? Propón el orden de creación y espera sin escribir toda la función.
6. Un `catch (Exception)` convierte una `CancellationException` en estado de error. Explica un caso visible en que eso confunde al usuario.
7. ¿Al girar la pantalla se limpia necesariamente el ViewModel? ¿Qué diferencia hay entre conservar el propietario y conservar datos en disco?

## Interpretar Flow

8. Se escriben `k`, `ko` y `kot` a intervalos de 100 ms; después no hay más cambios. Con `debounce(300)`, ¿qué texto supera la pausa? ¿Desde qué cambio se mide?
9. Se activa favoritos sin volver a escribir. ¿Por qué `combine` vuelve a emitir criterios? ¿Qué problema tendría esperar siempre una nueva emisión de texto para aplicar el interruptor?
10. ¿Por qué la solución usa `transformLatest` en vez de `mapLatest`? ¿Qué cambia si una consulta no coopera con la cancelación?
11. Se coloca `catch { emit(error) }` al final de todo el flujo y la fuente termina por fallo. ¿Escribir otra consulta garantiza que vuelva a recogerse? ¿Dónde gestiona los errores este laboratorio?

## Detectar efectos incorrectos

12. El temporizador usa `LaunchedEffect(Unit)` y captura `consulta`. ¿Se reinicia al escribir? ¿Qué valor debería identificar esa tarea?
13. Se registra un listener dentro del cuerpo composable y se retira otro objeto nuevo en `onDispose`. Identifica los dos problemas.
14. Un botón llama directamente a una función suspendida. ¿Qué aporta `rememberCoroutineScope` y dónde debe ejecutarse su `launch`?
15. ¿Por qué `remember` no soluciona por sí solo una lista mutable que cambia internamente? ¿Qué medirías antes de introducir una optimización de recomposición?

[Respuestas razonadas](soluciones.md)
