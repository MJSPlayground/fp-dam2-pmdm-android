# Seguimiento, incertidumbre y geocercas

## De una lectura a una secuencia

Una lectura puntual termina al entregar una posición o informar de que no la obtuvo. El **seguimiento** mantiene una suscripción: cada actualización puede cambiar un marcador, una distancia o el estado de una zona. Una suscripción olvidada sigue consumiendo recursos y puede revelar posiciones después de que la persona deje de usar la función.

El proyecto **Zona de paseo** utiliza `LocationManager`, sin Google Play Services. El ayudante `Seguimiento` entrega cada `Location` a una lambda de la Activity. La pantalla conserva una posición actual, sin crear un historial en disco. Se solicita una cadencia de cinco segundos y cinco metros; son parámetros de solicitud, no una promesa de una posición exacta cada cinco segundos.

Este fragmento de `Seguimiento.iniciar`, ya incluido en el proyecto, registra un receptor con permisos comprobados previamente:

```kotlin title="Seguimiento.kt — dentro de iniciar"
gestor.requestLocationUpdates(
    proveedor,
    5000L,
    5f,
    receptor,
    Looper.getMainLooper()
)
```

`proveedor` es GPS si hay precisión autorizada y está activo; con permiso aproximado se intenta red. `receptor` implementa `LocationListener`. El Looper del hilo principal permite comunicar cambios a la interfaz. Si no existe proveedor adecuado o se revoca la autorización, se informa y se desactiva el seguimiento. Una localización aproximada no autoriza a forzar un proveedor preciso.

## Ciclo de vida y consumo

El botón inicia la escucha; el mismo botón la detiene. La Activity llama también a `detener()` en `onStop`, cuando deja de ser visible, y en `onDestroy`. El ayudante conserva el receptor para retirarlo con `removeUpdates`: crear otro receptor al detener no eliminaría el anterior.

No se reinicia automáticamente al volver: la persona decide iniciar otra sesión. Esto hace observable la responsabilidad del ciclo de vida. La pantalla puede seguir mostrando el último resultado en memoria, pero ese dato no significa que exista una escucha activa. Gira, abre otra aplicación y vuelve: el control debe ofrecer Iniciar.

Un intervalo menor, más precisión y desplazamiento continuo suelen aumentar el trabajo del dispositivo. Elige parámetros según la necesidad: consultar una dirección no requiere seguir un trayecto. Tampoco compares batería entre una posición simulada en emulador y un teléfono buscando satélites.

## Distancia y precisión

`Geometria.distancia(a, b)` aproxima la distancia sobre una esfera con la fórmula de Haversine. Convierte grados a radianes y utiliza un radio terrestre de 6 371 000 m. El resultado es una distancia geodésica aproximada; no es la longitud de una ruta por calles. El modelo evita introducir un servicio remoto para saber si un punto está cerca de un centro.

La estimación de precisión de una lectura impide tratar el punto como una certeza. Si la distancia estimada es `d` y la precisión es `p`, el laboratorio compara el intervalo `[d − p, d + p]` con un radio `r`:

| Condición | Interpretación del laboratorio |
| --- | --- |
| `d + p <= r` | Dentro: incluso el extremo más lejano cabe en la zona |
| `d − p > r` | Fuera: incluso el extremo más cercano queda fuera |
| Cualquier otro caso | Incierta: el intervalo toca o cruza el borde |

Por ejemplo, con radio 500 m, distancia 490 m y precisión 10 m se informa Dentro. Con distancia 510 m y la misma precisión, el intervalo toca el borde: Incierta. Con 511 m, Fuera. Esta regla **didáctica** no convierte la precisión estimada en una garantía estadística ni basta para decisiones de seguridad.

El proyecto valida que coordenadas, distancia, precisión y radio sean finitos y estén dentro de su dominio. Un `NaN` podría propagarse sin lanzar una excepción matemática; no debe convertirse silenciosamente en una decisión de pertenencia.

## Geocerca local y geofencing del sistema

Una **geocerca** define una región y detecta pertenencia o cambios de entrada/salida. En el laboratorio se calcula la pertenencia de cada lectura mientras la pantalla está visible. No registra geocercas de sistema ni emite transiciones cuando el proceso está parado.

El [geofencing de Android con servicios de ubicación](https://developer.android.com/develop/sensors-and-location/location/geofencing) es otra integración: registra regiones, recibe transiciones mediante PendingIntent y requiere diseñar permisos, expiración, retirada y tratamiento de eventos. No entrega un evento en cada paso sobre un borde geométrico exacto. Necesita pruebas reales de latencia, disponibilidad y precisión; una comparación numérica en JVM no lo acredita.

## Ubicación en segundo plano: un requisito distinto

La [documentación de acceso en segundo plano](https://developer.android.com/develop/sensors-and-location/location/background) exige justificar que la función lo necesita y comunicarlo claramente. En versiones modernas hay un permiso específico y restricciones de entrega y ejecución. Un servicio en primer plano con notificación visible y ubicación de fondo no son mecanismos intercambiables: su inicio, tipos de servicio y autorizaciones dependen de la versión objetivo.

Antes de ampliar la app a ese comportamiento, define: qué beneficio requiere continuar al salir, cuánto tiempo, qué se conserva, cómo se detiene y qué ocurre si se revoca el permiso. La implementación de este tema no declara `ACCESS_BACKGROUND_LOCATION`, no usa un servicio y no atribuye seguimiento de fondo al hecho de que el marcador mantenga el último punto.
