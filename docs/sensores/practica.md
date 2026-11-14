# Práctica guiada: laboratorio del dispositivo

La aplicación muestra sensores disponibles, ofrece respuesta háptica, controla una linterna y permite una demostración de reautorización biométrica. El registro, los permisos y la interfaz están preparados; completarás la presentación de luz y una conversión de unidades equivalente a los ejemplos. Lee [sensores](lecturas.md) y [hardware](hardware.md).

[Proyecto inicial — ZIP](../descargas/proyectos/sensores-inicial.zip){ .md-button .md-button--primary }

???+ example "Preparación y lectura resuelta"

    Abre la carpeta con `settings.gradle.kts` en Android Studio, sincroniza y ejecuta **app**. Dentro de `app/src/main/java/es/mjsplayground/pmdm/sensores`, abre `MainActivity.kt` y `Medidas.kt`. La app puede iniciarse sin todos los sensores; no necesitas conectar un dispositivo que tenga termómetro.

    **Observa el código existente.** Sigue `onResume` → `seleccionar` → `registerListener` → `onSensorChanged`. Antes de registrar otro sensor, se retira la escucha anterior. Al abandonar la Activity, `onPause` elimina el registro. Los datos solo se muestran; no se escriben en archivos.

    **Ejemplo resuelto.** El caso de presión obtiene `v[0]` y lo entrega a `R.string.medida_presion`, cuyo formato añade hPa. El caso de aceleración recibe tres valores y llama a `magnitud`. Abre esa función: valida que sean finitos y aplica la raíz de la suma de cuadrados.

    **Comprueba.** Elige acelerómetro y modifica la posición en Virtual sensors del emulador. Deben cambiar las cifras. En reposo no esperes módulo cero: la gravedad está incluida. Elige temperatura; si no existe, debe mostrarse Sensor no disponible, sin cerrar la app.

??? example "Presentar luz siguiendo el ejemplo"

    **Ahora te toca — `sensor-luz`.** En `MainActivity.onSensorChanged`, sustituye el mensaje provisional del caso de luz por su valor y unidad, siguiendo el patrón preparado de presión. El recurso `medida_luz` ya está definido y utiliza el mismo tipo de argumento decimal. No añadas un registro de sensor por cada recomposición.

    ??? tip "Pista: separa dato y unidad"

        Los dos sensores usan el primer componente del evento, pero sus unidades y recursos son diferentes. Conserva la selección del contrato mediante el tipo del sensor.

    **Comprueba.** Si hay sensor de luz, cambia la entrada virtual o la iluminación del dispositivo real y verifica una cifra en lux. Si no existe, comprueba el mensaje de ausencia y pasa a otra función; no inventes una lectura para ocultar ese estado.

??? example "Convertir una magnitud conocida"

    El giroscopio entrega radianes por segundo y la pantalla anuncia grados por segundo. El TODO devuelve cero provisionalmente; esa cifra no representa todavía una lectura convertida.

    **Ahora te toca — `sensor-unidades`.** Completa `radianesAGrados` en `Medidas.kt`. Conserva la validación de finitud y devuelve la conversión angular. La relación es π rad = 180°. Puedes usar `Math.toDegrees` o expresar esa relación. El resultado sigue siendo una velocidad angular, no un ángulo acumulado.

    ??? tip "Pista: signo y cero"

        El giro negativo debe mantener su signo. Cero radianes debe producir cero grados; π/2, aproximadamente 90. No redondees antes de convertir: el formato de pantalla ya controla los decimales.

    **Comprueba.** Evalúa 0, π/2 y −π desde el depurador o una prueba local. Observa el giro simulado y comprueba la unidad °/s. Comparar contra cero únicamente no detectaría el TODO original.

??? example "Permisos y resultados que no se pueden simular como físicos"

    **Observa el código existente.** Sigue el botón de linterna hasta la solicitud CAMERA y su callback. Elige Denegar: la app debe permitir seguir leyendo sensores. Si concedes, se comprueba qué cámara tiene flash y se trata que esté ocupada. Al abandonar, se intenta apagar.

    **Ahora te toca.** Sin modificar la infraestructura, prepara un registro con capacidad disponible, permiso, acción y resultado de linterna, vibración y biometría. Diferencia una operación solicitada de un efecto observado.

    **Comprueba.** En un dispositivo físico con vibrador, pulsa la acción y verifica sensación y texto. En un modelo sin flash debe aparecer su ausencia. Para biometría, prueba Cancelar y, solo si está configurada, autenticar: el texto de demostración aparece únicamente tras éxito y se oculta al abandonar la pantalla. No hay datos privados cifrados en este laboratorio.

## Solución de referencia

[Solución completa — ZIP](../descargas/proyectos/sensores-solucion.zip){ .md-button }

Resuelve los dos TODO y añade pruebas de magnitud, conversión y valores no finitos. Las comprobaciones físicas quedan condicionadas por el dispositivo disponible, aunque las pruebas JVM pasen.
