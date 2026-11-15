# Mensajería cercana — solucion

Abre esta carpeta desde **Open** en Android Studio y espera sincronización Gradle. Ejecuta **app** con SDK 36 y Gradle JDK compatible (JDK 17 o JBR 21). Mínimo API 26. Mantiene Kotlin 2.2.21 y añade Ktor 3.3.3 con motor Android y JSON; catálogo fijado y wrapper incluidos.

El inicio no pide permisos Bluetooth ni inicia radio. HTTP funciona de forma independiente. Para Bluetooth utiliza solamente dos equipos propios o autorizados y mensajes públicos fijos del laboratorio. No se conectan equipos automáticamente. Los permisos se solicitan según la acción y todas las operaciones se cierran al salir.

La solución incluye filtro de no leídos, saludo HOLA, PING y consulta ESTADO del repaso; RFCOMM cliente/servidor y BLE central/periférico GATT con una operación pendiente cada vez.

## Servidor HTTP propio

Ejecuta `py servidor.py` desde un terminal en esta carpeta con Python 3. Escucha solo en `127.0.0.1:8092`. En el emulador usa `http://10.0.2.2:8092/mensajes`; detén con Ctrl+C. `?vacio=1`, `?error=1` y `?roto=1` reproducen vacío, HTTP 503 y contrato incompatible. No consulta servicios externos. En teléfono puedes usar ADB reverse por USB y loopback, o un servidor propio HTTPS accesible. Release no permite el HTTP de laboratorio.

## Bluetooth físico

- Clásico: vincula los equipos desde Ajustes. A escucha 15 s; B consulta vinculados, elige A y envía HOLA o PING. Cada envío tiene su sesión. No escribas datos privados.
- BLE: A necesita modo anunciante compatible. Anuncia el servicio propio 60 s. B explora ese UUID 10 s, selecciona A y conecta. Tras descubrir la característica, lee HOLA, escribe PING, espera confirmación y lee PONG.
- Detén o abandona una pantalla para comprobar cierre. Documenta permiso denegado, ausencia de soporte, desconexión y espera agotada. El emulador no acredita anuncios ni transferencia física.

Los UUID están en `Enlaces.SERVICIO` y `DATO`. No sustituyas esos IDs por características de equipos ajenos. Las tramas están limitadas a 20 bytes y los mensajes son HOLA/PING/ESTADO; el protocolo no autentica usuarios ni es un chat privado de producción.

## Pruebas técnicas

```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:lintDebug :app:assembleDebugAndroidTest --console=plain
```

Los tests verifican el contrato JSON con Ktor MockEngine, errores HTTP/conversión y las reglas del protocolo. La prueba instrumentada verifica inicio sin operaciones Bluetooth; no certifica radio física.

Los resultados realmente obtenidos se registran en `ESTADO-DESARROLLO.md` del repositorio. Distingue API probada, hardware y ramas pendientes. Nunca se declara verificado RFCOMM/GATT real por compilar.

Código Apache-2.0, LICENSE incluido; Ktor y AndroidX conservan sus licencias.
