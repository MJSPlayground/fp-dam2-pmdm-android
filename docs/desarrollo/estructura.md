# Estructura y ejecución de la aplicación

## Una actividad y varias pantallas

En el [primer tema](../ecosistema/proyecto.md) abrimos una Activity que llamaba a una función de pantalla. **Cuaderno** mantendrá una actividad y cambiará el contenido mediante navegación Compose. Una pantalla de la aplicación no tiene por qué corresponder a otra Activity.

El sistema crea la `MainActivity` y llama a `onCreate`. Después de `super.onCreate`, `setContent` define la raíz de la interfaz. Dentro situaremos el tema Material 3 y la función de la aplicación, que conectará las pantallas con el estado. `onCreate` no debe contener una consulta de red bloqueante ni toda la lógica de persistencia.

```text
Android crea MainActivity
          ↓
       onCreate
          ↓
      setContent
          ↓
   Tema → aplicación Compose → pantalla actual
```

El tema se coloca alrededor de las pantallas para que todas puedan consultar sus colores, tipografía y formas. La raíz de navegación decide qué destino mostrar. Los componentes de una pantalla se ocupan de presentar datos y comunicar eventos.

La [guía de actividades](https://developer.android.com/guide/components/activities/intro-activities) explica el componente del sistema; la [introducción de Compose](https://developer.android.com/develop/ui/compose/mental-model) explica por qué las funciones de interfaz pueden volver a ejecutarse.

## Recursos y manifiesto

`res/values/strings.xml` conserva textos; `res/drawable` puede contener vectores e imágenes; `res/mipmap` se utiliza habitualmente para iconos del lanzador. Android genera la clase `R` con referencias a esos recursos. No edites un archivo generado para corregir un nombre: modifica el recurso de origen y vuelve a compilar.

En Compose, `stringResource(R.string.guardar)` obtiene el texto de la configuración activa. Permite que la interfaz cambie de idioma sin sustituir literales en cada función. Para un texto con valores, define un formato en el recurso —por ejemplo `%1$d notas`— y proporciona el valor a `stringResource`; para singular y plural se utilizan recursos `plurals`. No construyas frases traducibles concatenando piezas cuyo orden puede variar entre idiomas.

El manifiesto declara la aplicación, sus Activities, filtros de entrada y permisos. El permiso de red, cuando lleguemos a HTTP, irá directamente dentro de `manifest`, fuera de `application`. Su declaración permite solicitar conexiones; no garantiza que exista red o que el servicio responda.

No añadiremos acceso a contactos, ubicación ni almacenamiento a Cuaderno para guardar texto en una base de datos privada. Los permisos deben corresponder a una función concreta. Consulta la [descripción del manifiesto](https://developer.android.com/guide/topics/manifest/manifest-intro) y la [guía de recursos](https://developer.android.com/guide/topics/resources/providing-resources) para interpretar estos archivos.

## Scripts Gradle: qué cambia y qué permanece

La raíz del proyecto contiene `settings.gradle.kts`, `build.gradle.kts`, el catálogo y el wrapper. El módulo `app` tiene su propio script. Las bibliotecas de la aplicación se declaran en el bloque `dependencies` del módulo; los plugins que transforman código se aplican en `plugins`.

Hay una diferencia importante: una biblioteca como Material 3 ofrece clases que usa nuestra app; un plugin como el compilador de Compose participa al construirla. Añadir un plugin como si fuera una dependencia `implementation` no lo aplica. Tampoco basta con escribir una entrada en el catálogo: hay que referenciarla donde corresponda.

En el catálogo, un alias une nombre y coordenadas. Este es un ejemplo aislado del formato utilizado por el curso:

```toml title="gradle/libs.versions.toml — fragmento ilustrativo"
[versions]
navigation = "2.9.5"

[libraries]
navigation-compose = { module = "androidx.navigation:navigation-compose", version.ref = "navigation" }
```

El módulo puede entonces declarar `implementation(libs.navigation.compose)`, como hace el proyecto. Los guiones del alias se convierten en accesos con puntos. El catálogo concentra versiones, pero no detecta automáticamente todas las incompatibilidades posibles: la compilación y las pruebas siguen siendo necesarias.

La **Compose BOM** alinea las versiones de las bibliotecas Compose que gestiona. No fija por sí sola Activity, Lifecycle, Navigation, Room ni Kotlin. No elimines las versiones de esas bibliotecas porque hayas añadido una BOM. La [guía de catálogo de versiones](https://developer.android.com/build/migrate-to-catalogs) y la [explicación de la BOM](https://developer.android.com/develop/ui/compose/bom) detallan ambos mecanismos.

## Generación de código: serialization y KSP

Al introducir rutas tipadas y JSON utilizaremos el plugin Kotlin Serialization. Genera serializadores a partir de clases anotadas con `@Serializable`. La biblioteca JSON se utiliza además durante la ejecución. Son dos piezas diferentes: plugin de construcción y biblioteca.

Room necesita generar implementaciones a partir de entidades y DAO. Para ello usaremos **KSP**, un procesador de símbolos Kotlin. El módulo aplica el plugin KSP y declara el compilador de Room con `ksp(...)`, además de `room-runtime` para ejecutar la base de datos. Veremos la configuración mínima junto al primer DAO, cuando podamos relacionarla con el código generado.

En proyectos antiguos puedes encontrar `kapt`, que permite procesar anotaciones mediante infraestructura orientada a Java. No añadiremos KAPT y KSP a la vez para Room: el curso fija KSP. Si un mensaje menciona una implementación que Room no pudo generar, busca antes un error en la entidad o el DAO que haya detenido ese procesado.

## Ciclo de vida y cambios de configuración

Una actividad pasa por estados que reflejan si está creada, visible o en primer plano. Android llama a funciones como `onStart`, `onResume`, `onPause`, `onStop` y `onDestroy` durante esas transiciones. No es necesario sobrescribirlas todas; las bibliotecas pueden observar el ciclo de vida para activar y detener trabajo.

Cuando cambian condiciones como la orientación, el sistema puede destruir y recrear la actividad. Una variable ordinaria de la instancia se pierde. El **ViewModel** mantendrá el estado asociado a su propietario durante cambios de configuración, pero tampoco es una base de datos: al finalizar el proceso, no podemos confiar en conservar sus objetos.

En este tema distinguiremos tres mecanismos:

| Necesidad | Mecanismo |
| --- | --- |
| Recordar un valor durante recomposiciones | `remember` |
| Restaurar estado pequeño de UI ante recreación | `rememberSaveable` y estado guardado |
| Conservar notas entre ejecuciones | Room |

El ciclo de vida no es un reloj que garantice una última oportunidad para guardar todo. Guarda una nota como respuesta a una acción de guardar y trata su resultado. No dependas de `onDestroy` para persistir trabajo importante: el proceso puede finalizar sin una llamada final que puedas aprovechar.

## Abrir y ejecutar sin perder el contexto

Abre la raíz de cada proyecto desde Android Studio, espera la sincronización y selecciona **app** y el dispositivo. Si la ventana Build indica un error de script o descarga, corrígelo antes de buscar fallos en una pantalla. Si compila e instala, pero al abrir se cierra, revisa la excepción de Logcat.

Los fragmentos de teoría no son clases completas para reemplazar archivos indiscriminadamente. Cuando se señala «fragmento del ViewModel», se supone dentro de esa responsabilidad. La práctica indicará el archivo concreto, el código preparado y el cambio pendiente. Esta separación evita convertir una explicación pequeña en una reescritura continua del proyecto.
