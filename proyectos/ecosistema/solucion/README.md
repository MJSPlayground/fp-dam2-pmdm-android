# Mi primera aplicación — solución formativa

Abre esta carpeta (solucion) desde **Open** en Android Studio. La raíz contiene
`settings.gradle.kts` y `gradlew.bat`. Espera la sincronización de Gradle e instala
Android SDK Platform 36 cuando lo solicite. Selecciona el JDK de Gradle compatible
con Gradle 8.13 (JDK 17 o JBR 21 del IDE). No actualices automáticamente las versiones
para reproducir esta práctica.

Selecciona un dispositivo virtual o un teléfono con Android 8.0/API 26 o posterior.
Escoge la configuración **app** y pulsa **Run**.

## Archivos que se utilizan

- `app/src/main/java/es/mjsplayground/pmdm/ecosistema/MainActivity.kt`: punto de entrada y pantalla.
- `app/src/main/res/values/strings.xml`: textos visibles.
- `app/src/main/java/es/mjsplayground/pmdm/ecosistema/ui/Theme.kt`: tema Material 3.
- `gradle/libs.versions.toml`: versiones fijadas del proyecto.

## Resultado observable

La pantalla muestra **Mi primera app Android**. Al pulsar **Comprobar**, aparece
**La aplicación responde**. Pulsaciones posteriores mantienen un solo mensaje.
El estado es deliberadamente temporal: al recrear la Activity vuelve al inicio.
La conservación del estado se estudia en el tema de desarrollo con Compose.

El tema sigue la preferencia clara/oscura del sistema. La aplicación no solicita
permisos ni usa red ni almacenamiento. Los márgenes de Scaffold respetan las barras
del sistema.

## Verificación técnica

Desde esta carpeta, en PowerShell con JAVA_HOME y ANDROID_HOME configurados:

```powershell
.\gradlew.bat :app:assembleDebug :app:lintDebug --console=plain
.\gradlew.bat :app:connectedDebugAndroidTest --console=plain
```

La prueba instrumentada comprueba el saludo inicial, el clic y la aparición del mensaje. Necesita un emulador o dispositivo conectado.
Consulta el registro de progreso del repositorio para las pruebas realmente ejecutadas.
El código se distribuye bajo Apache-2.0; véase LICENSE incluido en este proyecto.
Gradle Wrapper se distribuye bajo Apache-2.0, con su aviso de licencia en los scripts.
