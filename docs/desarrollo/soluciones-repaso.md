# Soluciones del repaso

## Kotlin

1. Produce `Hola, visitante`, `Hola, ` y `Hola, Ana`. Elvis sustituye `null`, no cadenas vacías ni espacios.
2. `map` devuelve `List<Boolean>` con `[false, true]`. `filter` devuelve `List<Tarea>` con la tarea Leer. Transformar no es seleccionar.
3. Sí. `val` impide reasignar esa variable; no convierte automáticamente el objeto en inmutable. Una `MutableList` permite cambiar elementos y tamaño.
4. `suspend` permite suspender una corrutina. No crea por sí solo un hilo ni convierte cálculos bloqueantes en trabajo de fondo. Hay que utilizar las APIs o contextos adecuados para cada operación y respetar la cancelación.

## Estado

5. La variable local se inicializa en cada ejecución y su cambio no es observable por Compose. Para un contador temporal, una solución es `var pulsaciones by remember { mutableStateOf(0) }` con los imports correspondientes. Si debe restaurarse tras recreación, hay que elegir una política de estado como `rememberSaveable`; si es dato de negocio persistente, necesita almacenamiento. No son soluciones intercambiables para cualquier caso.
6. El texto recordado con `remember` puede perderse al recrear. Los registros guardados en Room permanecen tras cerrar y abrir. Una preferencia no almacenada no se puede dar por conservada. `ViewModel` ayuda con cambios de configuración, pero no equivale a una base de datos.
7. La pantalla recibe un valor y comunica eventos, sin mantener otra copia que pueda contradecirlo. El propietario del estado decide y entrega el valor actual. Puede ser el composable padre o el ViewModel según su responsabilidad.
8. Los modificadores se aplican en orden. Si el fondo se aplica antes del relleno interior puede cubrirlo; aplicado después, puede cubrir un área menor. Comprueba el resultado con colores visibles y evita deducirlo únicamente por el orden de los nombres.
9. Una clave basada en el identificador permite asociar la identidad de una fila a su nota al cambiar posiciones. El índice cambia al insertar, ordenar o borrar y puede asociar estado al elemento equivocado.

## Navegación y arquitectura

10. El identificador es pequeño y estable; destino consulta la versión actual de los datos. Pasar una copia completa puede quedar desactualizado. Si ya no existe, se muestra un estado comprensible y una forma de volver, sin provocar una excepción.
11. Tras guardar, retirar el formulario de la pila y volver a la lista evita encontrarlo como si siguiera pendiente. La operación de almacenamiento y la navegación deben coordinarse para no cerrar antes de saber si guardar tuvo éxito.
12. La UI comunica el evento; el ViewModel valida y coordina; el repositorio llama al DAO; Room modifica SQLite; la consulta observable emite el nuevo resultado; el estado llega a Compose y se dibuja. La validación de dominio no debe depender exclusivamente de habilitar o deshabilitar un botón.
13. Una recomposición puede repetir llamadas o efectos y acopla la interfaz a almacenamiento. Las escrituras responden a eventos a través de ViewModel/repositorio. La UI observa estado; las operaciones no se lanzan por el mero hecho de dibujar.
14. La recolección considera el ciclo de vida activo de la pantalla. Reduce trabajo innecesario y presenta el flujo como estado de Compose, pero no almacena ese estado entre reinicios del proceso.

## Datos

15. El permiso se revisa en el manifiesto; un 404 en el recurso/URL y el código HTTP; una conexión fallida en dirección, red, servidor y excepción de E/S. Logcat y el registro HTTP del servidor ayudan a separar causas. El usuario necesita saber qué puede hacer, por ejemplo revisar conexión o reintentar; no necesita una traza de implementación.
16. No. El contrato de conversión falló. Debe diferenciarse de una respuesta válida que contiene `[]`; confundir ambos oculta errores del servicio o del modelo.
17. No: la importación crea una copia en la base local. Una subida necesitaría una operación de red explícita, su endpoint y tratamiento de resultados. Room no sincroniza automáticamente con una API.
18. Room detecta los cambios que afectan a la consulta y vuelve a emitir su resultado. El repositorio y el ViewModel lo exponen a la interfaz. Mantener otra lista manual con sus propias mutaciones puede introducir dos fuentes de verdad.
19. El identificador puede no existir porque se eliminó o porque se guardó otro ID. Comprobar la nota por su identificador y el número de filas afectadas distingue el caso de un fallo de ejecución SQL.
20. El esquema describe tablas y columnas. Una migración lleva los datos existentes al esquema nuevo. Borrar la base puede ser útil en un experimento desechable controlado, pero no es una reparación aceptable de datos del usuario. La arquitectura y las migraciones avanzadas se desarrollan en la ampliación correspondiente.

## Integración

21. El explícito identifica un componente; el implícito declara una acción y datos que otras aplicaciones pueden resolver. Sharesheet ofrece destinos compatibles. Compartir debe gestionar que una acción no pueda resolverse y utilizar permisos de URI cuando se comparten archivos, tema que se retoma en multimedia.
22. No. `10.0.2.2` es una ruta virtual hacia el host del emulador. Una prueba Wi-Fi real necesita Android conectado por Wi-Fi y un servicio en otro ordenador de esa LAN, con petición y respuesta observables. Registra red y dispositivos sin publicar direcciones sensibles. Sin teléfono, deja esa evidencia pendiente y conserva solo la prueba de emulación realizada.
23. No. Falta comprobar el flujo con teclado y espacio reducido, además de accesibilidad. Scroll, insets y espacio de acciones se verifican en una ejecución real.
24. Registra abrir nota → editar sin guardar → girar → observar texto. Revisa el propietario del estado del formulario y su restauración; el contenido aún no guardado no debe buscarse en Room como primera causa.

## Función de filtrado

Este es un archivo Kotlin completo sin dependencias Android. `filter` conserva los elementos cuyo predicado es verdadero; `contains` permite ignorar mayúsculas. No se modifica la lista recibida.

```kotlin title="FiltroTitulos.kt"
package es.mjsplayground.pmdm.desarrollo.cuaderno.ejercicios

fun filtrarTitulos(titulos: List<String>, consulta: String): List<String> {
    val texto = consulta.trim()
    return titulos.filter { it.contains(texto, ignoreCase = true) }
}
```

Con `" LEER "` devuelve `Leer Kotlin` y `leer Android`. Con consulta vacía conserva todos porque cualquier cadena contiene la cadena vacía. Con `"red"` devuelve una lista vacía. El proyecto inicial incluye `FiltroTitulos.kt` con el TODO y `FiltroTitulosTest` inicialmente omitido. Retira `@Ignore` al completarlo y ejecuta la clase desde el triángulo. La solución contiene ambos archivos resueltos y las pruebas activas.

La solución ejecutable de las cuatro funciones de Kotlin y sus pruebas está en [el ZIP de solución de Cuaderno](../descargas/proyectos/desarrollo-solucion.zip), dentro del paquete `ejercicios`. La comprobación integrada se contrasta con el comportamiento del mismo proyecto solución y sus instrucciones; no sustituye la prueba física pendiente.
