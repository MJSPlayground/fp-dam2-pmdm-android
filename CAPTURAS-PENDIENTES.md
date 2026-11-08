# Capturas reales

No hay imágenes simuladas ni enlaces a capturas inexistentes en la web. Las siguientes ayudarían a orientar al alumnado; las instrucciones textuales permiten continuar sin ellas.

| Prioridad / estado | Página y posición | Captura y procedimiento | Destino, alt y pie |
| --- | --- | --- | --- |
| Útil / pendiente | `ecosistema/entorno.md`, después de crear el dispositivo | Android Studio: Device Manager, perfil de teléfono API 36 arrancado. Mostrar selector y pantalla de inicio sin datos personales. | `docs/assets/capturas/device-manager.png`; alt: «Device Manager con un teléfono virtual API 36 iniciado»; pie: «Perfil y sistema del dispositivo de prueba». |
| Útil / pendiente | `ecosistema/practica.md`, tras primera ejecución | Ejecutar proyecto inicial desde Android Studio. Capturar editor, selector de dispositivo y resultado real, sin rutas de usuario ni cuentas. Anotar versión del IDE. | `docs/assets/capturas/primera-ejecucion.png`; alt: «Proyecto inicial abierto en Android Studio y ejecutado en emulador»; pie: «El proyecto se abre desde su carpeta raíz y se ejecuta con Run». |

Las capturas automatizadas de QA web se guardan en `qa-captures/` (no se publican como contenido docente). Las pruebas reales del emulador se registran debajo; no equivalen a pruebas en teléfono físico.

## Recurso obtenido

`docs/assets/capturas/primera-app.png`: captura real por ADB de la solución ejecutada en Pixel_9, Android 16/API 36, con el mensaje visible tras pulsar el botón. Inspeccionada e incorporada a la práctica. La captura combinada IDE/emulador de la tabla sigue siendo opcional y pendiente; esta imagen no demuestra apertura desde la GUI del IDE.

`cuaderno-lista.png`, `cuaderno-editor-teclado.png` y `cuaderno-remotos.png`, en la misma carpeta: capturas reales de la solución Cuaderno en Pixel_9/API 36, 1080 × 2424 px, 420 dpi (aproximadamente 411 dp de anchura), castellano, fuente normal y tema claro. Se muestran una nota creada desde la UI, el editor con teclado y una consulta efectiva al servidor local de pruebas. Inspeccionadas e incorporadas a proyecto, estado y HTTP respectivamente. No prueban comunicación Wi-Fi entre dos dispositivos físicos.
