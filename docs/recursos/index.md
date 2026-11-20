# Recursos y ayuda

## Configuración reproducible

El entorno del aula es la referencia oficial de todos los proyectos Android de PMDM. Conserva las versiones del proyecto mientras realizas una práctica: una actualización puede exigir otro SDK, plugin o JDK. No aceptes actualizaciones automáticas de Android Studio sin comprobar antes su compatibilidad con esta configuración.

| Componente | Versión del curso | Referencia |
| --- | --- | --- |
| Android Studio | Rabbit 2 · 2026.2.2 Patch 1 · estable · AI-262.10968.63.2622.16840690 | [Compatibilidad del IDE](https://developer.android.com/studio/releases) |
| JDK que ejecuta Gradle | Java 25; JBR del IDE 25.0.4 | [Compatibilidad Gradle/Java](https://docs.gradle.org/current/userguide/compatibility.html) |
| Android Gradle Plugin | 9.2.1 | [Compatibilidad oficial](https://developer.android.com/build/releases/agp-9-2-0-release-notes) |
| Gradle Wrapper | 9.4.1 | Matriz de AGP anterior; no requiere Gradle global |
| Kotlin y plugin Compose | 2.2.21 | [Versiones Kotlin](https://kotlinlang.org/docs/releases.html) |
| Compose BOM | 2025.12.00 | [Publicación de diciembre](https://developer.android.com/blog/posts/whats-new-in-the-jetpack-compose-december-release) |
| Activity Compose | 1.11.0 | [Activity](https://developer.android.com/jetpack/androidx/releases/activity#1.11.0) |
| Lifecycle | 2.9.4 | [Lifecycle](https://developer.android.com/jetpack/androidx/releases/lifecycle#2.9.4) |
| Navigation Compose | 2.9.5 | [Navigation](https://developer.android.com/jetpack/androidx/releases/navigation#2.9.5) |
| Plugin Serialization / JSON | 2.2.21 / 1.9.0 | [Serialization](https://github.com/Kotlin/kotlinx.serialization/releases/tag/v1.9.0) |
| Corrutinas Android | 1.10.2 | [Publicaciones oficiales](https://github.com/Kotlin/kotlinx.coroutines/releases/tag/1.10.2) |
| Room | 2.8.4 | [Room](https://developer.android.com/jetpack/androidx/releases/room#2.8.4) |
| KSP | 2.3.4 | [KSP con soporte AGP 9](https://github.com/google/ksp/releases/tag/2.3.4) |
| Retrofit y conversor Serialization | 3.0.0 | [Publicación oficial](https://github.com/lysine-dev/retrofit/releases/tag/3.0.0) |
| SDK de compilación (`compileSdk`) | 37 (paquete SDK `platforms;android-37.0`) | [Niveles SDK](https://developer.android.com/guide/topics/manifest/uses-sdk-element) |
| SDK objetivo (`targetSdk`) | 36 | Se conserva el comportamiento objetivo de las aplicaciones |
| SDK Build Tools | 36.0.0 | Versión predeterminada de AGP 9.2 |
| Bytecode Java y Kotlin | 17 | Independiente del JDK 25 que ejecuta Gradle |
| Platform Tools / ADB | 37.0.3 | [Herramientas de plataforma](https://developer.android.com/tools/releases/platform-tools) |
| Emulador de referencia | Android Emulator 37.3.10; Medium Phone API 37.0, x86_64, Google Play | [Emulador](https://developer.android.com/studio/run/emulator) |
| ExifInterface | 1.4.2 | [Publicaciones oficiales](https://developer.android.com/jetpack/androidx/releases/exifinterface#1.4.2) |
| Media3 | 1.9.4 | [Publicación elegida](https://developer.android.com/jetpack/androidx/releases/media3#1.9.4) |
| CameraX | 1.5.3 | [Versiones oficiales](https://developer.android.com/jetpack/androidx/releases/camera) |
| Android Image Cropper | 4.7.0 | [Repositorio mantenido](https://github.com/CanHub/Android-Image-Cropper) |
| MapLibre Native Android | 12.2.0 | [SDK y ejemplos oficiales](https://maplibre.org/maplibre-native/android/examples/) |
| Fragment (propietario de BiometricPrompt) | 1.8.9 | [Compatibilidad de la biblioteca](https://developer.android.com/jetpack/androidx/releases/fragment#1.8.9) |
| Biometric | 1.1.0 | [Biblioteca estable](https://developer.android.com/jetpack/androidx/releases/biometric#1.1.0) |
| Coil | 3.3.0 | [Publicación oficial](https://coil-kt.github.io/coil/changelog/#330---july-22-2025) |
| DataStore | 1.2.1 | [Versiones AndroidX](https://developer.android.com/jetpack/androidx/releases/datastore) |
| Hilt | 2.59.2 | [Correcciones para AGP 9](https://github.com/google/dagger/releases/tag/dagger-2.59.2) |
| Ktor | 3.3.3 | [Versiones del proyecto](https://ktor.io/docs/releases.html) |
| WorkManager | 2.10.5 | [Versiones AndroidX](https://developer.android.com/jetpack/androidx/releases/work) |
| Firebase BoM | 34.7.0 | [Combinación elegida](https://firebase.google.com/support/release-notes/android#bom_v34-7-0) |
| MockK | 1.14.6 | [Publicación oficial](https://github.com/mockk/mockk/releases/tag/1.14.6) |
| UiAutomator (pruebas del selector del sistema) | 2.3.0 | [Versiones AndroidX Test](https://developer.android.com/jetpack/androidx/releases/test-uiautomator) |
| Espresso Core (instrumentación Compose) | 3.7.0 | [Compatibilidad de inyección de eventos](https://developer.android.com/jetpack/androidx/releases/test#espresso-3.7.0) |
| Android mínimo de los proyectos | API 26 | Elección del curso |

En **Settings → Build, Execution, Deployment → Build Tools → Gradle**, utiliza el JBR 25 incluido en Android Studio para **Gradle JDK**. En la terminal, `JAVA_HOME` debe señalar un JDK 25 y `ANDROID_HOME` tu SDK local. Son rutas propias de cada ordenador: no las escribas en los archivos compartidos del proyecto ni incluyas `local.properties` en las descargas. El Wrapper descarga Gradle 9.4.1 y verifica su SHA-256.

`gradle/gradle-daemon-jvm.properties` fija Java 25 para el proceso de construcción y utiliza un JDK local; no contiene direcciones para descargar otros JDK. Las pruebas instrumentadas declaran Espresso 3.7.0 explícitamente para evitar que una dependencia transitiva antigua de Compose utilice métodos internos retirados de Android 17.

AGP 9 incorpora el soporte de Kotlin: los proyectos ya no aplican `org.jetbrains.kotlin.android`. El plugin Compose y, donde se utiliza, Serialization fijan Kotlin 2.2.21; KSP 2 genera el código de Room y Hilt. No añadas de nuevo el plugin Kotlin Android ni mezcles KAPT y KSP. Consulta la [migración oficial a Kotlin integrado](https://developer.android.com/build/migrate-to-built-in-kotlin).

Compilar con API 37 no obliga a cambiar `targetSdk`: se mantiene en 36 para evitar introducir cambios de comportamiento ajenos a las prácticas. Java/Kotlin siguen generando bytecode 17 y Android mínimo sigue en API 26. No se necesita Java 17 instalado para ello. Los proyectos introductorios no incorporan Room, Retrofit ni otras bibliotecas de datos.

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
