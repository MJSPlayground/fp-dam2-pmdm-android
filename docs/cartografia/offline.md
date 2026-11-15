# Caché, mapas sin conexión y alternativas Compose

## Una caché no garantiza un mapa offline

La caché ambiental conserva recursos descargados durante el uso normal. Puede ahorrar solicitudes al volver a una zona, pero no demuestra que existan todos los zoom, calles, glifos e imágenes necesarios. Puede vaciarse o invalidarse. Apagar Wi-Fi después de mirar una zona prueba lo que quedó disponible en ese dispositivo, no un paquete offline completo.

La caché de respuestas de búsqueda del laboratorio es otra cosa: guarda JSON interpretado en memoria. No almacena teselas y desaparece con el proceso. Distinguirlas evita diagnosticar una ruta vacía como si faltaran imágenes de cartografía.

## Un esquema local que funciona desde el inicio

**Zona de paseo** arranca con un estilo JSON propio, sin fuentes de teselas, sprites ni glifos remotos. Añade la zona, el centro y las geometrías conservadas en memoria. Puedes probarlo en modo avión desde un inicio limpio; no depende de haber abierto previamente Madrid.

```json title="Estilo local incorporado en MainActivity — formato abreviado"
{
  "version": 8,
  "name": "Esquema local",
  "sources": {},
  "layers": [{
    "id": "fondo", "type": "background",
    "paint": { "background-color": "#E8F5E9" }
  }]
}
```

Es un **esquema sin calles**, identificado así en pantalla. No es una descarga offline de OpenStreetMap. Sirve para conservar la representación propia cuando no se dispone de un mapa base y para probar geometrías sin generar tráfico. El botón de cartografía remota carga otro estilo expresamente; volver a local reconstruye sus capas con los datos en memoria.

Una estrategia real puede combinar este estado de respaldo con un paquete de datos autorizado. Si falta información, indícalo: mostrar solo una geocerca no autoriza a prometer orientación por calles.

## Preparar una región offline autorizada

MapLibre Native ofrece [`OfflineManager`](https://maplibre.org/maplibre-native/android/api/-map-libre%20-native%20-android/org.maplibre.android.offline/-offline-manager/index.html) y una definición de región por estilo, límites, zoom y densidad. El flujo es: definir área y rango, crear región, observar progreso/errores, activar descarga y gestionar su eliminación. El estado completado debe acreditarse antes de anunciar disponibilidad sin red.

No basta con guardar las teselas visibles: el estilo puede necesitar otras fuentes, iconos y tipografía. Acotar zoom importa: cada nivel puede multiplicar notablemente los recursos de un área. Muestra progreso, tamaño y cancelación, y ofrece borrar el paquete; no descargues en cada arranque.

La [guía del proveedor OpenFreeMap](https://openfreemap.org/quick_start/) permite usos offline dentro de límites concretos de tamaño, paralelismo e identificación. Deben contrastarse para el producto y la versión empleados antes de activar una descarga. La app educativa no inicia ninguna descarga de región ni certifica paquetes remotos. Si necesitas mayores áreas, negocia el servicio o sirve datos propios. La política del servidor público OSM es independiente de las condiciones de otro proveedor.

Para verificar un paquete real: descarga una región pequeña autorizada, registra sus límites y zoom, detén el proceso, desactiva red y ábrelo de nuevo. Comprueba bordes, zoom mínimo/máximo, etiquetas y marcadores. Después elimina el paquete y verifica que no sigues anunciándolo como instalado. Documenta procedencia, atribución y política de actualización de sus datos.

## Native con AndroidView frente a MapLibre Compose

| Criterio | Native + AndroidView usado aquí | MapLibre Compose |
| --- | --- | --- |
| Integración | Vista Android con ciclo de vida reenviado | API declarativa orientada a Compose Multiplatform |
| Datos y capas | Controlador, estilo, fuentes y callbacks | Estado y elementos declarativos según su API |
| Continuidad | Reutiliza el laboratorio y versión ya comprobados | Requiere comprobar dependencias y migrar integración |
| Plataformas | Android en este proyecto | Varias plataformas, con diferencias de soporte |

El [proyecto oficial de MapLibre Compose](https://github.com/maplibre/maplibre-compose) advierte que su API sigue evolucionando y que cambios menores pueden romper código. Su [guía de instalación](https://maplibre.org/maplibre-compose/getting-started/) define también el runtime Android del motor; no basta con cambiar el import de `MapView` por un composable.

Para decidir, crea una copia independiente y contrasta las funciones que necesitas: capas, imágenes, cámara, accesibilidad, offline y ciclo de vida. Fija una combinación compatible, compila y repite los mismos casos observables. Este tema conserva Native 12.2.0 y no presenta como probada una migración que no se ha ejecutado. El proveedor de teselas y sus condiciones siguen siendo una decisión separada cualquiera que sea el envoltorio de UI.
