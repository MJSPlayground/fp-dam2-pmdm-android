# Navegar entre pantallas

## Pantallas, destinos y pila

Cuaderno necesita una lista de notas, un formulario de nueva nota, un detalle editable y una pantalla de publicaciones remotas. Son tareas diferentes dentro de una misma Activity. Para pasar entre ellas utilizaremos **Navigation Compose**, en la versión fijada para el curso. No mezclaremos en la práctica varias bibliotecas de navegación equivalentes.

Un **destino** representa una ubicación navegable. La **ruta** identifica el destino y los argumentos necesarios. El **NavController** gestiona la navegación y la pila de destinos; **NavHost** muestra el contenido correspondiente al destino actual.

Piensa en la pila como un historial de entradas. Al abrir una nota desde la lista se añade su detalle. Volver elimina el detalle y muestra la lista que quedaba debajo:

```text
Inicio:          [ Lista ]
Abrir nota 7:    [ Lista, Detalle(7) ]
Volver:          [ Lista ]
```

No es lo mismo volver que navegar de nuevo a Lista. Si al cerrar cada detalle llamaras a `navigate(Lista)`, podrías acumular copias del listado y el botón Atrás recorrería un historial extraño. La [guía de pila de navegación](https://developer.android.com/guide/navigation/backstack) explica cómo se añaden y retiran esas entradas.

## Rutas tipadas

Una ruta construida concatenando texto es fácil de escribir mal o de interpretar con un tipo incorrecto. Las rutas tipadas representan los destinos con objetos y clases Kotlin. Se utiliza Kotlin Serialization para describir sus argumentos.

El siguiente archivo declara los destinos de Cuaderno. Un objeto representa un destino sin argumentos; una clase de datos representa el detalle que necesita un identificador:

```kotlin title="navigation/Destinos.kt"
package es.mjsplayground.pmdm.desarrollo.cuaderno.navigation

import kotlinx.serialization.Serializable

@Serializable
object Lista

@Serializable
object Nueva

@Serializable
object Remotos

@Serializable
data class Detalle(val id: Long)
```

`@Serializable` permite generar el serializador de esas rutas. Requiere el plugin `org.jetbrains.kotlin.plugin.serialization` y las dependencias de navegación y serialización del proyecto. Una anotación sin su plugin no genera por sí sola todo lo necesario.

`Detalle(7L)` expresa una entrada al detalle de la nota siete. Pasamos solo el identificador. No enviamos la nota completa en una ruta: podría quedar desactualizada, ocupar demasiado o mezclar la navegación con la persistencia. El destino recupera el dato actual utilizando ese identificador.

La [documentación de rutas tipadas](https://developer.android.com/guide/navigation/design/type-safety) utiliza este modelo y especifica las versiones en que está disponible.

## Conectar el grafo con las pantallas

`rememberNavController()` conserva un controlador para la composición. El grafo declara su destino inicial y el contenido de cada destino. Este fragmento ilustra la conexión; las funciones de pantalla reciben datos y eventos, y el código completo incorpora la lectura del ViewModel:

```kotlin title="Fragmento de la raíz de navegación"
val navController = rememberNavController()

NavHost(navController = navController, startDestination = Lista) {
    composable<Lista> {
        ListaPantalla(
            onNueva = { navController.navigate(Nueva) },
            onAbrir = { id -> navController.navigate(Detalle(id)) },
            onRemotos = { navController.navigate(Remotos) }
        )
    }
    composable<Nueva> {
        NuevaPantalla(onVolver = { navController.popBackStack() })
    }
    composable<Detalle> { entrada ->
        val ruta = entrada.toRoute<Detalle>()
        DetallePantalla(
            id = ruta.id,
            onVolver = { navController.popBackStack() }
        )
    }
    composable<Remotos> {
        RemotosPantalla(onVolver = { navController.popBackStack() })
    }
}
```

Las funciones `ListaPantalla`, `NuevaPantalla`, `DetallePantalla` y `RemotosPantalla` son nombres ilustrativos de los contenidos de cada destino; no pretenden sustituir el archivo completo del proyecto. La idea importante es que las pantallas reciben `onVolver` u `onAbrir`, en lugar de crear controladores propios.

`composable<Detalle>` declara el tipo de ruta que entiende el destino; `toRoute<Detalle>()` reconstruye sus argumentos desde la entrada. Esas funciones necesitan sus imports de Navigation Compose y `androidx.navigation.toRoute`. Un error de tipo en el argumento se detecta antes que en una ruta montada a mano con cadenas.

No llames a `navigate` directamente en el cuerpo de una pantalla solo porque hay datos: la recomposición podría repetirlo. La apertura desde un botón debe ocurrir en su callback. La [guía de navegación a destinos](https://developer.android.com/guide/navigation/use-graph/navigate) distingue la declaración del grafo de las acciones que lo recorren.

## Recuperar datos y tratar un detalle inexistente

Una ruta válida no garantiza que la nota exista. Puede haberse eliminado, el identificador puede no pertenecer a la base actual o el proceso puede haber sido restaurado con datos distintos. El detalle debe mostrar una situación de «nota no disponible» y permitir volver, en vez de utilizar `!!` para exigir que una búsqueda tenga éxito.

Cuando un ViewModel pertenece a un destino, sus argumentos también pueden recuperarse con `SavedStateHandle.toRoute<Detalle>()`. Esto permite que ese ViewModel obtenga el identificador sin recibir un NavController. No cambies a otra solución de navegación por usar estado guardado: continúa siendo la misma ruta tipada.

La responsabilidad sigue siendo clara: navegación transporta el identificador; el repositorio busca la nota; la interfaz presenta carga, contenido o ausencia. Las consultas y los permisos no deben esconderse dentro de la clase que declara los destinos.

## Guardar y regresar

Al crear una nota, primero se valida el borrador y se solicita el guardado. Si termina correctamente, podemos volver a la lista. Si falla, mantenemos el formulario con sus valores y el error. Volver inmediatamente al pulsar Guardar ocultaría la diferencia entre operación iniciada y operación completada.

`popBackStack()` intenta retirar la entrada actual y devuelve si lo ha conseguido. En el destino raíz no hay una pantalla anterior del grafo a la que volver; el botón Atrás del sistema puede salir de la aplicación. No añadas un bucle que vuelva a navegar a Lista cada vez que la pila se queda en su raíz.

Al eliminar una nota desde su detalle, la confirmación debe actuar sobre su identificador. Tras el éxito se abandona ese detalle, porque ya no tiene un contenido válido que editar. Volver desde un detalle normal no crea otra nota ni vuelve a consultar el servidor por sí mismo.

## Estado al navegar

Navigation mantiene una identidad para cada entrada de la pila. El estado guardable de una pantalla puede conservarse mientras su entrada siga presente. Pero al retirar la entrada, no debes esperar que su borrador exista indefinidamente. Abrir Nueva otra vez debe tener un comportamiento definido, en lugar de recuperar accidentalmente datos de otra nota.

Un ViewModel también tiene un propietario y una duración: puede pertenecer a una actividad o a una entrada del grafo. Dos pantallas no comparten automáticamente el mismo ViewModel por utilizar la misma clase. En el proyecto, observa dónde se obtiene la instancia y qué propietario la mantiene. Crear una instancia con `CuadernoViewModel(...)` dentro del cuerpo de una función no equivale a obtenerla mediante la infraestructura `viewModel(...)`.

Al probar navegación, recorre lista → nueva → cancelar, lista → detalle → volver, detalle → guardar y detalle → eliminar. Comprueba también Atrás del sistema y recreación del dispositivo. No basta con abrir cada pantalla una vez desde un botón diferente.
