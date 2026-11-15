# Animaciones y transiciones

## Relacionar el movimiento con un cambio

Una animación ayuda cuando explica continuidad: una tarjeta se abre, un panel aparece o un elemento cambia de estado. No debe impedir leer, tocar o comprender el resultado si el sistema reduce movimiento. El tema principal utilizó `AnimatedVisibility`; aquí distinguimos propiedades, contenidos y elementos compartidos.

Para un color, tamaño u opacidad con un valor objetivo, las funciones `animate…AsState` conservan una transición entre valores. Para varias propiedades coordinadas, `updateTransition(estado)` permite que compartan el mismo estado de destino. Es distinto de volver a iniciar animaciones manualmente en cada recomposición. La [guía de animación por valor](https://developer.android.com/develop/ui/compose/animation/value-based) desarrolla sus contratos.

Un ejemplo ilustrativo de propiedad, dentro de una tarjeta con `seleccionada: Boolean`, sería:

```kotlin
val relleno by animateDpAsState(
    targetValue = if (seleccionada) 20.dp else 12.dp,
    label = "relleno"
)
Column(Modifier.padding(relleno)) { contenido() }
```

La variable observada es el estado animado y `contenido` representa el slot ya disponible. No modifica el modelo de negocio ni añade otra tarjeta. Cambiar padding puede mover contenido próximo; elige propiedades cuyo efecto visual aporte orientación.

## Sustituir contenido completo

`AnimatedContent(targetState = elegido)` conserva el contenido entrante y saliente durante la transición. En el laboratorio, `null` representa la galería y un ID representa un detalle. Usa el parámetro que recibe la lambda para dibujar cada rama: durante la transición no todo debe leer exclusivamente el valor exterior más reciente.

`BackHandler` devuelve del detalle y el botón Volver ofrece la misma acción visible. La animación no sustituye la pila de navegación de una aplicación completa. Aquí son dos estados locales acotados; en una app con varios flujos seguirías utilizando los destinos de navegación ya aprendidos.

## Compartir una imagen entre dos representaciones

La miniatura y la imagen de detalle representan la misma lámina con distinto tamaño. `SharedTransitionLayout` proporciona el ámbito donde se relacionan. Ambas imágenes utilizan una `SharedContentState` con la **misma clave** y reciben el ámbito de visibilidad de `AnimatedContent`.

Fragmento de uno de los dos extremos, dentro de esos ámbitos ya creados:

```kotlin
Modifier.sharedElement(
    rememberSharedContentState("foto-${lamina.id}"),
    animatedVisibilityScope
)
```

La clave une una miniatura con su detalle. Usar una constante para todas las imágenes confundiría identidades. El proyecto repite esa expresión en ambos extremos; `animatedVisibilityScope` corresponde a la rama animada, no al scope de corrutinas.

La combinación fijada declara `ExperimentalSharedTransitionApi`; puede exigir adaptación al actualizar Compose. El código del proyecto compila con su BOM, y la [guía oficial de elementos compartidos](https://developer.android.com/develop/ui/compose/animation/shared-elements) permite contrastar el contrato actual. No copies ejemplos de una versión distinta suponiendo que sus parámetros son idénticos.

## Movimiento repetitivo y recursos

El esqueleto del laboratorio usa `rememberInfiniteTransition`, `animateFloat` e `infiniteRepeatable` para oscilar la opacidad. Se compone únicamente mientras hay refresco. Al desaparecer ese elemento, su animación deja de pertenecer a la composición. No se crea una corrutina global ni un hilo para mantenerlo vivo.

Un skeleton puede representar dónde llegará el contenido; un shimmer añade una banda de luz que recorre esos bloques. No son técnicas de descarga ni justifican ocultar un fallo. La implementación utiliza un pulso sencillo y admite una alternativa estática; añadir un degradado móvil sería otra variante del mismo estado de carga.

Prueba entrada/salida del detalle, filtro durante carga, Atrás repetido, fuente grande y animaciones desactivadas en el dispositivo. Comprueba el resultado final y el destino del foco: una captura fija no demuestra que una transición continúe correctamente ni que el lector de pantalla permanezca en un lugar útil.
