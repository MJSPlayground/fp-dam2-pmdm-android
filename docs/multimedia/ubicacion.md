# Ubicación puntual del dispositivo

## Una posición incluye incertidumbre

Un mapa puede mostrar Madrid sin saber dónde está el teléfono. La **localización** es una operación distinta: intenta estimar la posición de ese dispositivo. GNSS es el conjunto de sistemas de navegación por satélite; GPS es uno de ellos. Android también puede combinar información de redes y otros proveedores. Estar conectado a Internet no implica tener una lectura GNSS disponible, y el emulador no demuestra la recepción de satélites de un teléfono.

Una `Location` contiene latitud, longitud, instante y, cuando existe, una estimación de precisión en metros. Una precisión de 150 m no significa que la persona está exactamente sobre el punto dibujado. En interiores o al iniciar una lectura puede no haber posición. Nunca sustituyas una posición desconocida por `(0, 0)`: ese par representa un lugar real.

La [guía de localización de Android](https://developer.android.com/develop/sensors-and-location/location) distingue frecuencia, precisión y consumo. En este tema pediremos **una lectura mientras la pantalla está visible**. No iniciamos seguimiento, servicio ni ubicación en segundo plano.

## Permiso aproximado y preciso

`ACCESS_COARSE_LOCATION` autoriza ubicación aproximada; `ACCESS_FINE_LOCATION` permite solicitar más precisión. Se declaran en el manifiesto y se comprueban en ejecución. En Android moderno, al solicitar precisión, pide ambos en la misma operación. La persona puede conceder únicamente aproximada, y la aplicación debe seguir funcionando con esa decisión.

El proyecto registra `ActivityResultContracts.RequestMultiplePermissions` como propiedad de la Activity. Esto prepara un receptor de respuesta sin abrir todavía el diálogo. Al pulsar **Obtener mi posición**, comprueba si ya dispone de permiso y, si falta, lanza el contrato. Su callback decide si puede empezar la lectura. No pidas permisos desde el cuerpo de un composable: una recomposición no es una petición del usuario.

Este fragmento de la Activity solicita ambos permisos; no configura seguimiento:

```kotlin title="MainActivity.kt — fragmento dentro de una acción de usuario"
permisos.launch(arrayOf(
    Manifest.permission.ACCESS_COARSE_LOCATION,
    Manifest.permission.ACCESS_FINE_LOCATION
))
```

Si se deniega, se informa y se conserva el mapa del lugar de referencia. No reabras automáticamente el diálogo. Tras denegaciones reiteradas, el sistema puede no mostrarlo otra vez: los ajustes del dispositivo son la vía para cambiar esa decisión. Consulta [solicitud de permisos de ubicación](https://developer.android.com/develop/sensors-and-location/location/permissions/runtime).

## Leer con LocationManagerCompat

El laboratorio utiliza el servicio `LocationManager` de Android y el adaptador AndroidX [`LocationManagerCompat.getCurrentLocation`](https://developer.android.com/reference/androidx/core/location/LocationManagerCompat). No depende de Google Play Services. El adaptador recibe:

| Entrada | Para qué sirve |
| --- | --- |
| Gestor de ubicación | Acceder a los proveedores del sistema |
| Nombre de proveedor | Elegir GPS o red entre los disponibles y autorizados |
| `CancellationSignal` | Cancelar si se abandona la pantalla |
| Executor | Decidir dónde se entrega el callback; aquí, hilo principal |
| Callback | Recibir una `Location` o `null` |

La clase `UbicacionActual`, preparada en el proyecto, comprueba de nuevo los permisos antes de llamar al sistema. Con permiso preciso busca GPS y, si no está activo, red. Con aproximado intenta red. **No todos los dispositivos tienen un proveedor de red**: en ese caso comunica ausencia de proveedor; no fuerza GPS después de una autorización aproximada.

La lectura es asíncrona. El botón debe mostrar un estado de búsqueda y evitar solicitudes simultáneas. El resultado no vuelve mediante el valor de retorno de `obtener`, sino mediante sus funciones de respuesta. La pantalla puede recibir `null`, por ejemplo al agotarse la lectura sin una posición. Tampoco debe confundirlo con haber recibido una coordenada válida.

Este fragmento ilustra cómo interpretar una posición ya recibida, dentro de un callback donde `posicion` tiene tipo `Location?`:

```kotlin
if (posicion == null) {
    estadoUbicacion = R.string.sin_posicion
} else {
    coordenadas = getString(R.string.coordenadas,
        posicion.latitude, posicion.longitude, posicion.accuracy)
}
```

`getString` usa los formatos regionales del dispositivo. Las coordenadas se muestran para comprobar el laboratorio, sin guardarlas en disco, registros públicos ni servicios externos. El marcador tampoco representa una autorización para compartirlas.

## Cancelación y ciclo de vida

Conserva la señal de la solicitud activa. Antes de iniciar otra, cancela la anterior. En `onStop`, cuando la Activity deja de ser visible, llama a `cancelar` y restaura el estado del botón. El ayudante solo entrega el resultado si la señal sigue siendo la actual; evita que un callback tardío cambie una pantalla abandonada.

Un permiso puede revocarse entre comprobación y lectura. Por eso el ayudante captura `SecurityException`, además de comprobar el permiso. La comprobación previa mejora el flujo, pero no elimina los fallos que puede comunicar el sistema.

## Simular y después probar físicamente

En **Extended controls → Location** del emulador envía una posición conocida, activa ubicación y solicita la lectura desde la app. Una posición simulada permite contrastar latitud, longitud y marcador con datos controlados. Deniega después el permiso y desactiva el proveedor para observar caminos diferentes.

En un teléfono comprueba además primera lectura real, interior/exterior y precisión aproximada/precisa. Si no se dispone de él, esa parte queda pendiente. No registres coordenadas personales en capturas o informes públicos.

La [práctica del mapa](practica-mapa.md) conecta esta lectura con una representación cartográfica sin convertirla en seguimiento continuo.
