# Tecnologías móviles y compatibilidad

## Elegir cómo construir una aplicación

Una aplicación móvil recibe eventos —pulsaciones, cambios de ubicación, una respuesta de red— y actualiza lo que ve la persona. A diferencia de un programa de escritorio, no puede dar por hecho que seguirá abierta, que tiene conexión o que dispone de memoria suficiente. El sistema comparte los recursos entre aplicaciones y puede detener procesos para recuperarlos.

**Nativa** significa que se construye para las APIs y convenciones de una plataforma. En Android utilizaremos Kotlin y Jetpack Compose; en iOS es habitual Swift con SwiftUI. La interfaz se adapta bien a los controles y servicios del sistema, pero mantener versiones para ambas plataformas implica trabajo específico. Java sigue siendo utilizable en Android; Kotlin permite expresar con menos código propiedades, valores ausentes y operaciones asíncronas.

**Multiplataforma** describe herramientas que comparten una parte del programa entre sistemas. Flutter utiliza Dart y su propio sistema de widgets; React Native utiliza JavaScript o TypeScript y componentes integrados con las plataformas; Kotlin Multiplatform permite compartir lógica Kotlin y, cuando se elige Compose Multiplatform, también interfaz. Compartir código no elimina las diferencias de permisos, publicación, accesibilidad ni hardware: hay que probar cada plataforma.

Una aplicación **híbrida** presenta una interfaz web dentro de un contenedor nativo, normalmente con una WebView y puentes para acceder a funciones del dispositivo. Puede aprovechar conocimientos web, aunque la integración de funciones específicas y la experiencia de navegación requieren cuidado. Una web adaptable o una PWA puede resolver servicios sencillos sin distribuir una app por una tienda; las capacidades disponibles dependen del navegador y el sistema.

| Necesidad | Opción razonable | Coste que hay que valorar |
| --- | --- | --- |
| App Android con cámara, permisos e integración profunda | Kotlin y APIs Android | Implementación específica para Android |
| Producto para Android e iOS con equipo Flutter | Flutter | Aprender Dart y resolver integraciones de cada sistema |
| Equipo con experiencia React y dos plataformas | React Native | Dependencias y módulos específicos de plataforma |
| Compartir reglas de negocio conservando UI nativa | Kotlin Multiplatform | Separar el código común de las APIs de cada plataforma |
| Consulta de información y formularios accesibles por enlace | Web adaptable | Límites de navegador, conexión e integración |

No existe una tecnología ganadora para cualquier aplicación. Para aprender el ciclo de vida, los permisos y las APIs del dispositivo, trabajaremos directamente con Android. Las alternativas se comparan aquí; no necesitas programarlas para continuar.

Puedes contrastar sus modelos en la documentación de [Flutter](https://docs.flutter.dev/resources/architectural-overview), [React Native](https://reactnative.dev/docs/intro-react-native-components) y [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform/get-started.html).

## El dispositivo impone límites

Una fotografía de 4000 × 3000 píxeles puede ocupar aproximadamente 48 MB al decodificarse a cuatro bytes por píxel, aunque el JPEG guardado ocupe mucho menos. El almacenamiento del archivo y la memoria necesaria para procesarlo son magnitudes distintas. Cargar varias imágenes sin reducir su resolución puede agotar la memoria.

La CPU y la GPU compiten por tiempo y energía. Si una operación de lectura, cálculo o red bloquea el hilo que dibuja la interfaz, el usuario observa pulsaciones que no responden. Más adelante aprenderemos a suspender operaciones y a mover trabajo pesado fuera de ese hilo.

La conexión cambia: una respuesta puede tardar, fallar o llegar después de abandonar la pantalla. Tampoco todos los dispositivos tienen los mismos sensores ni la misma precisión. Una aplicación debe explicar qué ocurre cuando falta una capacidad y ofrecer una salida razonable.

Las pantallas varían en tamaño, densidad, orientación y espacio disponible. Una tableta en pantalla dividida puede dejar menos anchura a una aplicación que un teléfono. Por eso Android utiliza unidades independientes de la densidad y Compose distribuye componentes según restricciones; no colocaremos elementos con coordenadas fijas de píxeles.

## Versión de Android y nivel de API

El nivel de API identifica el conjunto de APIs de una versión del sistema. No es la versión de Kotlin, de Android Studio ni de nuestra aplicación. Una actualización del IDE tampoco actualiza el sistema de un teléfono.

En el script del módulo se declaran tres decisiones diferentes. Este fragmento es de configuración, todavía no código de la pantalla:

```kotlin title="app/build.gradle.kts — fragmento"
android {
    compileSdk = 36
    defaultConfig {
        minSdk = 26
        targetSdk = 36
    }
}
```

`compileSdk` determina las APIs que el compilador conoce. `minSdk` fija el sistema mínimo donde se podrá instalar la aplicación. `targetSdk` declara para qué nivel se ha preparado y probado su comportamiento: Android aplica determinados cambios de compatibilidad según ese valor. **No es un máximo de instalación.**

En el ejemplo, un dispositivo API 25 queda fuera; uno API 26 puede instalar la app. Pero compilar contra API 36 no hace que una API introducida en 33 exista en el dispositivo 26: habrá que comprobar la versión antes de usarla o utilizar una API de compatibilidad apropiada. Estudiaremos estos casos al introducir permisos y notificaciones.

Consulta la [referencia de niveles SDK](https://developer.android.com/guide/topics/manifest/uses-sdk-element). En proyectos modernos, estos valores se configuran en Gradle; no los dupliques manualmente en el manifiesto.

## Perfiles de prueba

Un **emulador** reproduce un dispositivo Android virtual en el ordenador. Su perfil define características como tamaño de pantalla; su imagen de sistema define la versión de Android. Permite repetir pruebas, rotar la pantalla y simular algunas condiciones. Su velocidad depende del equipo y de la aceleración disponible.

Un **dispositivo real** permite observar hardware, consumo, cámara, radios, permisos del fabricante y comportamiento táctil reales. La simulación de una ubicación demuestra cómo responde la aplicación a coordenadas conocidas, pero no verifica la recepción GNSS de un teléfono. Necesitaremos ambos tipos de prueba.

Al empezar, utiliza un teléfono virtual con API 36. Al incorporar compatibilidad, prueba también el nivel mínimo soportado y una ventana de mayor tamaño. Registra el perfil y la versión de cada comprobación: «funciona en mi emulador» no indica qué se ha probado.
