# Pruebas de interfaz Compose

## Preguntar por significado

Una prueba de UI encuentra nodos, realiza acciones y comprueba estados visibles. En Compose trabaja con el árbol de **semántica**, que también ofrece información a servicios de accesibilidad. No busca necesariamente cada `Column` o cada píxel dibujado.

La [introducción oficial a pruebas Compose](https://developer.android.com/develop/ui/compose/testing) diferencia reglas, búsquedas y aserciones. El proyecto incluye `ui-test-junit4` alineado con su BOM y el manifiesto de pruebas en Debug. Las clases están en `androidTest`; ejecutarlas requiere un dispositivo conectado o emulador.

En `RevisionUiTest`, `createComposeRule` crea el entorno de composición y la prueba entrega una pantalla sin estado propio:

```kotlin
pantalla.setContent {
    RevisionTheme { RevisionPantalla(RevisionState(), {}) }
}
pantalla.onNodeWithText("No hay notas").assertIsDisplayed()
```

Es el caso inicial resuelto. Una pantalla que recibe estado y eventos puede comprobarse sin montar un servidor, esperar una red real o manipular el estado privado del ViewModel. Otras pruebas comprueban la integración completa cuando eso sea necesario.

## Buscar, actuar, afirmar

`onNodeWithText` identifica por texto; `onNodeWithContentDescription`, por descripción; los matchers permiten roles y otras propiedades. `performClick` realiza la acción semántica. `assertIsDisplayed` exige que el elemento esté visible, no solo que exista en un archivo Kotlin.

Si aparecen varios nodos con el mismo texto, la prueba necesita un criterio más preciso. No añadas descripciones artificiales que empeoren TalkBack únicamente para que el test encuentre un control. Un `testTag` puede ser útil en un componente sin identificación natural, pero no debe reemplazar un nombre accesible que la UI necesita de todos modos.

Los contenedores pueden combinar semántica. El árbol sin combinar sirve para investigar por qué un nodo no se encuentra; no debe ser la primera solución a cualquier fallo. Piensa qué percibe el usuario y qué contrato quieres asegurar.

## Un evento observable

El botón Volver a cargar llama a una función recibida por parámetro. Para comprobar ese evento puedes entregar un contador local y verificar que vale uno después del clic. Eso demuestra que el control comunica la acción; no demuestra que el Repository cargue correctamente. Esa responsabilidad pertenece al test del ViewModel.

`runOnIdle` permite leer una observación cuando la UI está en un punto estable. En trabajos asíncronos utiliza mecanismos de sincronización y condiciones concretas; una espera fija de varios segundos no distingue un resultado correcto de un error lento.

Hay dos relojes diferentes: el reloj de animaciones de Compose y el de corrutinas de prueba. Avanzar uno no ejecuta automáticamente trabajo de otro dispatcher. Las pruebas locales del ViewModel usan tiempo virtual; las instrumentadas del laboratorio entregan estados preparados y no necesitan esperar una red.

## Qué no demuestra una prueba semántica

Encontrar un texto no asegura que su contraste sea suficiente ni que el layout esté libre de recortes en todos los tamaños. Un test que pasa en español no prueba cadenas largas en inglés. Un clic semántico no acredita por sí mismo el tamaño táctil real del control.

Combina pruebas con ejecución visual en ventana compacta, mediana y expandida si la aplicación las soporta; claro/oscuro, fuente grande, teclado y errores. Usa TalkBack para escuchar el orden y el significado de las acciones. Una captura fija ayuda a revisar un estado, pero no sustituye un flujo de navegación ni una animación completa.

En el laboratorio las pruebas instrumentadas cubren vacío, lista/evento y error/reintento visible. La compilación de su APK no equivale a ejecutarlas: registra el resultado del dispositivo y los casos realmente superados.
