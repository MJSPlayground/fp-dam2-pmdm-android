# BLE, exploración y características GATT

## Roles que conviene separar

En BLE, un periférico puede anunciar su presencia y un central puede explorarlo y conectar. Tras conectar, GATT organiza los datos en **servicios**, **características** y, cuando proceda, **descriptores**. Los roles de central/periférico describen la conexión; cliente/servidor GATT describe quién solicita y quién expone datos. A menudo coinciden de la manera habitual, pero no son el mismo concepto.

El laboratorio puede anunciar un servicio propio en un dispositivo compatible y actuar de central en otro. Su servicio y característica tienen UUID definidos en `Enlaces.SERVICIO` y `DATO`; no se utilizan UUID de fabricantes ni se escriben características desconocidas. Comprueba las [guías de GATT](https://developer.android.com/develop/connectivity/bluetooth/ble/connect-gatt-server) junto al código.

## Permisos y disponibilidad

Desde API 31, `BLUETOOTH_SCAN` permite explorar; `BLUETOOTH_CONNECT`, conectar y operar; `BLUETOOTH_ADVERTISE`, anunciar. Se solicitan solo para la acción correspondiente. La app declara que no deduce ubicación de los resultados con `neverForLocation`; esto puede filtrar determinados anuncios.

En API 26–30 la exploración necesita la autorización de ubicación correspondiente y pueden influir los ajustes de ubicación del sistema. Los requisitos antiguos del manifiesto se acotan a esas versiones. Un permiso concedido no crea hardware: el anunciante puede no estar disponible aunque exista un adaptador central.

La app comprueba capacidad de anuncios y muestra un estado cuando falta. Ninguna búsqueda ni anuncio comienza al abrir. Las operaciones se detienen al salir de la pantalla.

## Explorar con filtro y límite

`BluetoothLeScanner.startScan` entrega resultados en `ScanCallback`. El ejemplo utiliza `ScanFilter` para el UUID del servicio propio, modo de bajo consumo y duración máxima de diez segundos. La lista elimina duplicados y se limita a diez equipos; una persona selecciona cuál conectar.

La [guía oficial de exploración](https://developer.android.com/develop/connectivity/bluetooth/ble/find-ble-devices) recomienda acotar su duración. No reinicies el escaneo en bucle ni lo mantengas por cada recomposición. Una lista vacía puede significar que el servicio no se anuncia, que no hay soporte o que hay restricciones; no prueba ausencia de cualquier Bluetooth cercano.

## Conectar y descubrir antes de leer

`connectGatt` inicia una operación asíncrona; su valor de retorno no significa que los servicios estén disponibles. El callback distingue conexión, descubrimiento y desconexión. Al conectar, solicita `discoverServices`; después busca el UUID de servicio y la característica esperada. Solo entonces permite trabajar con ella.

El laboratorio entrega `leerBle` resuelto. `readCharacteristic` comunica si aceptó iniciar la operación; los bytes llegan después al callback, con un estado. La [guía de transferencia BLE](https://developer.android.com/develop/connectivity/bluetooth/ble/transfer-ble-data) explica esa diferencia. El ejemplo atiende el callback con copia del valor desde API 33 y el anterior para API 26–32.

Mantiene una operación GATT pendiente cada vez. No envía escritura y lectura simultáneas esperando que el sistema las ordene como necesita nuestra aplicación. Si se desconecta, libera GATT y la característica deja de estar disponible.

## Escribir y comprobar la respuesta

Una característica declara propiedades, por ejemplo lectura y escritura. El servidor también define permisos de acceso; no basta con que el cliente desee escribir. En el servicio propio se permite una escritura pequeña con respuesta. Se rechazan escrituras preparadas, offsets distintos de cero y cargas superiores a 20 bytes.

Desde API 33 se entrega el valor directamente a `writeCharacteristic` junto al tipo de escritura y se recibe un código de aceptación. Para las versiones anteriores se configura el valor del objeto y se usa la operación antigua. Este fragmento muestra **solo el inicio moderno**, dentro del método que ya tiene `actual` y `dato` preparados:

```kotlin title="Referencia de API 33 — no es el método completo"
val aceptada = actual.writeCharacteristic(
    dato,
    "PING".toByteArray(Charsets.UTF_8),
    BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
) == BluetoothStatusCodes.SUCCESS
```

La confirmación definitiva llega en `onCharacteristicWrite`. El servidor procesa PING con el mismo protocolo puro utilizado en RFCOMM y conserva PONG como valor para la siguiente lectura. Escribe, espera la confirmación y después lee. El ejemplo no implementa notificaciones de características ni pretende que escribir cause automáticamente un callback de lectura.

## Servidor GATT y publicidad propios

`openGattServer` crea el servidor; `addService` incorpora un servicio primario con su característica. La publicidad comienza después del callback de servicio añadido, para no ofrecer un endpoint que todavía no existe. No incluye el nombre del dispositivo en el anuncio; basta el UUID del laboratorio.

Los callbacks atienden peticiones de lectura/escritura y responden con `sendResponse`. El anuncio dura como máximo 60 segundos y se detiene al salir o pulsar Detener. Esta demostración no exige vínculo cifrado para sus mensajes públicos fijos; no debe reutilizarse para credenciales, datos médicos o mensajes privados sin diseñar protección adecuada.

Para verificarlo hacen falta dos dispositivos compatibles o un periférico de prueba que exponga exactamente ese servicio. La compilación y las pruebas del protocolo acreditan código y reglas; no anuncios, conectividad radioeléctrica ni callbacks físicos.
