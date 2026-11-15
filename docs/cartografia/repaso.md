# Consolidación y repaso de cartografía

## Ejercicio autónomo: distancia de un recorrido

Una aplicación de paseos recibe una secuencia ordenada de posiciones y necesita informar de su longitud aproximada. El total debe sumar los tramos consecutivos, sin unir el final de nuevo con el principio. Una lista vacía o con un solo punto tiene longitud cero. No se deben eliminar puntos repetidos: un tramo entre dos posiciones iguales aporta cero.

Trabaja con Kotlin y los tipos de coordenadas del proyecto inicial. No realices peticiones de rutas ni guardes coordenadas personales. Utiliza estos datos públicos:

| Punto | Longitud | Latitud |
| --- | --- | --- |
| A | −3.7038 | 40.4168 |
| B | −3.7000 | 40.4188 |
| C | −3.6900 | 40.4200 |

La lista A → B → C debe medir más que la distancia directa A → C. Repetir B en medio no debe aumentar el total. Define y prueba también los casos vacío, un punto y dos puntos iguales. Explica qué significa la medida y por qué no acredita un recorrido transitable por calles.

[Proyecto inicial — ZIP](../descargas/proyectos/cartografia-inicial.zip){ .md-button }

## Preguntas de comprensión

1. El mapa sigue mostrando una posición después de pulsar Inicio del sistema. ¿Demuestra que continúa la suscripción? ¿Qué observarías para comprobarlo?
2. Con radio 500 m, ¿qué resultados corresponden a 490 ± 10, 510 ± 10 y 511 ± 10 m? ¿Qué añade la precisión al dato de distancia?
3. Una persona mueve mucho la cámara y la app crea otra fuente `posicion` en cada lectura. ¿Qué identidad debería mantenerse y qué dato debería cambiar?
4. En modo avión se sigue viendo parte de una ciudad visitada antes. ¿Qué prueba falta para anunciar que el mapa funciona offline?
5. Una ruta devuelve HTTP 200 y `code: NoRoute`. ¿Debe dibujarse como éxito? ¿Qué ocurre si el JSON solo contiene un punto?
6. ¿Qué datos envía el servidor de laboratorio y cuáles envía una consulta a un proveedor real? ¿Se envía la ubicación del teléfono como origen de la ruta del ejemplo?
7. Un alumno configura el servidor público Nominatim en todos los teléfonos y limita cada uno a una consulta por segundo. ¿Ha controlado el tráfico agregado y aceptado las condiciones del servicio?
8. ¿Qué trabajo nuevo exige convertir la geocerca local de pantalla en geofencing que recibe transiciones con el proceso detenido?
9. ¿Por qué incorporar MapLibre Compose no resuelve por sí mismo el proveedor, las cuotas o la autorización de uso offline?

## Diagnóstico

La aplicación muestra una geocerca correcta en su esquema local, pero el mapa remoto no carga y la búsqueda informa de un error. Explica cómo distinguirías un fallo de teselas, un fallo del servidor de consultas y una denegación de ubicación. Indica una acción y un resultado observable para cada hipótesis, sin borrar cachés ni cambiar todas las dependencias.

[Soluciones de repaso](soluciones-repaso.md)

[Solución ejecutable de métricas — ZIP](../descargas/proyectos/cartografia-solucion.zip){ .md-button }

Dentro del proyecto solución, `MetricaRecorrido.kt` resuelve el cálculo y `MetricaRecorridoTest.kt` comprueba los casos. Es código de consolidación separado del inicial; no modifica la recepción de ubicaciones ni realiza comunicación externa.
