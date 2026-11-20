# Lista de revisión — solucion

Abre esta carpeta desde Open en Android Studio, sincroniza Gradle y ejecuta app en
API 26 o posterior, con SDK 37 y el Gradle JDK de Recursos. No requiere red ni cuentas.
La aplicación preparada carga dos notas locales mediante DemoRepository y ViewModel.

RevisionTest prueba validación, Repository con tiempo virtual, carga/error/reintento/sustitución del ViewModel con MockK. RevisionUiTest prueba estados y un evento Compose. Versiones.kt/VersionesTest resuelven la actividad autónoma separada del inicial.

La variante Debug añade .debug al applicationId y usa firma de depuración. Release
activa R8 y reducción de recursos, pero no configura firma privada. Una compilación
Release aquí es unsigned y no se publica. No incluyas keystores ni contraseñas.
Crashlytics se explica como integración externa opcional: no se instala ni envía
informes desde este laboratorio. No se ha creado ningún proyecto Firebase/Play.

Práctica: https://mjsplayground.github.io/fp-dam2-pmdm-android/calidad/practica/

## Comprobación técnica opcional

```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:lintDebug :app:assembleDebugAndroidTest --console=plain
.\gradlew.bat :app:connectedDebugAndroidTest --console=plain
.\gradlew.bat :app:assembleRelease :app:bundleRelease --console=plain
```

Las instrumentadas necesitan un dispositivo. Los resultados reales, informes y
comprobaciones pendientes se registran en ESTADO-DESARROLLO.md del repositorio.
No se certifica publicación, instalación Release firmada o Crashlytics por compilar.
Código Apache-2.0; wrapper con sus avisos propios.
