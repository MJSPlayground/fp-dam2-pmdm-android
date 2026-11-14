# Actividades autónomas

Parte de tu buscador terminado. Los retos siguientes amplían sus operaciones de datos; no exigen servicios externos ni nuevas bibliotecas. Puedes comprobar funciones puras mediante pruebas JVM desde Android Studio. La solución de referencia contiene código separado de los TODO del proyecto inicial.

## Un resumen fiable del catálogo

La biblioteca recibe listas que pueden repetir un libro con el mismo ID. Necesita saber cuántos libros distintos hay en cada categoría. Si un ID aparece varias veces, conserva la primera aparición. No mezcles dos libros diferentes por tener el mismo título.

El resultado debe ordenar los nombres de categoría y no modificar los datos recibidos. Con el catálogo de cinco libros del proyecto debe devolver Programación: 3 y Viajes: 2. Añadir otra vez el primer libro no debe aumentar el recuento. Una entrada vacía produce un resumen vacío.

## Dos fuentes sin esperar innecesariamente

La biblioteca combinará dos catálogos independientes. Una fuente tarda 400 ms y otra 600 ms en el laboratorio. Quiere disponer del conjunto cuando hayan terminado ambas, sin esperar primero una fuente para iniciar la segunda. El conjunto final conserva la primera aparición de cada ID y se presenta ordenado por título normalizado.

Si cualquiera falla, la operación completa debe fallar y no devolver un éxito parcial. Si quien solicita abandona la operación, ambas tareas deben poder cancelarse. Se permiten proveedores suspendidos simulados; no se pide desarrollar un servidor. Con las dos fuentes de prueba parcialmente coincidentes deben quedar los cinco IDs distintos. La duración lógica se aproxima a la fuente más lenta, sin sumar ambas demoras.

## Distinguir fallo y abandono

Una operación de catálogo devuelve libros o lanza un fallo conocido. La pantalla necesita convertir ese fallo en `ResultadoBusqueda(error = true)`, pero una cancelación debe llegar al propietario que la pidió. Si la operación termina bien, conserva su lista y no activa error ni carga.

Prueba tres entradas controladas: una operación exitosa, una que lanza `IllegalArgumentException` y otra que lanza `CancellationException`. No añadas mensajes de fallo para el abandono ni captures cualquier error de programación como si fuera una incidencia esperada del catálogo.

[Solución de las actividades — ZIP](../descargas/proyectos/asincronia-solucion.zip){ .md-button }

Las funciones de referencia están en `Retos.kt` y se comprueban en `CatalogoTest`. No forman parte del proyecto inicial. La [página de soluciones](soluciones.md#actividades-autonomas) identifica su correspondencia con estos requisitos.
