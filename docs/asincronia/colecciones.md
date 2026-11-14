# Colecciones, extensiones y funciones

## Expresar una transformación

En Java puedes recorrer una lista con un bucle y añadir coincidencias a otra. Kotlin permite describir la transformación mediante funciones que reciben lambdas. El criterio para elegirlas es que el recorrido quede comprensible; una cadena muy larga no es mejor por tener menos líneas.

Supongamos una lista de `Libro(id, titulo, categoria, favorito)`. Antes de mostrarla queremos conservar únicamente los favoritos, extraer sus títulos y ordenarlos. El siguiente fragmento trabaja con esa lista ya disponible; no crea un proyecto ni una pantalla:

```kotlin
val titulos = libros
    .filter { it.favorito }
    .map { it.titulo }
    .sorted()
```

`filter` conserva elementos del mismo tipo; `map` transforma cada elemento en otro valor. Aquí el resultado es una `List<String>`, no una lista de libros. Los objetos originales no desaparecen ni cambian de posición en la lista de entrada. Cada función devuelve su resultado para la siguiente operación. Esta diferencia es útil al representar una vista filtrada sin perder el catálogo completo.

La [documentación de operaciones de colecciones](https://kotlinlang.org/docs/collection-operations.html) diferencia las funciones que generan resultados de las que modifican una colección mutable. `sortedBy` devuelve otra lista; `sortBy` modifica una `MutableList`. En estado de Compose, publicar una nueva lista facilita detectar el cambio; modificar por dentro una lista normal no la convierte en observable.

## Una lambda puede tener nombre de parámetro

`it` es cómodo en operaciones pequeñas. En cuanto hay anidamiento, los nombres explican mejor qué dato se utiliza:

```kotlin
val conRutas = libros.filter { libro ->
    libro.titulo.contains("rutas", ignoreCase = true)
}
```

La lambda recibe un `Libro` y devuelve `Boolean`. No imprime el resultado ni actualiza automáticamente la interfaz: decide si ese libro permanece. Una función de orden superior es una función que recibe o devuelve funciones; `filter` es un ejemplo. En Java el equivalente conceptual sería un `Predicate<Libro>`.

Podemos recibir el criterio en una función reutilizable. Este fragmento presupone la misma clase `Libro` del laboratorio:

```kotlin
fun seleccionar(libros: List<Libro>, acepta: (Libro) -> Boolean): List<Libro> =
    libros.filter(acepta)

val favoritos = seleccionar(libros) { it.favorito }
```

La función no conoce por qué el usuario filtra; solo aplica el criterio. Evita convertir cada expresión corta en una abstracción: si el criterio se usa una vez y el nombre no aclara nada, `filter` directamente puede bastar.

## Extensiones y normalización

Buscar suele exigir ignorar espacios exteriores y diferencias de mayúsculas. En el proyecto hay una extensión `String.normalizada()`. Una [función de extensión](https://kotlinlang.org/docs/extensions.html) permite escribir una operación como si fuera miembro, sin modificar la clase original ni heredar de ella.

La implementación utiliza `trim().lowercase(Locale.ROOT)`. `Locale.ROOT` hace que esta normalización técnica no cambie según el idioma del dispositivo. No elimina tildes: `camara` y `cámara` siguen siendo cadenas diferentes. Si el producto necesita búsqueda insensible a acentos, eso requiere otra decisión y sus pruebas.

```kotlin
val consulta = "  KoTLin  ".normalizada()
// consulta contiene "kotlin"
```

Una extensión no puede acceder a miembros privados de `String` ni cambiar el despacho de sus métodos. Si existe un miembro con la misma firma, el miembro tiene prioridad. Utiliza nombres que expresen su contrato, no nombres que parezcan operaciones universales cuando aplican reglas particulares de tu aplicación.

## Agrupar, asociar y evitar pérdidas silenciosas

`groupBy` produce un mapa donde cada clave se asocia a una **lista** de elementos. Con tres libros de Programación y dos de Viajes se obtienen dos grupos. Si únicamente necesitamos contar, `groupingBy { ... }.eachCount()` evita construir todas las listas de grupos.

`associateBy { it.id }` produce un mapa con un elemento por ID. Si hay IDs repetidos, una entrada sustituye a la anterior. No lo uses para «detectar» duplicados: puede ocultarlos. `distinctBy` conserva la primera aparición de cada clave; también descarta información y debes decidir si ese es el contrato correcto. Estas operaciones se explican en [agrupación](https://kotlinlang.org/docs/collection-grouping.html) y [transformaciones](https://kotlinlang.org/docs/collection-transformations.html).

En el laboratorio hay IDs repetibles en las actividades autónomas para que esta decisión tenga un efecto comprobable. Separar `id` y `titulo` evita tratar dos libros diferentes con igual título como si fueran el mismo.

## Secuencias y coste

En una `List`, las operaciones intermedias suelen producir colecciones. Una [Sequence](https://kotlinlang.org/docs/sequences.html) retrasa la transformación hasta una operación terminal como `toList`, `count` o `first`. Puede evitar listas temporales o trabajo innecesario, pero añade su propio coste y no vuelve asíncrono el procesamiento.

```kotlin
val primeros = libros.asSequence()
    .filter { it.favorito }
    .map { it.titulo }
    .take(2)
    .toList()
```

Se buscan dos títulos aceptados y se deja de recorrer cuando se consiguen. En un catálogo de cinco elementos, una lista normal resulta sencilla y suficiente. En grandes volúmenes conviene medir antes de cambiar. Una secuencia, un Flow y una corrutina tienen propósitos diferentes: evaluación perezosa, emisiones en el tiempo y ejecución suspendible, respectivamente.
