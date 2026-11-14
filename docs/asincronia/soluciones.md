# Soluciones de asincronía

## Repaso

1. Es `List<String>`. `map` cambia el tipo de cada elemento; una cadena no posee el ID del libro original. Si necesitas título e ID, conserva el libro o crea un tipo de salida con ambos.
2. Queda una entrada por clave; las apariciones posteriores sustituyen valores anteriores. No advierte del conflicto. La detección exige comparar o agrupar apariciones según el contrato elegido.
3. No cambia de hilo. Una secuencia retrasa el recorrido hasta una operación terminal; puede evitar trabajo intermedio, pero el cálculo ocurre en el contexto que la consume.
4. `Thread.sleep` bloquea también dentro de `suspend`. `delay` suspende cooperativamente y reconoce cancelación; no bloquea el hilo durante la espera.
5. Se ha introducido espera antes de iniciar la segunda. Crea ambos `Deferred` primero y espera después dentro del mismo ámbito estructurado. El tiempo conjunto depende de la fuente más lenta si las esperas se solapan.
6. Al escribir una consulta nueva, la anterior se cancela para sustituirla. Mostrar «falló el catálogo» por esa cancelación confunde una acción normal con un error. Relanzar `CancellationException` conserva la semántica de abandono.
7. Una recreación de configuración normalmente conserva el ViewModel. La terminación del proceso no. Mantener un objeto en memoria no sustituye Room ni otra persistencia.
8. Pasa `kot` tras 300 ms sin cambios desde su emisión. Las entradas previas fueron sustituidas antes de cumplir su pausa. No es una consulta por cada intervalo fijo de reloj.
9. `combine` conserva los últimos valores de ambas fuentes y vuelve a transformar al cambiar cualquiera, después de la primera emisión de las dos. Hacer depender todo de nuevas letras dejaría el interruptor sin efecto inmediato.
10. Se necesitan al menos dos emisiones por criterio: carga y resultado. `transformLatest` permite varias llamadas a `emit`. Si el trabajo no coopera, la cancelación puede retrasarse y no libera inmediatamente el recurso bloqueante.
11. El `catch` terminal puede emitir una alternativa y completar; no reabre la fuente automáticamente. El laboratorio captura el fallo esperado dentro de cada transformación y continúa observando los próximos criterios.
12. Una clave constante no cambia por modificar la consulta. La clave debe ser `consulta` porque cada texto invalida la espera anterior. El estado de salida `estable` no es la identidad de la tarea.
13. La recomposición puede registrar varias veces y el objeto que se retira no coincide con el registrado. Registra una instancia dentro de `DisposableEffect` y retira esa misma instancia al finalizar o cambiar su clave.
14. Proporciona un ámbito vinculado a ese punto de composición. El `launch` debe ejecutarse dentro del evento del botón; no automáticamente al recomponer. El ámbito se cancela al salir de la composición.
15. `remember` conserva una referencia; no convierte en observable cualquier mutación interna. Publica estado que Compose pueda observar y mide tiempo, trabajo y fluidez con herramientas de perfil, además de observar recomposiciones, antes de optimizar.

## Actividades autónomas

[Proyecto solución y pruebas — ZIP](../descargas/proyectos/asincronia-solucion.zip){ .md-button }

Abre la carpeta de la solución en Android Studio. La pantalla se ejecuta desde **app**. Para las actividades de datos, abre `CatalogoTest` en el source set **test** y utiliza Run sobre la clase; no necesitan un emulador ni conexión.

`Retos.kt` separa tres funciones de referencia:

| Requisito | Función | Evidencia que comprueba la prueba |
| --- | --- | --- |
| Resumen por categoría | `resumenPorCategoria` | Recuentos, duplicados y lista vacía |
| Dos fuentes | `catalogoDoble` | Demoras solapadas, cinco IDs distintos y orden de salida |
| Fallo o abandono | `intentarConsulta` | Fallo esperado convertido y cancelación propagada |

La primera conserva por ID antes de agrupar; la segunda crea ambas subtareas dentro de `coroutineScope`; la tercera solo transforma el fallo conocido. Son respuestas a los contratos de estas actividades, no reglas que deban imponerse a cualquier catálogo.

Las pruebas de Flow utilizan `runTest` y avanzan el tiempo virtual de la demora simulada. No cuentan la velocidad del ordenador como si fuera la latencia de una red real. Las comprobaciones de UI de la práctica siguen siendo necesarias y sus resultados se documentan por separado.
