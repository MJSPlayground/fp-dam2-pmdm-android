# Práctica guiada: Buscador con flujos

**Kotlin · Flow · ViewModel · Compose.** Construiremos un buscador local de libros que combina texto y favoritos, cancela búsquedas sustituidas y se recupera de fallos. Los controles de interfaz están preparados para concentrarnos en colecciones y asincronía: en el tema principal ya has construido formularios y listas con Compose.

Analizarás una extensión y dos efectos resueltos, completarás un filtro, conectarás flujos y finalmente decidirás las claves de un efecto. Cada etapa termina con resultados visibles. El catálogo y la demora son locales; no se consulta ningún servicio externo.

[Proyecto inicial — ZIP](../descargas/proyectos/asincronia-inicial.zip){ .md-button .md-button--primary }

???+ example "Preparación y lectura del código existente"

    Abre en Android Studio la carpeta que contiene `settings.gradle.kts`, espera la sincronización y ejecuta **app** en un dispositivo API 26 o posterior. Conserva las versiones del catálogo y el Gradle JDK compatible de [Recursos](../recursos/index.md).

    **Observa el código existente.** Dentro del paquete `es.mjsplayground.pmdm.asincronia.buscador` encontrarás:

    | Archivo | Ya preparado | Lo que completarás |
    | --- | --- | --- |
    | `Catalogo.kt` | Modelo, cinco libros, normalización y estado de resultado | `filtrar` y `flujoBusqueda` |
    | `BuscadorViewModel.kt` | Entrada de eventos, `StateFlow` y `stateIn` | Nada: sigue las llamadas |
    | `MainActivity.kt` | Campo, lista, favoritos, snackbar y observador de ciclo de vida | Efecto de consulta estable |
    | `ui/Theme.kt` | Tema claro/oscuro | Nada |

    Localiza `cambiarConsulta`: actualiza `_consulta`. La pantalla recoge ese valor, pero el inicial todavía ignora el texto al calcular libros. Sigue `resultado` hasta `flujoBusqueda`: ahí trabajarás, sin añadir un segundo filtro dentro de la UI.

    **Comprueba.** Debes ver cinco libros. Escribir `kotlin` y activar favoritos todavía no cambia esa lista. El botón Ayuda sí muestra un snackbar. El texto de espera no cambia al cumplirse un segundo: corresponde al último TODO. Si el inicial no arranca, resuelve la sincronización antes de tocar la lógica.

??? example "Transformar colecciones: ejemplo y primera implementación"

    Lee [colecciones](colecciones.md). **Observa** `String.normalizada()` y prueba mentalmente las entradas `" Kotlin "` y `"KOTLIN"`: ambas producen `"kotlin"`. Conserva esa operación ya implementada.

    **Ejemplo resuelto.** Para devolver los títulos de los libros favoritos se puede combinar selección y transformación:

    ```kotlin
    val titulosFavoritos = catalogoEjemplo
        .filter { it.favorito }
        .map { it.titulo }
    ```

    El resultado son dos títulos y su tipo es `List<String>`. Nuestra función `filtrar` debe devolver libros completos, por lo que no debe aplicar ese `map` final.

    **Ahora te toca.** Completa únicamente `filtrar` en `Catalogo.kt`: el título debe contener la consulta normalizada; si `soloFavoritos` es verdadero, admite solo favoritos; ordena por título normalizado. Una consulta vacía admite cualquier título. Conserva la lista de entrada y los IDs. La firma y la extensión están preparadas.

    ??? tip "Pista: combinar condiciones"

        Necesitas dos decisiones booleanas sobre cada libro. Cuando no se solicitan solo favoritos, el valor `libro.favorito` no debe excluirlo. La ordenación produce otra lista; no requiere convertir el catálogo en mutable.

    **Comprueba esta etapa antes de Flow.** Coloca temporalmente en el cuerpo preparado de `flujoBusqueda` `ResultadoBusqueda(libros = filtrar(libros, " KOTLIN ", true))`. Al ejecutar debe aparecer únicamente **Kotlin en práctica**. Con consulta vacía y `false` deben aparecer cinco libros ordenados; con `"inexistente"`, el mensaje vacío. Esta sustitución es una sonda provisional: se retira al completar la siguiente etapa.

??? example "Combinar filtros y gestionar cada consulta"

    Lee [corrutinas](corrutinas.md) y [flujos](flujos.md). **Observa** que `BuscadorViewModel` ya recibe los eventos, comparte el resultado y lo mantiene fuera del cuerpo composable. No vuelvas a escribir esa clase.

    **Ejemplo resuelto.** Si `textos` es un flujo de cadenas y `favoritos` uno de booleanos, este fragmento entrega el último par de criterios cada vez que cambia cualquiera:

    ```kotlin
    combine(textos, favoritos) { texto, solo -> texto to solo }
    ```

    Es una referencia del operador, no el cuerpo completo de la búsqueda. Construye `textos` a partir de `consultas`, normalizando, esperando `esperaEscritura` con `debounce` y descartando repeticiones con `distinctUntilChanged`.

    **Ahora te toca.** Sustituye el cuerpo provisional de `flujoBusqueda` por la tubería de criterios y una transformación cancelable. Los imports y opt-in necesarios ya están incluidos. Utiliza los parámetros de espera que recibe la función, sin fijarlos dentro del cuerpo. Por cada criterio aceptado:

    - Emite un `ResultadoBusqueda` con carga activa.
    - Suspende durante `latencia` con `delay`.
    - Si el texto normalizado es `!error`, lanza `IllegalArgumentException` para simular el fallo.
    - Si es válido, emite los libros de `filtrar`, sin carga ni error.
    - Representa ese fallo simulado como estado de error. Conserva la cancelación y la observación de consultas futuras.

    `ResultadoBusqueda` tiene valores por defecto: cada nueva instancia permite salir de carga sin mantener indicadores incompatibles. No declares otra lista compartida ni uses `Thread.sleep`.

    ??? tip "Pista: más de una emisión por consulta"

        `mapLatest` devuelve un único resultado. Para emitir carga y después el resultado necesitas la variante `transformLatest`, que ofrece `emit` dentro del bloque. Cada nueva entrada cancela el bloque anterior cuando coopera con la cancelación.

    ??? tip "Pista: alcance del error"

        El `try/catch` debe pertenecer a la consulta concreta, dentro de la transformación. Capturar y relanzar `CancellationException` permite que la sustitución no se convierta en un falso error. Un `catch` terminal no vuelve a iniciar automáticamente el flujo terminado.

    **Comprueba.** Escribe `kotlin` y espera: aparecen dos libros. Activa favoritos sin modificar el texto: queda **Kotlin en práctica**. Escribe `!error`: aparece el error, sin carga permanente. Cámbialo por `compose` y desactiva favoritos: debe aparecer **Android con Compose**. Escribe `k`, espera a ver carga y después `rutas`; tras completar la segunda consulta aparecen los dos títulos de rutas, sin un error de cancelación.

    La pausa de escritura precede a la cancelación. No esperes invalidación instantánea por cada tecla: el comportamiento exacto se explica en [sustituir trabajo](flujos.md#sustituir-el-trabajo-anterior).

??? example "Efectos ya resueltos y retirada de ayuda"

    Lee [efectos](efectos.md). **Observa el código existente** del botón Ayuda: `rememberCoroutineScope` proporciona un ámbito de pantalla y el clic lanza el snackbar. No hay una corrutina nueva por cada dibujo.

    Después sigue `DisposableEffect(propietario)`: registra un `LifecycleEventObserver` y retira esa misma instancia en `onDispose`. El contador de retornos sirve para observar eventos; no es un contador de recomposiciones y puede incluir la entrada inicial.

    **Ahora te toca.** Completa el TODO `efecto` de `MainActivity.kt`. Tras cambiar el campo, el mensaje debe indicar espera; al transcurrir un segundo sin cambios debe indicar consulta estable. Cada nuevo cambio reinicia la espera. Utiliza `estable`, ya declarado, sin tocar el filtro del ViewModel ni lanzar otra búsqueda.

    ??? tip "Pista: identidad de la tarea"

        ¿Qué valor hace que una espera anterior deje de servir? Esa es la clave del efecto. Una constante no detectaría cambios del campo; utilizar el propio resultado del temporizador produciría reinicios innecesarios.

    **Comprueba.** Escribe varias letras separadas por menos de un segundo: el mensaje estable no debe aparecer entre ellas. Deja de escribir y comprueba que cambia. Abre otra aplicación y vuelve: el contador de entradas cambia sin duplicar filas. El mensaje local y la consulta del ViewModel tienen duraciones distintas y no deben modificar mutuamente sus tareas.

??? example "Comprobación final y pruebas reproducibles"

    Repite la siguiente tabla con tu implementación; registra dispositivo/API y resultado. No sustituyas una prueba de ejecución por una lectura del código.

    | Acción | Resultado observable |
    | --- | --- |
    | Buscar ` KOTLIN ` | Dos libros, ignorando espacios exteriores y mayúsculas |
    | Activar favoritos con esa consulta | Un libro sin tener que volver a escribir |
    | Buscar un título inexistente | Mensaje vacío después de carga |
    | Buscar `!error` y después `compose`, sin favoritos | Error recuperable, luego un libro |
    | Cambiar la consulta durante carga | Termina con el criterio más reciente aceptado |
    | Abrir Ayuda | Snackbar; lista y filtros permanecen |
    | Girar la pantalla | ViewModel conserva filtros; estado local puede reiniciarse |
    | Modo oscuro y fuente grande | Textos legibles y acciones alcanzables mediante scroll |

    La solución incorpora `CatalogoTest`. En Android Studio abre la clase dentro de **test** y utiliza su triángulo Run para ejecutarla. El reloj virtual comprueba pausas y sustitución sin esperar demoras reales. No copies sus clases de respuesta a tu proyecto inicial para «pasar» la práctica: compara los contratos de la tabla con tu comportamiento.

    Las pruebas locales de Flow no demuestran TalkBack, teclado, aspecto visual ni comportamiento en un dispositivo físico. Esa parte se comprueba ejecutando la aplicación.

[Solución formativa — ZIP](../descargas/proyectos/asincronia-solucion.zip){ .md-button }

[Ver código y README de la solución](https://github.com/MJSPlayground/fp-dam2-pmdm-android/tree/main/proyectos/asincronia/solucion). Ábrela en otra carpeta: usa el mismo identificador de aplicación y sustituye a la inicial al instalarla.
