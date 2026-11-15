# Soluciones de repaso de cartografía

## Distancia de un recorrido

La solución ejecutable está en `MetricaRecorrido.kt`, dentro del [proyecto solución](../descargas/proyectos/cartografia-solucion.zip). Suma únicamente parejas consecutivas usando la distancia geodésica del laboratorio. Las listas de tamaño cero o uno no contienen ningún tramo y dan cero. Una repetición consecutiva añade un tramo de longitud cero.

La prueba comprueba que A → B → C supera el tramo directo A → C y que repetir B mantiene el total. Es la longitud de una polilínea aproximada sobre la superficie terrestre. Los puntos no describen necesariamente el trazado de una calle; tampoco incluyen restricciones de circulación, accesibilidad o seguridad.

## Respuestas razonadas

1. No. El último punto puede permanecer en memoria aunque se haya retirado el receptor. Comprueba que al volver el botón ofrece Iniciar y que no se reciben actualizaciones nuevas hasta pulsarlo. Una prueba de consumo necesita dispositivo físico y medidas apropiadas.
2. Dentro, Incierta y Fuera, respectivamente. En el segundo caso el intervalo alcanza el borde. Ignorar precisión daría una certeza que el dato no justifica. La regla del laboratorio no convierte ese intervalo en garantía de seguridad.
3. La fuente conserva su ID y la capa sigue apuntando a ella. Cambia el contenido GeoJSON mediante `setGeoJson`; no se crea otra fuente con ese ID. Un nuevo estilo sí requiere reconstruir sus fuentes y capas.
4. Puede ser caché ambiental parcial. Hace falta una región completa y autorizada, comprobar estado de descarga, cerrar el proceso y probar sin red dentro de todos sus límites y zoom, incluyendo etiquetas e imágenes. El esquema local del proyecto es otra estrategia: representa geometrías propias, sin calles ni teselas.
5. No. El contrato también debe indicar éxito y aportar una `LineString` con al menos dos puntos. HTTP 200 solo describe la respuesta HTTP; no certifica que exista un itinerario utilizable.
6. El servidor incluido devuelve datos sintéticos y no consulta otros proveedores. El cliente envía el texto de búsqueda o los extremos públicos fijos de la ruta; no añade la posición GPS. Un proveedor externo recibe lo que se envía en su consulta y aplica sus políticas. La casilla no sustituye revisar privacidad y condiciones.
7. No. El límite individual no controla la suma de usuarios. El servicio público tiene condiciones que deben verificarse y no constituye un backend genérico predeterminado. Para aula usa el servidor sintético, una instancia propia o un proveedor adecuado con control conjunto de tráfico.
8. Definir necesidad, permisos y restricciones de la versión, registrar regiones en un servicio compatible, recibir transiciones, gestionar expiración/retirada/reinicio y comprobar precisión y latencia reales. El cálculo local deja de ejecutarse al salir y no acredita ese funcionamiento.
9. El envoltorio cambia la integración de UI, no la licencia de datos ni el servicio que los entrega. Además hay que comprobar API, runtime, compatibilidad y funciones que se usan antes de migrar.

## Diagnóstico separado

- **Teselas o estilo:** el esquema local funciona; al activar remoto aparece el error de cartografía. Con red disponible, revisa estilo y proveedor sin solicitar GPS para corregirlo.
- **Servicio de consultas:** conserva el mapa local, consulta `Museo` contra el servidor incluido y después `ERROR`. Deben diferenciarse marcador correcto y error. Si el servidor está parado, otra consulta no cacheada debe fallar.
- **Permiso de ubicación:** la simulación sigue funcionando. Solicita seguimiento y deniega: aparece el aviso de permiso y el mapa permanece utilizable. Una búsqueda vacía no se corrige concediendo ubicación.

Las pruebas del servidor y de interpretación ayudan a localizar el contrato que falla. La prueba desde la interfaz verifica que carga, estado y representación se conectan realmente; no sustituye las comprobaciones físicas pendientes.
