# Recursos y ayuda

## Configuración reproducible

Los proyectos fijan una combinación estable. No se pretende utilizar la última versión de cada biblioteca: una actualización puede exigir otro SDK, plugin o JDK. Conserva las versiones del proyecto mientras realizas una práctica y revisa los cambios en una copia separada.

| Componente | Versión del curso | Referencia |
| --- | --- | --- |
| Android Gradle Plugin | 8.13.2 | [Compatibilidad oficial](https://developer.android.com/build/releases/agp-8-13-0-release-notes) |
| Gradle Wrapper | 8.13 | Matriz de AGP anterior |
| Kotlin y plugin Compose | 2.2.21 | [Versiones Kotlin](https://kotlinlang.org/docs/releases.html) |
| Compose BOM | 2025.12.00 | [Publicación de diciembre](https://developer.android.com/blog/posts/whats-new-in-the-jetpack-compose-december-release) |
| Activity Compose | 1.11.0 | [Activity](https://developer.android.com/jetpack/androidx/releases/activity#1.11.0) |
| Lifecycle | 2.9.4 | [Lifecycle](https://developer.android.com/jetpack/androidx/releases/lifecycle#2.9.4) |
| Navigation Compose | 2.9.5 | [Navigation](https://developer.android.com/jetpack/androidx/releases/navigation#2.9.5) |
| Plugin Serialization / JSON | 2.2.21 / 1.9.0 | [Serialization](https://github.com/Kotlin/kotlinx.serialization/releases/tag/v1.9.0) |
| Corrutinas Android | 1.10.2 | [Publicaciones oficiales](https://github.com/Kotlin/kotlinx.coroutines/releases/tag/1.10.2) |
| Room | 2.8.4 | [Room](https://developer.android.com/jetpack/androidx/releases/room#2.8.4) |
| KSP | 2.2.21-2.0.4 | [Compatibilidad con Kotlin fijado](https://github.com/google/ksp/releases/tag/2.2.21-2.0.4) |
| Retrofit y conversor Serialization | 3.0.0 | [Publicación oficial](https://github.com/lysine-dev/retrofit/releases/tag/3.0.0) |
| SDK de compilación y objetivo | 36 | [Niveles SDK](https://developer.android.com/guide/topics/manifest/uses-sdk-element) |
| Android mínimo de los proyectos | API 26 | Elección del curso |

AGP 8.13 requiere Gradle 8.13 y como mínimo JDK 17. En Android Studio selecciona un Gradle JDK compatible —por ejemplo el JBR incluido en una versión compatible del IDE— y conserva el objetivo de bytecode del proyecto. Los proyectos introductorios no necesitan Room, Retrofit ni otras bibliotecas de datos.

La compilación local se comprueba con Android Studio instalado 2025.3.1 y su JBR. Puedes utilizar una versión estable compatible más reciente: al abrir la práctica, evita actualizar sus plugins automáticamente. La [página oficial de instalación](https://developer.android.com/studio/install) indica la versión estable y los requisitos actuales del IDE.

Los puntos de control incorporan solo las dependencias necesarias para su etapa. Por ejemplo, la pantalla inicial no usa Room ni Retrofit; la etapa de red conserva datos en memoria y la solución añade Room. Los catálogos se incluyen en cada proyecto descargable para que pueda abrirse de forma independiente. Conserva las versiones comunes al avanzar entre etapas.

## Consultar la documentación

- [Kotlin: sintaxis básica](https://kotlinlang.org/docs/basic-syntax.html): referencia del lenguaje; utiliza las comparaciones con Java del itinerario para aprenderlo progresivamente.
- [Jetpack Compose](https://developer.android.com/develop/ui/compose/documentation): estado, componentes y diseño de pantallas.
- [Android Studio](https://developer.android.com/studio/intro): herramientas del IDE.
- [Guías de arquitectura](https://developer.android.com/topic/architecture): responsabilidades de las capas; no es necesario adoptar todos los patrones para una app pequeña.
- [Material 3](https://m3.material.io/): comportamiento y diseño de componentes.

## Diagnosticar sin cambiarlo todo

1. Reproduce el problema con una acción concreta: abrir, sincronizar, compilar, ejecutar o pulsar un control.
2. Copia el primer error relevante de **Build** o la excepción de **Logcat**, con su causa. No te quedes con «la aplicación no funciona».
3. Comprueba la ruta y el archivo señalados. Un recurso mal cerrado puede causar muchos errores posteriores.
4. Revisa versiones y dispositivo frente a la tabla del curso. Cambia una sola causa y repite la acción.
5. Si necesitas ayuda, indica versiones, resultado esperado, resultado obtenido y pasos para reproducirlo. Elimina tokens, direcciones privadas y datos personales del registro compartido.

No borres cachés ni reinstales el IDE como primer paso: perderías información útil y podrías ocultar una causa que volverá a aparecer.
