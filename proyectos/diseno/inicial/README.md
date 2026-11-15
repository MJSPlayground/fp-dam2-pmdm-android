# Galería adaptable — inicial

Abre esta carpeta desde Open en Android Studio, espera la sincronización, selecciona
el Gradle JDK compatible de Recursos y ejecuta app en API 26 o posterior. SDK 36.

MainActivity contiene cuadrícula, categorías LazyRow, navegación, refresco simulado,
imágenes Coil y transición a detalle. Datos.kt conserva catálogo y operaciones puras.
La interfaz se adapta a la clase de ancho de ventana. Los textos tienen traducción inglesa.
Las tres ilustraciones vectoriales son originales. La cuarta imagen carga una captura
propia del repositorio por HTTPS y necesita Internet; se representa el fallo si no llega.
No se descargan imágenes privadas ni se envían datos. Actualizar simula 800 ms en memoria:
no descarga un catálogo nuevo ni invalida automáticamente la caché de Coil.

TODO: columnas en Datos.kt; alternar favoritos en Datos.kt; onError en Foto; stateDescription del botón en MainActivity. La aplicación arranca con una columna y las demás piezas de referencia preparadas. No contiene retos resueltos.

Inicial y solución comparten identificador: instalar una sustituye a la otra.
El catálogo fija Coil 3.3.0 y usa la BOM Compose del curso. Las transiciones compartidas
y pull-to-refresh declaran opt-in donde la combinación fijada lo requiere.

Práctica: https://mjsplayground.github.io/fp-dam2-pmdm-android/diseno/practica/

## Validación técnica opcional

```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:lintDebug :app:assembleDebugAndroidTest --console=plain
```

La ejecución realmente comprobada se registra en ESTADO-DESARROLLO.md. Una compilación
no demuestra TalkBack, adaptación visual, descarga remota o ejecución por Run en el IDE.
Ilustraciones y código Apache-2.0; wrapper con sus avisos propios. No se incluye ninguna
fuente tipográfica comercial: la pantalla usa Serif del sistema.
