# Práctica: Kotlin desde Java

## Abrir el código de trabajo

Utiliza el [proyecto inicial de Cuaderno](../descargas/proyectos/desarrollo-inicial.zip). Ábrelo en Android Studio desde su carpeta raíz y espera a que termine la sincronización. La pantalla Android tiene su propia práctica; aquí trabajarás con funciones Kotlin que no dependen de Android.

Tras estudiar [Kotlin desde Java](kotlin.md), abre `app/src/main/java/es/mjsplayground/pmdm/desarrollo/cuaderno/ejercicios/KotlinEjercicios.kt`. Incluye `data class Tarea`, que representa un título y si la tarea está terminada, y cuatro funciones con `TODO(...)`. Esa función especial permite compilar un trabajo incompleto, pero lanza una excepción si se ejecuta: debes sustituirla por tu implementación.

Las comprobaciones están en `app/src/test/java/es/mjsplayground/pmdm/desarrollo/cuaderno/ejercicios/KotlinEjerciciosTest.kt`. El directorio `test` identifica pruebas locales de JVM, diferentes de las instrumentadas de Android. No necesitan un emulador.

La clase de pruebas lleva una anotación `@org.junit.Ignore(...)` para que el proyecto inicial pueda abrirse y compilarse antes de realizar los ejercicios. **Retira esa anotación cuando quieras comprobar tus implementaciones.** Pulsa el triángulo junto a la clase y selecciona Run. La ventana de pruebas indica qué comparación falla y qué valor esperaba. No cambies las expectativas para conseguir un resultado verde.

## Tratar un valor ausente

Una interfaz necesita una etiqueta legible aunque un dato opcional no exista. Implementa `etiqueta(nombre: String?): String` con estas condiciones:

- `null`, la cadena vacía y una cadena de espacios producen `Sin nombre`.
- Si hay contenido, se devuelve sin espacios al principio ni al final.
- No uses `!!`: el valor ausente es un caso previsto del contrato.

Compara `etiqueta(null)`, `etiqueta("   ")` y `etiqueta(" Ane ")`. El resultado debe ser `Sin nombre`, `Sin nombre` y `Ane`, respectivamente. Piensa por qué comprobar únicamente `nombre == null` no cubre los tres casos.

## Transformar una colección

Implementa `pendientes(tareas: List<Tarea>): List<String>`. Debe devolver los títulos de las tareas no terminadas, en el mismo orden en que aparecen. No modifiques la colección recibida.

Para la entrada `Leer` pendiente, `Entregar` terminada y `Revisar` pendiente, la salida será `[Leer, Revisar]`. Para una lista vacía, la salida será una lista vacía. Utiliza las operaciones `filter` y `map` explicadas en la teoría, y señala qué tipo de colección produce cada paso.

Una comparación Java ayuda a razonar: filtrar objetos y transformar cada objeto en un texto son dos operaciones diferentes. Una lambda recibe el elemento de una operación; el `it` de `filter` no representa la lista completa.

## Reutilizar una función

Implementa `resumen(tareas: List<Tarea>): String`. Debe producir `Pendientes: 2` si hay dos tareas pendientes y `Pendientes: 0` si no hay ninguna. Reutiliza `pendientes` para no mantener dos definiciones distintas de qué significa estar pendiente.

Aquí una plantilla de cadena sustituye la concatenación de Java. Comprueba por qué `"Pendientes: tareas.size"` devuelve un texto literal y no el tamaño de la colección filtrada.

## Usar when como expresión

Implementa `clasificar(nota: Int): String`:

| Entrada | Resultado |
| --- | --- |
| Fuera del intervalo 0…10 | `Inválida` |
| Entre 0 y 4 | `Revisar` |
| Entre 5 y 10 | `Superada` |

Haz que `when` produzca el valor que devuelve la función. Comprueba las fronteras −1, 0, 4, 5, 10 y 11; un ejemplo en el centro de cada intervalo no detecta un límite mal escrito.

## Comprobar y explicar

Ejecuta la clase completa: sus cuatro pruebas deben pasar después de retirar `@Ignore`. Si aparece una excepción que menciona un TODO, queda una función sin implementar. Si el resultado aparece como omitido, comprueba la anotación. Que compile no demuestra que hayas resuelto el ejercicio.

Explica qué significa `val` en el parámetro de la clase, por qué `String?` acepta `null` y qué diferencia hay entre `List<Tarea>` y `List<String>`. Después cambia la colección de entrada de una prueba y predice el resultado antes de ejecutar.

## Solución de referencia

[Descargar solución de Cuaderno — ZIP](../descargas/proyectos/desarrollo-solucion.zip).

La solución contiene las implementaciones en `ejercicios/KotlinEjercicios.kt` y las mismas pruebas activas en `src/test`. Para revisar solo este ejercicio, ejecuta `KotlinEjerciciosTest`; no necesitas estudiar todavía las pantallas o la persistencia de la solución final.
