# mapa — solucion

Abre esta carpeta **solucion** desde **Open** en Android Studio y espera la sincronización
Gradle. Instala Android SDK36 y selecciona JDK17 o JBR21 para Gradle. Escoge **app**,
un emulador o dispositivo API26 o posterior y pulsa **Run**.

La aplicación sigue el tema claro/oscuro del sistema. Inicial y solución comparten
applicationId: instalar una sustituye a la otra. La solución formativa se distribuye
[por separado](https://github.com/MJSPlayground/fp-dam2-pmdm-android/tree/main/proyectos/multimedia/mapa/solucion).

## Comprobaciones técnicas

```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:lintDebug --console=plain
.\gradlew.bat :app:connectedDebugAndroidTest --console=plain
```

Las pruebas Android requieren un dispositivo conectado. Los resultados realmente
obtenidos se registran en ESTADO-DESARROLLO.md del repositorio, distinguiendo emulador
y pruebas físicas pendientes.

Código y Gradle Wrapper bajo Apache-2.0; LICENSE incluido, avisos del wrapper en sus scripts.

