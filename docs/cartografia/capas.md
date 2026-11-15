# Capas, polígonos y cámara

## Un mismo dato admite varias representaciones

En el tema principal un punto se representaba mediante una fuente GeoJSON y una capa de círculos. La ampliación utiliza también un **polígono** para una zona y una **línea** para una ruta. El SDK representa geometrías; no calcula automáticamente un itinerario válido al dibujar una línea entre dos puntos.

| Geometría | Datos necesarios | Capa de este laboratorio |
| --- | --- | --- |
| `Point` | Longitud y latitud | `CircleLayer` |
| `LineString` | Dos o más puntos ordenados | `LineLayer` |
| `Polygon` | Uno o más anillos cerrados | `FillLayer` y `LineLayer` |

Los [ejemplos oficiales de GeoJSON de MapLibre](https://maplibre.org/maplibre-native/android/examples/) muestran cómo separar fuente y representación. El proyecto conserva MapLibre Native 12.2.0 y las mismas versiones Android del itinerario; no necesitas sustituir todo el mapa para añadir una capa.

## Construir un polígono coherente

Un anillo exterior de un polígono vuelve a su primer punto. Un conjunto abierto de posiciones es una línea, no un área. Los anillos interiores pueden representar huecos; no se necesitan en la zona sencilla del laboratorio.

`cargarMapa()` genera un anillo propio de 72 segmentos alrededor del centro y lo aloja en la fuente `zona`. La conversión de metros a grados es una aproximación local para visualizar unos cientos de metros en Madrid. No debe extrapolarse a grandes distancias o polos. La pertenencia se calcula con Haversine, no leyendo píxeles del relleno.

Una fuente admite dos capas: una rellena el área y otra dibuja su borde. Este fragmento se ejecuta después de añadir la fuente `zona` al estilo:

```kotlin title="MainActivity.kt — capas del área"
nuevo.addLayer(FillLayer("zona-relleno", "zona").withProperties(
    fillColor("#2E7D32"),
    fillOpacity(0.20f)
))
nuevo.addLayer(LineLayer("zona-borde", "zona").withProperties(
    lineColor("#1B5E20"),
    lineWidth(2f)
))
```

El relleno deja ver lo que haya debajo. Su borde facilita reconocer la forma sin depender exclusivamente de un cambio tenue de color. Los IDs de capas son únicos; ambas apuntan a los mismos datos. El orden de incorporación influye en qué se dibuja encima: primero área y borde, después marcadores y ruta.

## Marcadores personalizados sin otra biblioteca

`punto(id, punto, color, radio)` es un ayudante del proyecto. Busca una fuente por ID; si existe, actualiza su GeoJSON. Si falta, crea fuente y capa. Permite representar centro, posición y búsqueda sin duplicar el mecanismo de actualización.

La personalización incluye tamaño, color y borde. El centro es mayor que la posición y existe texto que explica el resultado. Si necesitas un icono, un `SymbolLayer` referencia una imagen registrada en el estilo mediante su ID; no basta con pasar la ruta de un PNG como si fuera un recurso ya conocido por el motor. Conserva además un equivalente textual para quien no pueda interpretar el mapa visual.

Si una búsqueda nueva no devuelve lugar, elimina primero su capa con `removeLayer` y después su fuente con `removeSource`; no mantengas un punto antiguo como si respondiera a la nueva consulta. Retirar una fuente que todavía usa una capa invierte el orden de dependencias.

Al cambiar entre estilo remoto y local, el callback añade de nuevo fuentes y capas y representa los datos conservados en memoria. Un estilo es propietario de sus capas: obtener otro estilo no traslada automáticamente las del anterior.

## Mostrar una ruta recibida

`dibujarRuta` convierte una lista de `Punto` en `LineString`, añade la fuente `ruta` la primera vez y reutiliza `setGeoJson` en las siguientes. El servicio local entrega una geometría **sintética**; la línea sirve para probar el contrato y la representación, sin afirmar que se pueda circular por ese trazo.

Un error común consiste en mezclar GeoJSON longitud/latitud con `LatLng`, que utiliza latitud/longitud. Las pruebas de `RespuestasTest` incluyen coordenadas distintas en ambos ejes para detectar esa confusión. Otro error es dar por válida una ruta con un único punto: no puede representar un tramo.

## Cámara: incluir los datos que importan

Centrar un punto con un zoom fijo puede dejar fuera parte de una ruta. `LatLngBounds.Builder` reúne posiciones, y `newLatLngBounds` calcula una cámara que encuadre sus extremos con margen. En `encuadrar()` se incluye una caja alrededor de la zona y, si existe, cada punto de la ruta.

```kotlin title="Fragmento de encuadre con límites ya calculados"
mapa?.animateCamera(
    CameraUpdateFactory.newLatLngBounds(limites, 40)
)
```

El margen pertenece a la operación de cámara, no al radio de la geocerca. La vista debe tener tamaño antes de ajustar límites; el laboratorio lo invoca desde acciones y desde el mapa preparado. Si una búsqueda produce un lugar fuera de la zona, la cámara se acerca a ese resultado; **Encuadrar zona y ruta** permite volver al conjunto principal.
