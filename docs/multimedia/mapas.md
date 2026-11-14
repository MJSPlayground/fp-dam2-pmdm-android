# Cartografía con OpenStreetMap y MapLibre

## Tres responsabilidades diferentes

Un mapa necesita datos, un servicio que los entregue y un motor que los dibuje. **OpenStreetMap (OSM)** es una base de datos cartográfica colaborativa. **OpenFreeMap** es el proveedor de estilo y teselas escogido para el laboratorio. **MapLibre Native** es el SDK Android que los representa. Usar datos OSM no significa que todas las teselas procedan de los servidores públicos de OSM.

El estilo describe colores, capas, etiquetas y fuentes. Las teselas dividen el territorio en porciones que se solicitan según cámara y zoom. El laboratorio usa el estilo Liberty de la [guía de OpenFreeMap](https://openfreemap.org/quick_start/). Se requiere Internet y el permiso normal `INTERNET`; no una clave privada. Mostrar el mapa por sí solo no requiere permiso de localización.

Mantén los controles de atribución del SDK y los créditos del proveedor y los datos. El proyecto añade también una leyenda visible. No utilices el servidor como repositorio para descargas masivas. Las condiciones del proveedor pueden variar: la ampliación de mapas offline debe comprobarlas para ese uso específico. Los [derechos de OSM](https://www.openstreetmap.org/copyright) y la [política de teselas del servicio público de OSM](https://operations.osmfoundation.org/policies/tiles/) son referencias diferentes; esa última no convierte a OpenFreeMap en el mismo servicio.

## Alojar MapView dentro de Compose

El proyecto fija MapLibre Native Android 12.2.0. Su `MapView` es una vista Android, por lo que `AndroidView` la incorpora en una pantalla Compose. La [guía de interoperabilidad](https://developer.android.com/develop/ui/compose/migrate/interoperability-apis/views-in-compose) explica ese puente.

`MainActivity` crea **una sola vista por instancia de Activity** después de inicializar MapLibre. La factoría de `AndroidView` devuelve esa misma vista; no construye otro mapa al cambiar el texto de estado. La vista tiene una altura acotada y los botones de la pantalla pueden desplazarse incluso con fuente grande.

El SDK conserva recursos nativos y debe conocer el ciclo de vida. El proyecto reenvía `onCreate`, `onStart`, `onResume`, `onPause`, `onStop`, `onDestroy`, guardado de estado y aviso de memoria. Este trabajo está preparado para centrarse en datos y permisos; no lo borres al implementar los TODO. Consulta los [ejemplos oficiales Android](https://maplibre.org/maplibre-native/android/examples/) y el [código de la versión fijada](https://github.com/maplibre/maplibre-native/tree/android-v12.2.0/platform/android).

## Esperar al mapa y a su estilo

Hay dos preparaciones asíncronas: `getMapAsync` entrega el controlador `MapLibreMap`; cargar el estilo tiene su propio callback. Añade fuentes y capas dentro del callback del estilo, donde existe el destino que las alojará. El laboratorio distingue **cargando cartografía**, **mapa preparado** y **error** y permite recargar.

Este fragmento muestra la secuencia; `vista` es el `MapView` ya creado:

```kotlin
vista.getMapAsync { mapa ->
    mapa.setStyle(Style.Builder()
        .fromUri("https://tiles.openfreemap.org/styles/liberty")) { estilo ->
        // Aquí el estilo ya admite las fuentes y capas del laboratorio.
    }
}
```

Si la cartografía no carga, pedir GPS no corrige el estilo. Revisa conexión y dirección del proveedor. Si recargas un estilo, sus fuentes y capas anteriores dejan de pertenecer al nuevo: hay que incorporarlas otra vez, como hace el callback preparado.

## Datos de un punto y apariencia de su marcador

Un marcador sencillo se construye con una fuente GeoJSON y una capa. La fuente responde **dónde**; la capa, **cómo se dibuja**. El ejemplo preparado sitúa un lugar público en Madrid. No es la posición de quien utiliza la aplicación.

```kotlin title="Dentro del callback del estilo — ejemplo resuelto"
val punto = Point.fromLngLat(-3.7038, 40.4168)
estilo.addSource(GeoJsonSource("lugar", punto))
estilo.addLayer(CircleLayer("lugar-circulo", "lugar").withProperties(
    circleRadius(10f),
    circleColor("#1B5E20"),
    circleStrokeColor("#FFFFFF"),
    circleStrokeWidth(3f)
))
```

Los identificadores relacionan capa y fuente. `"lugar-circulo"` es el ID de la capa y `"lugar"`, el de sus datos. No tienen que coincidir; el segundo argumento de `CircleLayer` sí debe referirse a una fuente existente. Los imports y la clase completa están en el proyecto.

GeoJSON utiliza **longitud, latitud**. `LatLng`, utilizado para la cámara, recibe **latitud, longitud**. Invertir el orden puede producir un punto válido en una región equivocada: no siempre genera una excepción.

## Actualizar un punto sin duplicar su fuente

Cuando llega una segunda lectura de ubicación, conserva la identidad de la fuente y modifica su contenido mediante `setGeoJson`. Antes de crearla, utiliza `getSourceAs<GeoJsonSource>("posicion")`: devuelve la fuente o `null` si todavía no existe. Crea fuente y capa solo la primera vez. Recargar el estilo reinicia este conjunto.

La posición utiliza un círculo menor que el del lugar fijo, además de otro color. El texto muestra cuál es cada uno y las coordenadas; el color no es la única diferencia. El mapa visual no sustituye la información textual para una persona que no pueda verlo.

Para centrar una cámara se utiliza el controlador del mapa:

```kotlin title="Fragmento de cámara — controlador mapa ya preparado"
mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(
    LatLng(40.4168, -3.7038), 13.0
))
```

El zoom es una escala, no una distancia en metros. Acercar la cámara no hace más precisa una lectura GPS. En la [práctica guiada](practica-mapa.md) reutilizarás el patrón de marcador y cámara con la posición obtenida, gestionando también denegación y falta de proveedor.
