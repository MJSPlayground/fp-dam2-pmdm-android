# Construir interfaces con Compose y Material 3

## Describir una pantalla

En una interfaz imperativa podrías crear un control y después cambiar sus propiedades cada vez que cambian los datos. Compose utiliza un modelo declarativo: una función describe la interfaz correspondiente al estado actual y el sistema actualiza lo necesario cuando ese estado cambia.

La anotación `@Composable` identifica funciones que participan en esa descripción. Normalmente devuelven `Unit` y llaman a otras funciones componibles. No son componentes de Android que se declaren en el manifiesto ni métodos que debas llamar una sola vez al arrancar.

Esta función es un componente de presentación: recibe datos y dibuja un título y un contenido. El fragmento presupone los imports Compose del proyecto:

```kotlin title="Fragmento de un componente de pantalla"
@Composable
fun ResumenNota(titulo: String, contenido: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = titulo, style = MaterialTheme.typography.titleMedium)
        Text(text = contenido, style = MaterialTheme.typography.bodyMedium)
    }
}
```

Si recibe `Viaje` y `Llevar cargador`, aparecerán dos textos uno debajo del otro. La función no necesita saber si los datos proceden de memoria, de Room o de una respuesta HTTP. Esta separación facilita reutilizarla y probar cómo se presenta con diferentes valores.

Compose puede volver a ejecutar una función al cambiar el estado que consulta. A ese proceso lo llamamos **recomposición**. No hagas peticiones HTTP, guardados ni otras operaciones con efectos dentro del cuerpo de `ResumenNota`: podrían repetirse como parte de actualizaciones de interfaz. Los eventos y la lógica de datos tendrán lugares específicos.

La [explicación del modelo mental de Compose](https://developer.android.com/develop/ui/compose/mental-model) desarrolla esta diferencia con las interfaces imperativas.

## Textos, botones y eventos

`Text` presenta contenido; `Button` ofrece una acción y recibe una función `onClick`. El contenido del botón también se declara con una lambda componible, lo que permite añadir texto y, si hace falta, un icono.

Un componente no tiene por qué decidir qué significa guardar. Puede recibir la acción del nivel que lo utiliza:

```kotlin title="Fragmento de un botón de acción"
@Composable
fun AccionGuardar(disponible: Boolean, onGuardar: () -> Unit) {
    Button(onClick = onGuardar, enabled = disponible) {
        Text(stringResource(R.string.guardar))
    }
}
```

`() -> Unit` es el tipo de una función sin parámetros y sin resultado útil. La llamada `AccionGuardar(true, onGuardar = { ... })` permite al contenedor decidir qué hacer. Pasamos la función, no el resultado de ejecutarla al componer. Escribir `onClick = guardar()` sería incorrecto si `guardar()` devuelve `Unit`: habríamos ejecutado la operación antes del clic.

El estado deshabilitado comunica que no se puede realizar una acción, pero no explica por sí mismo la causa. Si falta un título, acompaña el campo con su indicación; si se está guardando, muestra progreso y evita envíos duplicados. La guía de [botones](https://developer.android.com/develop/ui/compose/components/button) permite consultar variantes sin inventar controles propios.

## Distribuir con Column, Row y Box

`Column` coloca hijos verticalmente; `Row`, horizontalmente; `Box` permite apilar contenido o alinear un hijo dentro de un espacio. Estas funciones reciben restricciones de tamaño de su padre. No eligen tamaños ilimitados ni conocen automáticamente el espacio que deseamos reservar.

Para una fila de título y acción, el título debe utilizar el espacio restante sin empujar el botón fuera. El peso pertenece al contexto de Row:

Este ejemplo utiliza una etiqueta adicional para la acción. Si lo pruebas en el proyecto, añade primero este recurso dentro de `resources` en `res/values/strings.xml`:

```xml title="strings.xml — recurso adicional del ejemplo de fila"
<string name="abrir">Abrir</string>
```

```kotlin title="Fragmento de una fila"
Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(8.dp)
) {
    Text(
        text = titulo,
        modifier = Modifier.weight(1f),
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )
    TextButton(onClick = onAbrir) {
        Text(stringResource(R.string.abrir))
    }
}
```

El título ocupa la parte disponible; el botón conserva el espacio que necesita. `maxLines` limita el resumen visible y la elipsis indica que hay más texto. El detalle deberá permitir leer el contenido completo: truncar no es una solución para toda una aplicación.

En una pantalla vacía, Box puede centrar un mensaje mediante `contentAlignment = Alignment.Center`. No uses una caja enorme para una lista larga: un contenedor no introduce desplazamiento automáticamente. Los [fundamentos de distribución](https://developer.android.com/develop/ui/compose/layouts/basics) explican cómo se relacionan restricciones, tamaño y posición.

## Modifier y el orden de sus operaciones

`Modifier` encadena comportamiento y distribución: relleno, tamaño, fondo, borde, respuesta al clic o semántica. Suele aceptarse como parámetro del componente para que quien lo utiliza pueda situarlo sin editar su interior.

El orden importa porque cada operación envuelve o transforma el resultado anterior. Estos dos casos no colorean la misma región:

```kotlin
Modifier.background(Color.Green).padding(16.dp)
Modifier.padding(16.dp).background(Color.Green)
```

En el primero el fondo incluye el área de relleno interior. En el segundo hay un relleno exterior al fondo. De forma parecida, colocar `clickable` antes o después de un relleno modifica qué región recibe el clic. Para acciones normales prioriza botones Material: incorporan comportamiento y semántica que tendrías que resolver manualmente con una zona clicable.

No encadenes `fillMaxSize` por costumbre en cada hijo: un hijo puede ocupar todo el espacio y dejar sin sitio a otros. Usa `fillMaxWidth` cuando solo necesites anchura y deja que el contenido determine su altura. Consulta [modificadores Compose](https://developer.android.com/develop/ui/compose/modifiers) para ver sus contratos y alcances.

## Formularios con TextField

Un campo de texto controlado recibe el valor actual y una función que notifica el valor propuesto cuando se edita. El componente no puede conservar por sí solo el texto si le pasamos siempre una constante:

```kotlin title="Fragmento de un campo controlado"
OutlinedTextField(
    value = titulo,
    onValueChange = onTituloChange,
    label = { Text(stringResource(R.string.titulo)) },
    modifier = Modifier.fillMaxWidth(),
    singleLine = true,
    isError = mostrarError,
    supportingText = {
        if (mostrarError) Text(stringResource(R.string.validacion))
    }
)
```

Al escribir, `onValueChange` recibe la cadena completa propuesta, no solo el último carácter. El propietario debe actualizar `titulo`; Compose volverá a presentar el campo con ese valor. La etiqueta explica qué se pide incluso cuando el campo contiene texto. El texto de apoyo permite indicar el rango aceptado sin usar únicamente un borde rojo.

En Cuaderno se validará un título de 3 a 80 caracteres, sin contar espacios en los extremos, y contenido no vacío. La interfaz puede anticipar el error, pero la operación de guardar también debe comprobarlo: otros caminos de entrada no deben poder crear una nota inválida.

`singleLine` resulta adecuado para el título; el contenido necesita varias líneas. Los ajustes de teclado y de acción IME ayudan a introducir datos, pero no sustituyen la validación. En ventanas pequeñas, el formulario deberá poder desplazarse y respetar el teclado para no ocultar Guardar. La [documentación de entrada de texto](https://developer.android.com/develop/ui/compose/text/user-input) muestra las APIs disponibles; aquí utilizamos la variante `value`/`onValueChange` de los ejemplos fijados.

## Listas que no dibujan todos los elementos a la vez

Una Column compone todos sus hijos. `LazyColumn` está preparada para listas cuyo tamaño puede crecer: compone y dispone los elementos necesarios para la zona visible, con el trabajo adicional que necesite su implementación.

Cada nota tiene un identificador estable. Usarlo como clave mantiene la identidad de un elemento cuando se insertan o eliminan otras notas:

```kotlin title="Fragmento del listado"
LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp)
) {
    items(notas, key = { it.id }) { nota ->
        Card(onClick = { onAbrirNota(nota.id) }) {
            ResumenNota(
                titulo = nota.titulo,
                contenido = nota.contenido.take(120),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )
        }
    }
}
```

El bloque `items` de colecciones pertenece a `androidx.compose.foundation.lazy.items`; no lo confundas con la variante que recibe una cantidad. Las claves deben ser únicas. Si todavía estás usando notas en memoria, asígnales identificadores distintos en vez de repetir `id = 0` en todos los elementos.

La lista vacía no es un error: presenta un mensaje y la acción para crear la primera nota. En una lista cargada, no cambies los datos al dibujar cada tarjeta. La consulta debe alimentar el estado del listado desde fuera. Consulta [listas Compose](https://developer.android.com/develop/ui/compose/lists) para ampliar claves y conservación de posición.

## Scaffold, barras y mensajes

`Scaffold` organiza áreas comunes de una pantalla: barra superior, contenido, botón flotante o Snackbar. No crea navegación por sí solo. Su lambda de contenido recibe relleno que evita que las barras propias del Scaffold tapen lo que dibujamos.

```kotlin title="Fragmento de estructura de pantalla"
Scaffold(
    topBar = {
        TopAppBar(title = { Text(stringResource(R.string.app_name)) })
    }
) { innerPadding ->
    ListaDeNotas(
        notas = notas,
        onAbrirNota = onAbrirNota,
        modifier = Modifier.padding(innerPadding)
    )
}
```

`ListaDeNotas` es el nombre de un componente ilustrativo que acepta ese modificador. Algunas APIs de Material 3 de la versión fijada pueden requerir un `@OptIn(ExperimentalMaterial3Api::class)` explícito; el proyecto completo incluye el que corresponda. Un aviso de API experimental no se resuelve cambiando todos los imports de Material 3 por Material 2.

Un `Snackbar` ofrece un mensaje transitorio, por ejemplo tras una acción. Un error que impide continuar también necesita una representación persistente y una opción de reintento; no debe desaparecer antes de que se pueda leer. Un diálogo de confirmación sirve cuando una decisión necesita atención, como eliminar una nota. Debe incluir cancelar y no cerrarse dando por completada una operación que ha fallado.

## Tema, imágenes y accesibilidad

Material 3 define roles semánticos como `primary`, `surface`, `onSurface` y `error`, y estilos como `titleLarge` o `bodyMedium`. Una pantalla consulta `MaterialTheme.colorScheme` y `MaterialTheme.typography` en vez de copiar colores y tamaños en cada llamada. Las parejas `surface`/`onSurface` permiten mantener contraste en claro y oscuro.

El tema del proyecto selecciona una paleta según `isSystemInDarkTheme()`. No basta con cambiar el fondo a negro y conservar textos oscuros. Comprueba también campos, diálogos, controles deshabilitados y barras del sistema. La [guía de Material 3 en Compose](https://developer.android.com/develop/ui/compose/designsystems/material3) explica esos roles.

Para una imagen vectorial propia en `res/drawable`, `painterResource(R.drawable.nombre)` proporciona el recurso a `Image`. La descripción depende de su función: una imagen informativa necesita un texto que comunique lo relevante; una decoración que no aporta información puede usar `contentDescription = null` para evitar ruido en un lector de pantalla. Un icono que realiza una acción necesita una etiqueta de acción, como «Volver», aunque su dibujo sea reconocible.

Mantén áreas táctiles de al menos 48 dp y no comuniques un error solo mediante color. Prueba tamaño de fuente aumentado y navegación por teclado cuando corresponda. Consulta los [principios de accesibilidad Compose](https://developer.android.com/develop/ui/compose/accessibility) al revisar componentes propios.

## Preview y ejecución real

`@Preview` muestra una composición dentro del IDE sin iniciar toda la aplicación. Una función Preview sin argumentos puede llamar a `ResumenNota` con valores de ejemplo y envolverlo en el tema. Sirve para comprobar el componente con un título largo, contenido vacío o modo oscuro.

Una Preview no verifica permisos, navegación completa ni peticiones. Puede fallar si intenta obtener una base de datos o un servicio real. Mantén los componentes de presentación separados de esos recursos y proporciona datos preparados a la Preview. Después ejecuta la app para comprobar las interacciones y los espacios del sistema: las dos herramientas aportan evidencias diferentes.
