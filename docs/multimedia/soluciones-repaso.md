# Soluciones del repaso

1. Photo Picker permite elegir contenido concreto sin autorización general de galería. El callback entrega una URI o `null` si no hay selección; no devuelve un Bitmap ni una ruta de disco garantizada.
2. Hay que crear y conservar la URI de destino antes de lanzar la cámara. El booleano confirma que la captura se guardó allí; después se lee ese contenido.
3. 6000 × 4000 × 4 bytes son aproximadamente 96 MB solo para píxeles. Las copias aumentan el consumo. Se elige un tamaño de decodificación antes de materializar la imagen completa.
4. La orientación EXIF puede indicar giros y reflejos. Deben tratarse sus ocho posibilidades en la rama compatible; no aplicar dos veces una corrección que ImageDecoder ya realizó.
5. Con ImageDecoder el tamaño es 1024 × 512 y, tras girar, 512 × 1024. La rama API26–27 elige una muestra de 4: obtiene 600 × 300 y gira a 300 × 600. Ambas conservan proporción y respetan el máximo; el muestreo puede reducir más que el tamaño exacto. Si se exporta ese bitmap, esas son las dimensiones del archivo. Los bytes dependen también del contenido y del formato.
6. Falta recodificar los píxeles como JPEG. Se pueden inspeccionar cabecera y MIME decodificado, y reabrir el resultado con un decodificador; renombrar no basta.
7. JPEG no conserva alfa: se compone sobre un fondo opaco elegido. Guardar después como PNG evita pérdidas nuevas de ese formato, pero no reconstruye información previamente eliminada.
8. Autoridad y rutas de FileProvider, URI `content://`, MIME, `EXTRA_STREAM`, ClipData y concesión temporal de lectura. El receptor no puede abrir automáticamente una ruta privada de otra app.
9. Puede estar preparado y pausado. Pausar conserva posición; detener deja la preparación inactiva; liberar devuelve recursos y hace inutilizable esa instancia. La app decide si al detener también vuelve a cero.
10. Los eventos del Player y `isPlaying`. El clic expresa una intención, no garantiza apertura, búfer suficiente ni reproducción efectiva.
11. Permiso denegado, proveedor desactivado o lectura sin resultado. Primero comprobar carga del estilo y del lugar fijo; después permiso/proveedor y coordenadas simuladas. La ausencia de posición no implica que falten teselas.
12. `LatLng` recibe latitud/longitud y un punto GeoJSON usa longitud/latitud. Además de los rangos válidos, una coordenada pública conocida permite detectar el intercambio.
13. Autorización global de la app, canal y su importancia/configuración, identificador del aviso y restricciones de presentación del sistema. Un canal bloqueado no se desbloquea pidiendo de nuevo el permiso. Compilar no prueba publicación ni apertura.
14. La relación con el ciclo de vida: liberar el reproductor y cancelar la solicitud al abandonar su propietario. Los callbacks tardíos deben evitar actualizar vistas destruidas.
15. Ejemplo emulado: enviar coordenadas conocidas y verificar que el marcador y el texto coinciden. Ejemplo físico: capturar una fotografía con una cámara real y abrir su conversión en otra app, registrando modelo, API, orientación y resultado. Ninguna prueba sustituye a la otra.
