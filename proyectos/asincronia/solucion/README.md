# Buscador con flujos — solución

Abre esta carpeta desde **Open** en Android Studio. Espera la sincronización Gradle,
selecciona el Gradle JDK compatible de Recursos, SDK 37 y un dispositivo API 26 o
posterior; ejecuta **app**. No requiere red, cuenta, permisos ni servicios externos.

`MainActivity` abre la pantalla; `BuscadorViewModel` expone consultas y resultados;
`Catalogo.kt` contiene datos locales, filtros y la tubería Flow. La latencia es una
simulación mediante `delay`, no una petición HTTP. Consulta `docs/asincronia/practica.md`
en la web del repositorio para seguir la práctica.

La solución filtra tras 300 ms sin escritura y simula 600 ms de consulta. Escribe Kotlin: aparecen dos libros; activa Solo favoritos: queda uno. !error genera un error recuperable; al cambiarlo vuelve a buscar. El archivo Retos.kt resuelve las actividades autónomas, separado del inicial.

Inicial y solución comparten applicationId; instalar una sustituye a la otra.
El tema sigue el sistema. Los datos no se guardan al finalizar el proceso.

## Verificación técnica

```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:lintDebug --console=plain
```

Las pruebas de la solución verifican filtros, debounce, cancelación, combinación,
recuperación de errores y retos con reloj virtual. No certifican por sí mismas la
interfaz, TalkBack o ejecución por Run dentro del IDE. La evidencia real se registra
en ESTADO-DESARROLLO.md del repositorio. Código Apache-2.0 y wrapper con avisos propios.
