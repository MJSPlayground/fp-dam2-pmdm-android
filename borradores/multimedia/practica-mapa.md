# Práctica: mapa de lugares

## Abrir el laboratorio

Lee [ubicación](ubicacion.md) y [mapas](mapas.md). Necesitas conexión para cargar el estilo y las teselas del proveedor del ejemplo. La aplicación no necesita una clave privada ni Google Play Services.

[Proyecto inicial — ZIP](../descargas/proyectos/multimedia-mapa-inicial.zip){ .md-button .md-button--primary }

Abre el proyecto y ejecuta **app**. Identifica el espacio del mapa, las acciones para centrar y obtener posición, y la información de estado. La ubicación real solo se pide al activar esa función; mostrar un mapa no requiere por sí mismo permiso de localización.

La práctica utiliza tres piezas con responsabilidades diferentes: **OpenStreetMap** aporta datos; **OpenFreeMap** sirve el estilo y las teselas; **MapLibre Native** los representa en Android. Conservar esta distinción evita atribuir al SDK las condiciones del servidor de mapas.

## Preparar MapView y su ciclo de vida

En `mapa-ciclo`, inicializa MapLibre y la vista antes de utilizarla. La Activity conserva MapView y lo aloja mediante `AndroidView`. Reenvía creación, inicio, reanudación, pausa, parada y destrucción, además de guardado de estado y aviso de memoria cuando corresponda.

Este código de ciclo de vida es parte del funcionamiento del mapa: no es una decoración de la interfaz. Omitir la pausa o la destrucción puede dejar recursos nativos activos aunque la pantalla desaparezca. Crear otro MapView en cada recomposición produce problemas similares.

Ejecuta y cambia a otra aplicación antes de volver. La vista debe seguir siendo utilizable. Gira también el emulador y revisa que no aparezcan dos mapas ni se acumulen vistas antiguas.

## Cargar el estilo y centrar

En `mapa-estilo`, solicita el mapa de forma asíncrona y carga `https://tiles.openfreemap.org/styles/liberty`. Espera al resultado del estilo antes de añadir sus fuentes y capas. Un mapa todavía no preparado no tiene por qué aceptar todas las operaciones de estilo.

Centra la cámara en las coordenadas públicas del ejemplo con una escala adecuada para reconocer calles. En MapLibre, `LatLng` se construye con latitud y longitud; un punto GeoJSON utiliza longitud y latitud. Comprueba cada contrato antes de copiar dos números en orden inverso.

La pantalla debe distinguir carga y error de estilo. Si no aparece cartografía, comprueba conexión, dirección del estilo y mensajes antes de solicitar permisos GPS: una denegación de ubicación no explica necesariamente que falten las teselas.

Mantén visibles los controles de atribución. No elimines los créditos de OSM/OpenMapTiles para ganar espacio. El servicio del ejemplo no garantiza disponibilidad permanente; su [guía y condiciones](https://openfreemap.org/quick_start/) se consultan antes de reutilizarlo con otros patrones de uso.

## Representar un lugar

En `mapa-marcador`, crea un punto GeoJSON y una fuente para los lugares. Una capa de círculos representa ese punto con tamaño y colores visibles. La fuente contiene los datos; la capa decide su apariencia. No es necesario incorporar una biblioteca de anotaciones diferente para un marcador sencillo.

Comprueba que el lugar aparece al terminar de cargar el estilo y permanece al mover la cámara. Repite con otras coordenadas públicas conocidas. Si el punto aparece en otra región, revisa el orden longitud/latitud de GeoJSON y los valores válidos.

No vuelvas a crear una fuente con el mismo identificador en cada actualización: conserva su identidad y actualiza su contenido cuando cambie la posición. Al volver a cargar un estilo, vuelve a añadir los elementos que pertenecían al anterior siguiendo el callback correspondiente.

## Obtener una posición puntual

En `mapa-ubicacion`, conecta el botón con la solicitud contextual de permisos. Solicita los permisos de ubicación en el grupo apropiado y acepta que la persona autorice solo precisión aproximada. Una app no debe llamar a la API de ubicación como si la autorización precisa estuviera garantizada.

El ayudante `UbicacionActual` elige un proveedor disponible adecuado al permiso. Una lectura puntual utiliza `LocationManagerCompat.getCurrentLocation`, un executor y una señal de cancelación. La operación puede devolver una posición o `null`; también puede no haber proveedor activo. Conserva un estado que permita distinguir esas situaciones y ofrecer reintentar sin inventar coordenadas.

Al recibir una posición, actualiza la representación y centra el mapa si esa es la acción solicitada. No guardes coordenadas personales en registros públicos ni las utilices para sustituir sin explicación el lugar fijo de la práctica. La obtención puntual no debe transformarse en seguimiento de fondo.

Cancela la solicitud si abandonas la pantalla. Un resultado tardío no debe intentar actualizar una vista destruida. Comprueba también la denegación y la ubicación desactivada: el mapa de referencia puede seguir funcionando aunque no se pueda mostrar la posición del dispositivo.

## Probar con el emulador y con un teléfono

En los controles extendidos del emulador, introduce una ubicación conocida y envíala al dispositivo. Activa la localización, concede permiso y solicita la lectura desde la app. El marcador de posición debe corresponder a las coordenadas enviadas, con las restricciones de precisión elegidas.

La simulación comprueba el recorrido de coordenadas controladas. Después, en un teléfono, comprueba proveedor real, precisión, permiso aproximado/preciso y ausencia de posición. Si no tienes dispositivo físico, registra esa parte como pendiente; no la acredites con el punto simulado.

| Situación | Resultado que debes comprobar |
| --- | --- |
| Permiso denegado | Explicación y continuidad del mapa de referencia |
| Solo ubicación aproximada | Manejo sin asumir precisión GPS |
| Proveedor desactivado o ausente | Estado comprensible y posibilidad de reintentar |
| Posición simulada recibida | Coordenadas y representación coherentes |
| Fallo de estilo o red | Error de cartografía diferenciado de permiso de ubicación |
| Salir durante la lectura | Cancelación sin cierre inesperado |

## Consolidación

Añade un segundo lugar público y permite centrarlo con una acción. Conserva el marcador de referencia y distingue visualmente el de ubicación sin depender solo del color. Explica qué dato contiene cada fuente y qué define cada capa. La solución de referencia del laboratorio muestra el recorrido mínimo; para más capas y seguimiento consulta la ampliación cartográfica cuando esté disponible.

[Solución formativa completa — ZIP](../descargas/proyectos/multimedia-mapa-solucion.zip){ .md-button }

Incluye configuración, ciclo de vida, ayudante de posición, controles y gestión de estados. Su README identifica el proveedor y las comprobaciones realizadas o pendientes.
