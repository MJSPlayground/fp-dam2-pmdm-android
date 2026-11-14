# Laboratorio del dispositivo ? inicial

Abre la carpeta con settings.gradle.kts desde Android Studio, sincroniza y ejecuta app
(API26+, SDK36, JBR compatible). Usa las versiones de Recursos del curso.

Aceler?metro, giroscopio, luz, presi?n y temperatura se consultan solo mientras la
pantalla est? visible. Cada sensor puede estar ausente. La demo muestra biometr?a
local sin cifrar archivos ni identificar cuentas. No guarda lecturas. Linterna pide
CAMERA al pulsar y se apaga al abandonar. Vibraci?n requiere hardware real.

TODO sensor-unidades en Medidas.kt y sensor-luz en MainActivity.kt. El resto est? preparado.

Pruebas t?cnicas opcionales: gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:lintDebug
Los resultados reales se registran en ESTADO-DESARROLLO.md del repositorio. No se
certifica biometr?a, flash ni vibraci?n f?sica por compilar o usar emulador.

C?digo Apache-2.0; LICENSE y avisos del Gradle Wrapper incluidos.
