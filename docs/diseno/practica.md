# Práctica guiada: Galería adaptable

**Compose · Material 3 · Coil.** La galería contiene cuatro láminas, categorías, favoritos y un detalle animado. En ventana compacta usa barra inferior; al ensanchar, menú lateral y varias columnas. Analizarás las piezas nuevas ya resueltas y completarás decisiones de distribución, estado y accesibilidad.

[Proyecto inicial — ZIP](../descargas/proyectos/diseno-inicial.zip){ .md-button .md-button--primary }

???+ example "Preparación y primera observación"

    Abre la carpeta del ZIP con `settings.gradle.kts` en Android Studio, sincroniza y ejecuta **app**. Conserva la configuración de [Recursos](../recursos/index.md). El catálogo fija Coil 3.3.0 y declara opt-in para las APIs visuales que lo requieren en esta combinación.

    **Observa el código existente.** `Datos.kt` define láminas e IDs; `MainActivity.kt` contiene cuadrícula, fila de filtros, drawer, barra inferior, detalle y componente `Foto`. Las ilustraciones locales están en `res/drawable` y la captura remota pertenece a estos materiales. La red solo interviene al cargar esa captura.

    **Comprueba.** Al arrancar se ven tarjetas en una columna. Las categorías filtran, el detalle y Volver están preparados, pero guardar un favorito todavía no lo añade y el número de columnas aún no cambia. El texto de error de una imagen fallida y el estado semántico tienen TODO propios. Actualizar incrementa una revisión después de la demora simulada; no descarga un catálogo nuevo.

??? example "Cuadrícula: observar una fila y resolver la adaptación"

    Lee [distribución](distribucion.md). **Observa** el `LazyRow` de categorías: cada chip cambia `categoria`, y `seleccionar` calcula las láminas visibles. Ya has utilizado estado y filtros; no vuelvas a construir esas piezas.

    **Ejemplo resuelto.** El encabezado de la cuadrícula usa `item(span = { GridItemSpan(maxLineSpan) })` para ocupar una fila completa. Así puede alojar categorías y revisión aunque existan varias columnas. Las tarjetas usan su ID como clave.

    **Ahora te toca.** Completa `columnas` en `Datos.kt`: una columna para Compact, dos para Medium y tres para Expanded. La Activity ya calcula la clase y la pantalla la recibe. No consultes el modelo del teléfono ni añadas píxeles fijos a cada tarjeta.

    ??? tip "Pista: reutilizar un concepto conocido"

        `WindowWidthSizeClass` ofrece valores identificables. Una expresión `when` devuelve directamente el entero que necesita `GridCells.Fixed`; la cuadrícula ya está conectada a esa función.

    **Comprueba.** Prueba ventanas por debajo de 600 dp, entre 600 y 839 y desde 840. Deben verse una, dos y tres columnas, conservando el encabezado completo. La barra inferior aparece en compacta y Menú en las mayores. Cambia tamaño con una categoría seleccionada: la selección no debe perderse ni repetirse el catálogo.

??? example "Favoritos: aplicar estado aprendido con menos ayuda"

    **Observa** `seleccionar`: recibe categoría, destino de favoritos y conjunto de IDs. El catálogo no almacena una segunda colección de objetos favoritos; conserva identidades. La barra y el drawer cambian el mismo destino lógico.

    **Ahora te toca.** Completa `alternar` en `Datos.kt`: si el ID ya estaba, el resultado lo excluye; si no estaba, lo incluye. Devuelve otro conjunto y conserva el recibido. La pantalla ya publica el resultado como estado guardable ante recreaciones de configuración.

    ??? tip "Pista opcional"

        Las operaciones `+` y `-` sobre conjuntos devuelven el resultado de añadir o quitar un elemento. Un conjunto impide que guardar dos veces genere dos copias del ID.

    **Comprueba.** Guarda Bosque, abre Favoritos y verifica que aparece una sola tarjeta. Quítalo: se muestra el mensaje vacío. Regresa a Catálogo: Bosque sigue existiendo. Gira la pantalla y comprueba que los IDs guardados se restauran; terminar el proceso no equivale a persistirlos en disco.

??? example "Coil: caso resuelto y resultado de error"

    Lee [imágenes y caché](imagenes.md). **Observa** `Foto`: `AsyncImage` recibe el modelo, utiliza Crop y notifica éxito. El contenedor mantiene sus dimensiones durante la carga. Se reutiliza el cargador de Coil, sin crearlo en cada tarjeta.

    **Ejemplo resuelto.** El callback `onSuccess` desactiva `cargando` y reinicia `fallo`. Esos valores gobiernan el indicador y el texto, sin modificar la lista de láminas.

    **Ahora te toca.** Completa el TODO `imagen-error`: el callback debe terminar la carga y activar el estado que muestra `error_imagen`. El título debe seguir visible y la tarjeta debe poder abrirse o volver. No transformes un fallo de imagen en una lista vacía de catálogo.

    ??? tip "Pista: estados incompatibles"

        Una petición que ya ha terminado con error no puede seguir anunciando carga. El componente ya contiene la rama que presenta el mensaje; falta activar su condición en el callback apropiado.

    **Comprueba.** Las tres ilustraciones locales deben aparecer sin red. La captura remota requiere HTTPS. Para reproducir un fallo de manera inequívoca, cambia temporalmente **solo en tu copia** su modelo por `"file:///imagen-que-no-existe.png"`: debe terminar la carga y aparecer el aviso. Restablece después la URL. Quitar la red no garantiza un fallo si la imagen ya estaba en caché.

??? example "Animación compartida y estado accesible"

    Lee [animaciones](animaciones.md) y [recursos y accesibilidad](recursos.md). **Observa** `SharedTransitionLayout`, el `AnimatedContent` y las dos imágenes con clave `foto-ID`. La infraestructura está resuelta porque es una API nueva; no debes reconstruir los ámbitos a partir de una captura.

    Abre una lámina y vuelve. La clave relaciona miniatura y detalle; el título permite identificarla incluso sin imagen ni movimiento. El esqueleto de refresco solo se compone durante la demora.

    **Ahora te toca.** En el botón de favorito de `MainActivity.kt`, completa el TODO `semantica`: comunica mediante `stateDescription` el texto `estado` ya calculado. Conserva el nombre accesible de la acción y sus recursos. No añadas el estado únicamente con un cambio de color.

    **Comprueba.** Con TalkBack, localiza la acción de una lámina, escucha su nombre y su estado antes y después de pulsar. Prueba también sin animaciones y con fuente grande. La acción Actualizar ofrece una alternativa al gesto. Si no has usado TalkBack, registra esa comprobación como pendiente.

??? example "Comprobación final"

    | Caso | Resultado esperado |
    | --- | --- |
    | Tres anchos de ventana | 1/2/3 columnas y navegación correspondiente |
    | Filtro Naturaleza y Favoritos | Solo láminas que cumplen ambas condiciones |
    | Abrir detalle y Atrás | Se vuelve al catálogo sin perder favoritos |
    | Actualizar por botón y gesto | Carga acotada y contador incrementado |
    | Modelo de imagen inexistente | Error visible, sin carga permanente |
    | Inglés del sistema | Recursos de interfaz y títulos traducidos |
    | Claro/oscuro y fuente grande | Textos legibles; acciones alcanzables con scroll |
    | TalkBack | Acción e información de estado comprensibles |

    La solución contiene pruebas JVM para columnas y operaciones de selección. Una prueba de datos no certifica las transiciones ni la accesibilidad. Registra dispositivo/API, tamaños e interacciones que hayas probado.

[Solución formativa — ZIP](../descargas/proyectos/diseno-solucion.zip){ .md-button }

[Código y README de la solución](https://github.com/MJSPlayground/fp-dam2-pmdm-android/tree/main/proyectos/diseno/solucion). Se abre en una carpeta distinta; instalarla sustituye la inicial porque comparten identificador.
