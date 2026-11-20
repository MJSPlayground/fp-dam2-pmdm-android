# notificaciones — solucion

Abre esta carpeta desde **Open** en Android Studio, espera la sincronización Gradle y ejecuta **app** en un emulador o dispositivo API 26 o posterior. Necesitas SDK 37 y Gradle JDK compatible (JBR 25 de Android Studio). Las versiones se fijan en el catálogo; no hace falta Gradle global.

La solución permite dos tipos de aviso con IDs distintos y abre sus detalles desde el sistema. Ambos son inmediatos y locales; no utiliza Firebase ni servicios de fondo.

Inicial y solución comparten applicationId; instalar otra variante sustituye a la anterior. Conserva tu copia de trabajo. La solución completa se descarga por separado desde la guía pública. El código de referencia no se incluye oculto dentro del inicial.

## Archivos y recorrido

`MainActivity.kt` solicita permiso en contexto, conserva la acción pendiente y recibe el destino en onCreate/onNewIntent. `Avisos.kt` define canal, contenido y PendingIntent; `strings.xml` e `ic_aviso.xml` aportan contenido e icono.

## Comprobaciones técnicas

```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:lintDebug --console=plain
.\gradlew.bat :app:connectedDebugAndroidTest --console=plain
```

La compilación y lint no demuestran funcionamiento de hardware o servicios externos. En la solución, antes de la prueba instrumentada concede POST_NOTIFICATIONS desde los ajustes de la app si utilizas API 33 o posterior. La prueba comprueba canal, IDs y actualización de avisos; la pulsación real de la bandeja se comprueba aparte. Las pruebas instrumentadas requieren dispositivo conectado; el comando no implica que se hayan ejecutado. Los resultados reales y las limitaciones de cada hito se registran en `ESTADO-DESARROLLO.md` del repositorio.

El evento de nota guardada se simula con un botón: no hay persistencia de notas. Comprueba bandeja, actualización por ID, ambos destinos, permiso denegado y canal bloqueado. Prueba API 26–32 además de API 33 o posterior para cubrir ambas ramas; no acredites una versión que no hayas ejecutado. Un banner flotante no es condición de éxito. No requiere Internet.

Código original y Gradle Wrapper bajo Apache-2.0; LICENSE incluido. MapLibre y las demás bibliotecas conservan sus propias licencias.
