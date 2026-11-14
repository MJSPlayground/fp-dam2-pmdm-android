# Leer e interpretar sensores

## De un dato físico a un evento

`SensorManager` permite descubrir sensores y recibir lecturas mediante un `SensorEventListener`. No abre una conexión HTTP ni devuelve una medida al instante de registrarse: el sistema llama a `onSensorChanged` cuando llega un evento. La pantalla observa un estado actualizado a partir de esos eventos.

Antes de registrarte necesitas decidir qué magnitud buscas y si ese sensor existe. `getDefaultSensor(tipo)` devuelve un sensor o `null`. Encontrar un acelerómetro no garantiza disponer de giroscopio, y que un teléfono mida la temperatura de su batería no significa que tenga termómetro de ambiente.

La [guía oficial de sensores](https://developer.android.com/develop/sensors-and-location/sensors/sensors_overview) clasifica sensores físicos y calculados y describe disponibilidad, coordenadas y precisión. El laboratorio usa las APIs de plataforma, sin una dependencia adicional para leerlos.

## Magnitudes y ejes

| Sensor | Qué mide | Unidad | Interpretación importante |
| --- | --- | --- | --- |
| Acelerómetro | Aceleración en X, Y, Z, incluida gravedad | m/s² | En reposo el módulo suele aproximarse a 9,81; no necesariamente cero |
| Giroscopio | Velocidad angular por eje | rad/s | No es un ángulo de orientación; convertir a grados no integra en el tiempo |
| Luz | Iluminancia | lux | No mide el brillo configurado de la pantalla |
| Presión | Presión atmosférica | hPa | No es directamente una altura absoluta |
| Temperatura ambiente | Temperatura del aire | °C | No equivale a temperatura de batería o CPU |

Los ejes se definen respecto al dispositivo, no respecto a la habitación. Rotar la interfaz no transforma automáticamente una lectura en coordenadas del mundo. Para una representación que deba seguir la orientación de pantalla habría que remapear ejes; el laboratorio presenta los del sensor, identificados como tales.

Para un vector `(3, 4, 0)`, su magnitud es `sqrt(3² + 4² + 0²) = 5`. Conserva las unidades originales. Calcular un módulo de aceleración no produce una distancia ni una velocidad. Integrar datos con ruido exige mucho más que sumar muestras: errores pequeños se acumulan.

## Registrar y retirar una escucha

Este fragmento ilustra el registro del acelerómetro dentro de un propietario que implementa `SensorEventListener`. `sensores` es el servicio obtenido con `getSystemService(SensorManager::class.java)`; las clases proceden de `android.hardware`.

```kotlin
val sensor = sensores.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
val disponible = sensor != null && sensores.registerListener(
    this, sensor, SensorManager.SENSOR_DELAY_NORMAL
)
```

Comprueba el booleano de `registerListener`: localizar un sensor y conseguir registrarse no son la misma operación. La demora solicitada orienta la frecuencia; no garantiza un intervalo exacto. No hagas trabajo pesado ni escribas un archivo en cada callback.

En el laboratorio, `onResume` registra y `onPause` llama a `unregisterListener(this)`. Elegir otro sensor primero retira la escucha anterior. Así no se siguen procesando muestras que una pantalla invisible no necesita ni se acumulan registros al volver.

La [referencia SensorManager](https://developer.android.com/reference/android/hardware/SensorManager) documenta el registro y sus límites. Las frecuencias elevadas de determinados sensores tienen restricciones de plataforma; el curso utiliza `SENSOR_DELAY_NORMAL`, sin solicitar acceso de alta frecuencia.

## Copiar el dato que vas a conservar

Android puede reutilizar el array de `SensorEvent.values`. Si lo guardas directamente y lo lees mucho después, su contenido puede haber cambiado. En el callback puedes copiarlo antes de almacenarlo o extraer de inmediato los valores necesarios:

```kotlin
val valores = event.values.copyOf()
val x = valores[0]
```

No todos los sensores entregan tres componentes. Luz, presión y temperatura usan su primer valor; intentar leer `values[2]` para todos los tipos puede cerrar la app. El `when(event.sensor.type)` selecciona el contrato antes de interpretar el array. `onAccuracyChanged` informa de cambios de precisión cuando el sensor los ofrece; no convierte una lectura en una medida certificada.

## Ruido, frecuencia y presentación

Una lectura puede variar aunque el dispositivo parezca quieto. Redondear la cifra visible mejora su legibilidad, pero no elimina ruido en el dato original. Un filtro pasa-bajos suaviza cambios mediante una media ponderada, por ejemplo `filtrada = alfa * filtrada + (1 - alfa) * nueva`; introduce retardo y necesita una inicialización coherente.

No apliques ese filtro a todas las magnitudes sin propósito. Para detectar un movimiento breve, suavizar demasiado puede borrarlo. Para una cifra ambiental, actualizar cientos de veces por segundo solo gasta recursos y dificulta leer. La UI del laboratorio utiliza valores formateados con recursos Android y la configuración regional.

## Probar sin certificar hardware inexistente

En Extended Controls del emulador, **Virtual sensors** permite variar algunas entradas. Una lectura que cambia comprueba el enlace evento → estado → texto. Un sensor ausente debe producir el mensaje correspondiente. Para luz o presión, el modelo virtual puede ofrecer controles diferentes o carecer de ellos.

En un teléfono real, repite reposo, inclinación, giro y cambios ambientales posibles. No uses la app como termómetro clínico ni instrumento calibrado. Documenta hardware, unidades y qué estímulo produjo cada cambio; no escribas que todos los dispositivos ofrecen el mismo conjunto.
