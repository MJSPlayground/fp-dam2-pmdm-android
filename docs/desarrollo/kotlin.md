# Kotlin desde Java

## Variables, valores y tipos

Kotlin y Java pueden convivir en un proyecto Android y se ejecutan sobre el mismo entorno de la plataforma, pero expresan de forma diferente algunas decisiones. No traduciremos cada punto y coma: buscaremos cómo representar los mismos datos con tipos claros y menos estado modificable.

En Java escribes el tipo antes del nombre. En Kotlin puedes declararlo después de dos puntos o permitir que el compilador lo deduzca. Este fragmento podría estar dentro de una función:

```kotlin
val titulo: String = "Lista de viaje"
val maximo = 80
var pendientes = 3
pendientes += 1
```

`val` permite asignar la referencia una sola vez; `var` permite reasignarla. La inferencia de `maximo` produce un `Int`: no convierte a Kotlin en un lenguaje de tipos dinámicos. Si intentas después `pendientes = "cuatro"`, la compilación falla porque una cadena no es un entero.

Prefiere `val` cuando el valor no tenga que cambiar. Sin embargo, `val` no convierte un objeto mutable en inmutable: una referencia `val` a una lista modificable sigue permitiendo modificar esa lista. En los estados de interfaz usaremos valores nuevos y colecciones de solo lectura para que los cambios sean explícitos.

Los tipos habituales incluyen `Int`, `Long`, `Double`, `Boolean` y `String`. Para los identificadores de nuestras notas utilizaremos `Long`. El literal `42L` es un `Long`; `42` se infiere normalmente como `Int`. Kotlin no convierte automáticamente cualquier variable numérica a otra: si una función requiere `Long` y tienes un `Int`, utiliza una conversión consciente como `numero.toLong()`.

Las [reglas de tipos básicos](https://kotlinlang.org/docs/basic-types.html) y la [sintaxis básica](https://kotlinlang.org/docs/basic-syntax.html) son referencias de consulta; los ejemplos siguientes muestran cómo se aplican a nuestros datos.

## Cadenas y expresiones

Las plantillas de texto evitan concatenaciones repetidas. Para una variable basta `$nombre`; para una expresión utiliza `${...}`:

```kotlin
val titulo = "Viaje"
val resumen = "$titulo tiene ${titulo.length} caracteres"
```

El valor de `resumen` es `Viaje tiene 5 caracteres`. Las comillas dobles crean una cadena; las simples se reservan para un carácter, por ejemplo `'A'`. En una interfaz Android externalizaremos los textos visibles en recursos para poder traducirlos; esta sintaxis sigue siendo útil en cálculos y ejemplos del lenguaje.

En Kotlin, `if` puede devolver un valor. Eso permite asignar una descripción sin declarar una variable que después modificamos en dos ramas:

```kotlin
val titulo = "  "
val estado = if (titulo.isBlank()) "Falta el título" else "Título válido"
```

`isBlank()` devuelve `true` para una cadena vacía o formada únicamente por espacios en blanco. La expresión anterior produce `Falta el título`. Es diferente de comprobar solo `isEmpty()`: una cadena con espacios no está vacía.

`when` expresa varias alternativas. En este ejemplo cada rama devuelve un texto:

```kotlin
fun descripcionRespuesta(codigo: Int): String = when (codigo) {
    in 200..299 -> "Operación aceptada"
    401, 403 -> "Acceso no permitido"
    404 -> "No encontrado"
    else -> "La operación no se completó"
}
```

`in 200..299` comprueba un intervalo cerrado, incluidos ambos extremos. La coma permite agrupar casos y `else` cubre los restantes. No confundir este ejemplo de clasificación con una gestión HTTP completa: más adelante distinguiremos respuestas del servidor y fallos donde ni siquiera llega una respuesta.

## Funciones y argumentos

Una función declara sus parámetros como `nombre: Tipo`. Después de los paréntesis se indica el tipo devuelto. Para una única expresión podemos utilizar `=` en lugar de un bloque y `return`:

```kotlin
fun tituloValido(titulo: String, limite: Int = 80): Boolean =
    titulo.trim().length in 1..limite

val aceptado = tituloValido(titulo = "Comprar pan")
val otroLimite = tituloValido("Comprar pan", limite = 5)
```

`trim()` quita espacios en blanco de los extremos y no modifica la cadena original. La primera llamada utiliza el límite por defecto y devuelve `true`; la segunda devuelve `false`. Los argumentos con nombre ayudan a entender una llamada cuando hay varios parámetros del mismo tipo. Para una función sin resultado útil, Kotlin utiliza `Unit`, que normalmente se omite.

Los parámetros son valores de solo lectura dentro de la función. Si necesitas un resultado transformado, devuelve ese resultado en vez de intentar reasignar el parámetro. Esa forma de trabajar encaja con el flujo de estado que emplearemos en Compose.

## Recorrer datos

El bucle `for` recorre elementos, no necesita el patrón Java `for (int i = 0; ...)` cuando solo queremos cada valor:

```kotlin
val titulos = listOf("Viaje", "Compra", "Ideas")
for (titulo in titulos) {
    println(titulo)
}
```

La salida contiene los tres títulos en ese orden. `listOf` crea una lista con una interfaz de solo lectura. Para recorrer índices, `titulos.indices` ofrece los índices válidos; así evitamos el error `0..titulos.size`, cuyo último valor queda fuera de la lista. `while` y `do/while` tienen un uso similar al de Java cuando el número de iteraciones depende de una condición.

## Clases, propiedades y datos

Kotlin permite declarar el constructor principal en la cabecera de la clase. Al escribir `val` o `var` delante de un parámetro del constructor, este se convierte además en una propiedad:

```kotlin
data class Nota(
    val id: Long = 0,
    val titulo: String,
    val contenido: String
)

val original = Nota(titulo = "Viaje", contenido = "Llevar cargador")
val corregida = original.copy(titulo = "Viaje a la costa")
```

`original` conserva el título `Viaje`; `corregida` es otro objeto con el título cambiado y el mismo contenido. `data class` genera operaciones útiles para representar datos: comparación por sus propiedades del constructor, `toString` y `copy`, entre otras. No necesita getters escritos a mano: en Kotlin se accede con `original.titulo`.

`copy` realiza una copia superficial. Si una propiedad contuviera un objeto mutable, no se clonaría por dentro. Por eso no debemos deducir que cualquier `data class` es automáticamente inmutable. Aquí sus propiedades son valores y cadenas, suficientes para un modelo sencillo.

El `id = 0` será una convención de nuestra base de datos para una nota todavía sin identificador asignado. No significa que el cero sea un identificador válido en cualquier servicio. Tampoco un identificador remoto y otro local son intercambiables porque ambos sean `Long`.

En una clase ordinaria puedes añadir funciones y un bloque `init` para comprobar condiciones al construirla. Utiliza una `data class` para datos cuando aporte valor; no conviertas todas las clases en datos por costumbre. Consulta [clases](https://kotlinlang.org/docs/classes.html) y [clases de datos](https://kotlinlang.org/docs/data-classes.html).

## Representar la ausencia: nulabilidad

En Java una referencia puede ser `null` aunque el tipo no lo muestre. En Kotlin, `String` y `String?` son tipos diferentes: el segundo permite un valor ausente. Eso obliga a decidir qué hacer antes de utilizarlo.

Supongamos que una búsqueda puede no encontrar una nota:

```kotlin
val encontrada: Nota? = null
val titulo: String = encontrada?.titulo ?: "Nota no disponible"
```

La llamada segura `?.` obtiene la propiedad solo si el objeto existe; si no, produce `null`. El operador Elvis `?:` utiliza la alternativa cuando su lado izquierdo es `null`. El resultado es `Nota no disponible`. Una cadena vacía no es `null`: si recibes `""`, Elvis no la sustituye.

Cuando necesitas trabajar con varias propiedades, una comprobación también resulta clara:

```kotlin
fun resumen(nota: Nota?): String {
    if (nota == null) return "No existe la nota"
    return "${nota.titulo}: ${nota.contenido.take(30)}"
}
```

Después de la comprobación, el compilador sabe que ese parámetro no es nulo. `take(30)` devuelve como máximo los primeros 30 caracteres. Evita `nota!!`: exige que el valor no sea nulo y provoca una excepción si te equivocas. No es una solución a que el compilador te pida tratar un caso que realmente puede ocurrir.

La [guía de seguridad frente a nulos](https://kotlinlang.org/docs/null-safety.html) explica también los límites al interoperar con Java. En Android encontrarás referencias de APIs Java: no des por hecho que todo valor externo cumple tus expectativas.

## Lambdas y colecciones

Una **lambda** es una función que se puede pasar como valor. En Java ya conoces expresiones como `nota -> nota.getTitulo()`. En Kotlin se escriben entre llaves; el parámetro y la expresión se separan con `->`:

```kotlin
val notas = listOf(
    Nota(1, "Viaje", "Tren a las nueve"),
    Nota(2, "Compra", "Pan y fruta")
)
val titulos = notas.map { nota -> nota.titulo }
val largas = notas.filter { nota -> nota.contenido.length > 15 }
val encontrada = notas.find { nota -> nota.id == 2L }
```

`map` transforma cada elemento y devuelve una lista nueva: aquí, `Viaje` y `Compra`. `filter` conserva los que cumplen una condición sin cambiar la lista original. `find` devuelve el primer elemento que la cumple, o `null` si ninguno coincide: por eso su resultado es `Nota?`.

Cuando una lambda tiene un solo parámetro, Kotlin permite llamarlo `it`: `notas.map { it.titulo }`. Utiliza nombres explícitos si hay lambdas anidadas o el significado no es evidente. Una lambda final puede escribirse fuera de los paréntesis; esa regla es la que permite el aspecto de las llamadas `Button(...) { ... }` que veremos en Compose.

Las [operaciones de colecciones](https://kotlinlang.org/docs/collection-transformations.html) evitan bucles auxiliares, pero no eliminan el coste de procesar muchos datos. No encadenes operaciones complejas si un bucle o una función con nombre explica mejor la intención.

## Funciones de extensión

Una extensión permite llamar a una función como si perteneciera a un tipo sin cambiar su clase. Aquí definimos una transformación de cadenas:

```kotlin
fun String.comoTitulo(): String = trim().take(80)

val titulo = "  Idea para el sábado  ".comoTitulo()
```

Dentro de la extensión, `trim()` y `take()` operan sobre la cadena receptora. El resultado es `Idea para el sábado`. No has añadido un campo ni cambiado la clase `String`; la extensión se resuelve como una función. Úsala para operaciones que tienen sentido sobre ese tipo y cuyo nombre explica el efecto. Una extensión no debería esconder una petición de red detrás de un nombre aparentemente trivial.

## Suspender una operación

Consultar una API o una base de datos requiere esperar. Bloquear el hilo principal mientras esperas impide responder a pulsaciones y dibujar con fluidez. Las **corrutinas** permiten organizar ese trabajo asíncrono sin convertir cada operación en una cadena de callbacks.

Una función marcada `suspend` puede suspender su ejecución y retomarla después. Solo se llama desde otra función suspendida o desde una corrutina. La palabra no crea por sí sola una corrutina ni convierte cualquier trabajo en trabajo de fondo:

```kotlin
// Fragmento de un ViewModel; viewModelScope se explicará en arquitectura.
fun cargar() {
    viewModelScope.launch {
        val publicaciones = api.obtenerPublicaciones()
        // Aquí se actualiza el estado con el resultado.
    }
}
```

Retrofit ofrece funciones `suspend` para peticiones y Room para operaciones de base de datos. Estas bibliotecas gestionan su trabajo bloqueante. Un cálculo pesado o una lectura de archivo que tú escribas no se vuelve seguro por añadir `suspend`: necesitará un contexto adecuado. Tampoco debes usar `Thread.sleep` para simular una espera en la interfaz. Cuando estudiemos datos utilizaremos estas operaciones en un ámbito asociado al ViewModel y trataremos errores y cancelación.

Esta introducción basta para seguir el itinerario. La [documentación de corrutinas Android](https://developer.android.com/kotlin/coroutines) y la de [funciones suspendidas](https://kotlinlang.org/docs/composing-suspending-functions.html) amplían el modelo; ámbitos, cancelación y composición avanzada se desarrollan en su ampliación.
