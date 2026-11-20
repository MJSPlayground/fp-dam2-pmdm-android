# mapa — inicial

Abre esta carpeta desde **Open** en Android Studio, espera la sincronización Gradle y ejecuta **app** en un emulador o dispositivo API 26 o posterior. Necesitas SDK 37 y Gradle JDK compatible (JBR 25 de Android Studio). Las versiones se fijan en el catálogo; no hace falta Gradle global.

El mapa, el ciclo de vida, el estilo y un marcador de referencia están implementados. Completa `mapa-permiso`, `mapa-lectura` y `mapa-posicion` en `MainActivity.kt` siguiendo la práctica. `UbicacionActual` se entrega como ayuda de plataforma.

Inicial y solución comparten applicationId; instalar otra variante sustituye a la anterior. Conserva tu copia de trabajo. La solución completa se descarga por separado desde la guía pública. El código de referencia no se incluye oculto dentro del inicial.

## Archivos y recorrido

`MainActivity.kt` aloja MapView con AndroidView y conecta eventos. `UbicacionActual.kt` encapsula LocationManagerCompat; `ui/Theme.kt` sigue claro/oscuro del sistema. `strings.xml` contiene mensajes y formato de coordenadas.

## Comprobaciones técnicas

```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:lintDebug --console=plain
.\gradlew.bat :app:connectedDebugAndroidTest --console=plain
```

La compilación y lint no demuestran funcionamiento de hardware o servicios externos. Las pruebas instrumentadas requieren dispositivo conectado; el comando no implica que se hayan ejecutado. Los resultados reales y las limitaciones de cada hito se registran en `ESTADO-DESARROLLO.md` del repositorio.

El mapa requiere Internet. Datos: © OpenStreetMap contributors; cartografía OpenFreeMap/OpenMapTiles; representación MapLibre Native 12.2.0. Conserva la atribución. No realiza descargas offline masivas ni almacena coordenadas. Consulta [condiciones del proveedor](https://openfreemap.org/quick_start/).

Comprueba dos posiciones simuladas, denegación, permiso aproximado, proveedor desactivado y salida durante lectura. En dispositivo físico quedan por comprobar precisión y proveedor reales, consumo y comportamiento interior/exterior; el emulador no acredita esas pruebas.

Código original y Gradle Wrapper bajo Apache-2.0; LICENSE incluido. MapLibre y las demás bibliotecas conservan sus propias licencias.
