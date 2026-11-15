# Consulta con texto e imagen — inicial

Abre la carpeta con `settings.gradle.kts` en Android Studio, sincroniza y ejecuta
**app** en un emulador API26+. El servidor incluido usa Python3.11 estándar.

Desde la carpeta `servidor`, ejecuta `python servidor.py`. Escucha exclusivamente
en `127.0.0.1:8095`; Android lo alcanza mediante `10.0.2.2`. Por defecto responde
con datos simulados y no llama a ningún modelo. Detén con Ctrl+C al terminar.

Completa `ia-validacion` en `ClienteIa.kt` y `ia-cancelacion` en
`ConsultaViewModel.kt`. HTTP, estado, preparación de imagen y pantalla están resueltos.

El modo real exige iniciar expresamente `python servidor.py --real`, con las variables
`GEMINI_API_KEY` y `GEMINI_MODEL` definidas solo en el proceso de servidor. Consulta la
documentación actual del proveedor y sus condiciones antes de usarlo. Puede consumir
cuota. No incluyas credenciales en código, recursos, capturas ni archivos versionados.

No es un backend de producción: antes de exponerlo fuera del laboratorio requiere
autenticación, límites por identidad, protección de datos y operación mantenida.

Pruebas técnicas: `gradlew.bat :app:assembleDebug :app:lintDebug` y
`python servidor/test_servidor.py`. Estas últimas usan red loopback y un doble del
proveedor; no hacen inferencia real ni facturan.

Código Apache-2.0; LICENSE y avisos del Wrapper incluidos.
