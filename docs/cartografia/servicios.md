# Geocodificación y cálculo de rutas

## Convertir un nombre en coordenadas

**Geocodificar** busca posiciones a partir de un texto, por ejemplo un museo. La operación inversa convierte unas coordenadas en una dirección aproximada. Ninguna de ellas está garantizada por dibujar un mapa: son servicios distintos, con sus condiciones y errores.

El cliente del laboratorio entiende un contrato de búsqueda compatible con [Nominatim](https://nominatim.org/release-docs/latest/api/Search/). Envía `GET /search?format=jsonv2&limit=1&q=...`; recibe una lista. Un resultado contiene `lat` y `lon` como cadenas. Lista vacía significa que no se encontró un lugar; no equivale a HTTP 503 ni a JSON incompatible.

`Respuestas.lugar` transforma el primer elemento válido en `Punto`. La clase valida rangos; una latitud 95 no se coloca en el mapa. La función no conoce Compose ni modifica la cámara: esa responsabilidad pertenece a la Activity después de recibir el dato.

## Un servidor de aula reproducible

Los dos proyectos incluyen `servidor.py`. Es un servicio local de respuestas propias, sin consultar proveedores de Internet. Conserva en el emulador la base `http://10.0.2.2:8091` y ejecuta el servidor según su README. La búsqueda `Museo` devuelve un punto fijo; otros textos, una lista vacía; `ERROR`, HTTP 503; `ROTO`, una estructura incompatible.

Esto permite depurar sin depender de cambios remotos ni enviar información de la clase a un tercero. La app solicita consentimiento para consultar y solo envía tras pulsar. El texto no dispara búsquedas en cada carácter. Para dispositivo físico se necesita un servidor accesible o una redirección USB; `10.0.2.2` es una dirección especial del emulador.

## Elegir un proveedor real

No hay una dirección pública de geocodificación incorporada en la app. La base es configurable para una instancia propia o un proveedor cuyo contrato hayas aceptado. Para búsqueda y rutas puedes necesitar proveedores o bases diferentes: el servidor del laboratorio reúne ambos contratos únicamente para simplificar pruebas.

La [política de Nominatim público](https://operations.osmfoundation.org/policies/nominatim/) limita el tráfico agregado de la aplicación, exige identificación y atribución y prohíbe autocompletado y consultas sistemáticas. Incluye restricciones sobre servicios genéricos generados por plataformas de IA. No debe convertirse en el backend predeterminado de un aula ni se presupone permiso por disponer de una URL. Una pausa por teléfono no controla la suma de toda la clase. Para uso colectivo, elige servicio propio o proveedor adecuado y centraliza cuotas y caché.

La selección de un lugar puede revelar intereses o direcciones. No envíes búsquedas personales a un servicio de demostración. El laboratorio tampoco utiliza la posición GPS como origen de las consultas: los extremos de su ruta son públicos y fijos.

## Obtener una geometría de ruta

Un motor de rutas conoce una red transitable y un perfil, como automóvil o bicicleta. Puede devolver geometría, distancia, duración e instrucciones. El proyecto consume el contrato [Route de OSRM](https://project-osrm.org/docs/v5.24.0/api/#route-service): posiciones en longitud/latitud, separadas por punto y coma, y `geometries=geojson` para recibir una línea legible.

Una respuesta `code = "Ok"` contiene `routes`. Cada geometría debe ser una `LineString` con al menos dos puntos. `NoRoute`, lista sin rutas o datos incompatibles son errores para esta operación. Una respuesta HTTP correcta no basta: también hay que validar el contrato de negocio.

El servidor de aula devuelve tres puntos sintéticos propios. No calcula trayectos ni pasos de navegación. Un proveedor real puede cambiar resultados según perfil y datos disponibles; no reutilices sin comprobación una ruta de coche como ruta peatonal accesible.

## Asincronía, errores y caché

`ServicioCartografico` ejecuta HTTP en `Dispatchers.IO`. La Activity lanza una corrutina, activa carga y presenta resultado o error. Deshabilita consultas simultáneas y cancela la actualización al salir. Las lecturas HTTP tienen timeout; cancelar la corrutina evita actualizar la pantalla abandonada, pero una llamada bloqueante puede tardar hasta su timeout en finalizar.

El cliente no sigue redirecciones automáticamente, limita la respuesta a 65 536 caracteres y solo guarda en caché datos que superan la interpretación. Conserva hasta 16 respuestas en memoria, no en disco. HTTP sin cifrado solo se admite para loopback o host del emulador en debug; fuera de esos casos exige HTTPS. No se incluyen credenciales en la app.

Este fragmento preparado ilustra la operación completa de una ruta: espera datos, representa y actualiza el mensaje, sin hacer HTTP dentro de la composición:

```kotlin title="MainActivity.consultar — rama de ruta"
puntosRuta = servicio.ruta(base)
dibujarRuta()
encuadrar()
red = getString(R.string.ruta_lista)
```

La práctica utiliza esta rama resuelta como patrón para implementar búsqueda. El caso vacío y el error deben conservar mensajes distintos. Para contrastar red, `test_servidor.py` comprueba seis respuestas por HTTP loopback; los tests Kotlin comprueban interpretación. Ninguno sustituye la prueba de integración desde Android.
