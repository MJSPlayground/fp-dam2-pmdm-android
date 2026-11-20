# Zona de paseo — solucion

Abre esta carpeta desde **Open** en Android Studio, espera sincronización Gradle y ejecuta **app**. Mantiene SDK 37, mínimo 26, AGP 9.2.1, Kotlin 2.2.21 y MapLibre Native 12.2.0 del curso. Selecciona un Gradle JDK compatible, como JBR 25. No requiere claves ni Google Play Services.

El esquema local arranca sin Internet. Muestra una zona propia aproximada de 500 m y permite simular un punto interior/exterior. El seguimiento real se activa por botón y se detiene en onStop; no hay servicio ni ubicación de fondo. Un botón distinto carga cartografía OpenFreeMap, con atribución visible y del SDK. El modo local no representa calles ni descarga teselas.

La solución clasifica posiciones teniendo en cuenta incertidumbre, actualiza marcador, consulta lugar/ruta y conserva capas al cambiar de estilo. El servicio local devuelve una ruta sintética, sin validez para navegación.

## Recorrido de archivos

- `MainActivity.kt`: MapView/Compose, mapa local/remoto, eventos, estado y cancelación.
- `Geometria.kt`: distancia y clasificación; Kotlin puro con pruebas JVM.
- `Seguimiento.kt`: permisos, proveedor disponible y listener de LocationManager.
- `ServicioCartografico.kt`: HTTP en IO, parsers Nominatim/OSRM, límites y caché en memoria de hasta 16 respuestas válidas.
- `servidor.py`: respuestas propias de laboratorio; no consulta proveedores externos.

## Servidor reproducible

En un terminal de esta carpeta ejecuta `py servidor.py` (Python 3). Escucha solo en `127.0.0.1:8091`; detén con Ctrl+C. En el emulador conserva `http://10.0.2.2:8091`. Activa la casilla de consultas y busca `Museo`. Otros nombres devuelven vacío; `ERROR` devuelve HTTP 503 y `ROTO`, un contrato incompatible. La ruta devuelve tres puntos propios: no son un itinerario real por calles.

En dispositivo físico el host 10.0.2.2 no apunta a tu ordenador. Para probar el servidor con USB puedes configurar ADB reverse y usar `http://127.0.0.1:8091`, o un servidor propio HTTPS accesible. No abras el servidor a Internet ni atribuyas al teléfono una prueba hecha en emulador.

Para un proveedor real, introduce la base autorizada del servicio con contrato compatible. La app no incluye la dirección pública Nominatim ni ejecuta búsquedas al escribir. La casilla autoriza enviar la consulta y los extremos públicos de la ruta, nunca la posición GPS. Verifica condiciones y cuota global del proveedor; un retardo de un móvil no controla el tráfico de toda el aula. HTTPS obligatorio fuera de loopback/host del emulador en debug. Release no habilita HTTP.

## Pruebas

```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:lintDebug :app:assembleDebugAndroidTest --console=plain
py test_servidor.py
```

Las pruebas JVM cubren distancias, incertidumbre y límites, coordenadas no válidas, respuestas vacías, orden longitud/latitud y errores de ruta. Las instrumentadas prueban simulación visible sin permiso ni servidor.

Registra aparte los resultados reales en `ESTADO-DESARROLLO.md` del repositorio. Compilar no demuestra GPS real, seguimiento de fondo ni disponibilidad del proveedor. Verifica manualmente parada al salir, concesión/denegación/aproximada, cartografía remota y ambos caminos de servicio. No se declara verificación física ni API antiguas sin ejecutarlas.

Código original Apache-2.0, LICENSE incluido. MapLibre conserva sus licencias. Los datos remotos mantienen atribución OpenStreetMap/OpenMapTiles/OpenFreeMap; las geometrías locales sintéticas son propias.
