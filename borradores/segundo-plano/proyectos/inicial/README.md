# Avisos y reproducción — inicial

Abre esta carpeta con Android Studio, sincroniza y ejecuta app en API 26 o superior,
SDK36 y Gradle JDK de Recursos. Concede notificaciones para probar los avisos locales.
No requiere cuenta Firebase para notificaciones, trabajo local ni audio.

TODO en Avisos.kt: estilo expandido; Trabajos.kt: solicitud WorkManager y receiver; MainActivity.kt: habilitar la solicitud al completarla. La notificación básica y MediaSessionService están preparadas como referencias.

WorkManager2.10.5 y Media3 1.9.4 conservan las versiones del curso. La condición de red
es parte de una sincronización simulada: no se envía información a ningún servidor.
La demora y reintentos no prometen hora exacta. Cancelar revisión cancela la solicitud
única; programarla otra vez reemplaza la pendiente.

Reproducir inicia el tono original del laboratorio en bucle; su servicio permite
seguir al salir de la Activity y ofrece controles multimedia del sistema. Detener
interrumpe el sonido. No se inicia reproducción automáticamente. El tono procede del
generador original proyectos/multimedia/reproductor/generar_audio.py del repositorio,
bajo Apache-2.0; no contiene muestras de terceros.

MensajesService es un adaptador FCM compilable. No hay google-services.json ni proyecto
Firebase inventado; auto-init remoto está desactivado. Recibir mensajes reales y
registrar tokens con backend requieren configurar una integración propia por separado.
No se imprime el token ni se incluye una cuenta de servicio o clave privada.

## Comprobación técnica opcional

```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:lintDebug :app:assembleDebugAndroidTest --console=plain
.\gradlew.bat :app:connectedDebugAndroidTest --console=plain
```

Los resultados ejecutados y las comprobaciones remotas o físicas pendientes se
registran en ESTADO-DESARROLLO.md. Una prueba del Worker aislado no demuestra el
planificador del sistema. No se certifica FCM por compilar su servicio.
Código Apache-2.0 y wrapper con avisos propios.
