# Depurar y documentar funciones del dispositivo

## Aislar el punto de fallo

Una pantalla vacía no identifica la causa. Divide la cadena antes de cambiar código: acción del usuario → permiso o contrato → resultado externo → transformación → estado → presentación. Si Photo Picker entrega una URI pero falla la lectura, pedir permisos generales de galería no arregla el decodificador.

En Android Studio abre **Logcat**, selecciona el proceso y reproduce una sola acción. Busca la excepción y su primera causa; identifica la clase propia que recibió una entrada inesperada. No publiques registros con coordenadas personales, rutas privadas o contenido de fotografías.

| Síntoma | Primera comprobación | Resultado que diferencia causas |
| --- | --- | --- |
| Cámara no abre | Contrato y aplicación receptora | Ausencia de app frente a fallo de FileProvider |
| Imagen girada al cargar | EXIF y rama de lectura | Orientación de entrada frente a giro solicitado |
| JPG que otro visor rechaza | MIME decodificado y cabecera | Nombre JPG frente a bytes JPEG |
| Botón Play sin sonido | Estado, `isPlaying` y salida de audio | READY pausado frente a reproducción efectiva |
| Mapa sin calles | Carga de estilo y conectividad | Error cartográfico frente a falta de ubicación |
| Aviso no visible | Permiso, canal y ajustes del sistema | Solicitud rechazada frente a publicación autorizada |

## Detenerse antes de transformar

Pon un breakpoint en `ImagenViewModel.rotar`, selecciona una imagen y pulsa Girar. Inspecciona las dimensiones del bitmap recibido y avanza hasta la actualización de estado. Si nunca se alcanza, revisa el evento del botón; si la nueva imagen existe pero no se presenta, revisa qué estado lee Compose.

En procesamiento de imagen, el hilo de trabajo puede cambiar por `withContext`. No elimines esa suspensión para facilitar el depurador: bloquearías la interfaz. Usa breakpoints dentro del procesador para inspeccionar allí los valores. La [guía oficial de depuración](https://developer.android.com/studio/debug) describe hilos, variables y evaluación de expresiones.

Para Media3 registra temporalmente cambios de estado y errores, sin incluir URI privadas en informes públicos. `READY`, `isPlaying` y posición responden a preguntas diferentes. Comprueba transiciones y recuperación, no una captura aislada del botón.

## Simular y medir con honestidad

El emulador permite fijar ubicación, cambiar permisos, girar y repetir instalaciones. Un punto enviado desde Extended Controls demuestra que la app recibe y representa esa entrada; no demuestra precisión GNSS, comportamiento de una antena real ni consumo de batería en movimiento.

La cámara virtual permite seguir el contrato y la URI. Para probar sensor y aplicación de cámara reales necesitas otro dispositivo. Del mismo modo, comprobar eventos de audio no demuestra su salida audible; comprueba volumen y escucha. En notificaciones, abrir desde un aviso debe probarse tanto con la app visible como fuera de ella.

## Un registro técnico breve y reproducible

Utiliza en tu copia del proyecto un README con cuatro datos: qué funcionalidad implementaste, cómo abrirla en Android Studio, qué acciones verificaste y qué limitaciones quedan. Un registro útil permite que otra persona repita el fallo:

```text
Entorno: emulador, Android/API y tamaño de ventana.
Entrada: imagen propia de dimensiones conocidas / audio local / posición simulada.
Acción: seleccionar, girar una vez, exportar PNG y abrir el resultado.
Esperado: dimensiones intercambiadas y PNG decodificable.
Observado: dimensiones y MIME reales, o error y acción para recuperarse.
Pendiente: repetir captura y apertura con cámara y receptor de un teléfono físico.
```

No sustituyas el resultado por «funciona» ni por «compila». Conserva una captura real cuando permita ver un defecto y especifica la acción anterior. Antes de compartirla elimina datos personales y evita hacer pasar una simulación por una medición física.
