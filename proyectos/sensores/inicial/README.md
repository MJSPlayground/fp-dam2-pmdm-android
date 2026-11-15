# Laboratorio del dispositivo ? inicial

Abre la carpeta con settings.gradle.kts desde Android Studio, sincroniza y ejecuta app
(API26+, SDK36, JBR compatible). Usa las versiones de Recursos del curso.

Acelerómetro, giroscopio, luz, presión y temperatura se consultan solo mientras la
pantalla está visible. Cada sensor puede estar ausente. La demo muestra biometría
local sin cifrar archivos ni identificar cuentas. No guarda lecturas. Linterna pide
CAMERA al pulsar y se apaga al abandonar. Vibración requiere hardware real.

TODO sensor-unidades en Medidas.kt y sensor-luz en MainActivity.kt. El resto est? preparado.

Pruebas técnicas opcionales: gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:lintDebug
Los resultados reales se registran en ESTADO-DESARROLLO.md del repositorio. No se
certifica biometría, flash ni vibración física por compilar o usar emulador.

Código Apache-2.0; LICENSE y avisos del Gradle Wrapper incluidos.
