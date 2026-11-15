# Soluciones de repaso de redes

## Consulta de estado

La [solución descargable](../descargas/proyectos/redes-solucion.zip) añade ESTADO → LISTO al protocolo puro compartido. `ConsolidacionTest` comprueba esa respuesta junto a HOLA y PING; `ProtocoloTest` conserva desconocidos y longitud. No es necesario modificar sockets ni características para decidir el texto de respuesta.

## Respuestas razonadas

1. Hay un transporte activo y el servidor respondió con un error de servicio. Un 503 no significa por sí mismo que Wi-Fi esté desconectado. La disponibilidad de un transporte tampoco garantiza otra URL.
2. El primero cumple el contrato de lista, pero no contiene elementos. El segundo tiene un tipo incompatible: convertirlo en lista vacía ocultaría un fallo de contrato.
3. El objeto incluye datos y metadatos. La app debe interpretar el sobre y solo habrá cargado las páginas que haya solicitado y recibido. Este laboratorio muestra una página y no anuncia paginación completa.
4. Puede no haber servidor escuchando el UUID propio, o estar fuera de alcance o desactivado. El vínculo previo no crea un servicio ni demuestra una conexión actual.
5. Una frontera definida por el protocolo, aquí salto de línea y máximo de bytes. El receptor acumula hasta esa frontera; no confía en que cada `read` equivalga a un mensaje completo.
6. SCAN, CONNECT y ADVERTISE respectivamente. HTTP no necesita esos permisos. En versiones antiguas se aplican los requisitos correspondientes, incluyendo ubicación para explorar BLE.
7. No. Deben llegar conexión correcta, descubrimiento de servicios y localización de la característica esperada. El objeto devuelto representa una operación que sigue desarrollándose.
8. El callback de escritura con estado correcto. Iniciar y terminar son dos resultados distintos; debe conservarse una operación pendiente hasta resolverla o fallar.
9. El filtro busca un servicio concreto y hay límites de tiempo, soporte y permisos. Un dispositivo con otro servicio o que no esté anunciando no aparecerá.
10. MockEngine comprueba el cliente y su conversor con respuestas controladas; el protocolo comprueba reglas sin transporte. Una prueba física verifica radios, permisos, servicio y callbacks reales. Ninguna de las dos primeras acredita la tercera.

## Caso de integración BLE

Primero comprueba que A informa de anuncio iniciado, no de ausencia de capacidad. Verifica Bluetooth y permisos en ambos. El anuncio del ejemplo empieza después de añadir el servicio GATT y expira a los 60 segundos; vuelve a iniciarlo y realiza en B una exploración explícita de diez segundos. Conserva el UUID común de la solución y selecciona únicamente el equipo propio de laboratorio.

Si continúa vacío, registra modelos, versiones, capacidades y estados obtenidos. Probar otro dispositivo compatible ayuda a distinguir una limitación de anunciante de un error del protocolo. No concluyas que el código intercambió datos porque el proyecto compiló ni publiques identificadores reales para pedir ayuda.
