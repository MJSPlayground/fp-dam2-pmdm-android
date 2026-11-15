# Consolidación y repaso de redes

## Ejercicio autónomo: estado del protocolo

El dispositivo del laboratorio debe admitir una consulta de estado, sin cambiar sus mensajes anteriores. Cuando recibe **ESTADO**, responde **LISTO**. HOLA conserva OK HOLA y PING conserva PONG. Los mensajes desconocidos siguen devolviendo ERROR y se mantiene el límite de 20 bytes.

Trabaja con el proyecto inicial o tu copia completada. Define pruebas que demuestren la compatibilidad de los tres mensajes, el caso desconocido y el exceso de longitud. No necesitas activar radio ni añadir un servicio remoto para comprobar las reglas. La extensión deberá servir al mismo intérprete utilizado por RFCOMM y GATT.

[Proyecto inicial — ZIP](../descargas/proyectos/redes-inicial.zip){ .md-button }

## Comprensión y diagnóstico

1. El transporte Wi-Fi está activo, pero HTTP devuelve 503. ¿Qué se sabe de la red y qué ha comunicado el servidor?
2. Un JSON contiene `mensajes: []`. Otro contiene `mensajes: "vacío"`. ¿Por qué no son equivalentes?
3. ¿Qué cambia entre un array de mensajes y un objeto con `mensajes` y `siguiente`? ¿Basta recibir ese metadato para afirmar que se cargaron todas las páginas?
4. Un dispositivo está vinculado, pero la conexión RFCOMM falla. Da dos causas compatibles con ese dato.
5. Una escritura de cuatro bytes llega al receptor en dos lecturas. ¿Qué condición permite reconstruir el mensaje sin asumir fronteras del transporte?
6. En Android 12 o posterior, ¿qué permisos corresponden a explorar, conectar y anunciar BLE? ¿Se necesitan al pulsar Cargar HTTP?
7. `connectGatt` devuelve un objeto. ¿Se puede leer la característica inmediatamente? Explica los resultados que deben llegar antes.
8. Una llamada de escritura devuelve que se aceptó iniciar. ¿Qué resultado falta para afirmar que terminó?
9. ¿Por qué el resultado de una exploración filtrada vacía no demuestra ausencia de todos los equipos BLE?
10. ¿Qué diferencia hay entre las pruebas con MockEngine, los tests del protocolo y una transferencia física entre dos teléfonos?

## Caso de integración

Dos equipos del aula ejecutan la solución. A anuncia el servicio BLE, pero B no lo encuentra. Propón una secuencia de comprobaciones que diferencie capacidad de anunciante, estado Bluetooth, permisos, servicio creado, duración del anuncio y filtro. Indica qué resultado observable confirma cada hipótesis. No amplíes la búsqueda a dispositivos desconocidos ni cambies todos los UUID por ensayo.

[Respuestas razonadas](soluciones-repaso.md)

[Solución ejecutable — ZIP](../descargas/proyectos/redes-solucion.zip){ .md-button }

`Protocolo.kt` contiene el comando adicional y `ConsolidacionTest.kt` comprueba que conserva las respuestas anteriores. Los tests son independientes de hardware, no una simulación presentada como transferencia radioeléctrica.
