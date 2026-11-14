# Práctica guiada: mapa de lugares

MapLibre Native representa datos de OpenStreetMap servidos por OpenFreeMap. Partirás de un mapa con un lugar público y añadirás una lectura puntual de ubicación y su marcador. Primero observarás la infraestructura preparada; después adaptarás un marcador resuelto y conectarás los permisos y el resultado. Lee antes [ubicación](ubicacion.md) y [mapas](mapas.md).

[Descargar proyecto inicial — ZIP](../descargas/proyectos/multimedia-mapa-inicial.zip){ .md-button .md-button--primary }

???+ example "Preparación y mapa ya implementado"

    Abre en Android Studio la carpeta que contiene `settings.gradle.kts`, espera la sincronización y ejecuta **app**. El mapa necesita Internet. La configuración, los recursos, el tema y la vista están preparados; las acciones de ubicación tienen TODO. No necesitas una clave privada ni Google Play Services.

    **Observa el código existente.** Abre `MainActivity.kt` y localiza `MapLibre.getInstance`, la creación de `vista` y su alojamiento con `AndroidView`. Sigue los métodos del ciclo de vida hasta `vista.onDestroy()`. La Activity conserva una vista; cambiar un texto no crea otro mapa.

    Abre `cargarMapa()`: `getMapAsync` entrega el controlador y `setStyle` espera el estilo. El callback incorpora el lugar y centra la cámara. Los errores de cartografía se muestran independientemente del estado de ubicación.

    **Comprueba.** Deben aparecer calles y un círculo grande en Madrid. Mueve el mapa, pulsa **Centrar lugar de referencia**, cambia a otra app y vuelve. Gira el dispositivo. No debe haber cierre inesperado ni dos mapas. Activa modo avión y recarga: debe comunicarse un fallo de cartografía; obtener permiso GPS no resuelve ese fallo. Restablece la conexión y recarga antes de seguir.

??? example "Comprender y modificar el marcador de referencia"

    **Ejemplo resuelto.** El bloque `mapa-referencia` utiliza `Point.fromLngLat` para los datos, `GeoJsonSource("lugar", ...)` para alojarlos y `CircleLayer("lugar-circulo", "lugar")` para definir su aspecto. El método `centrarLugar()` usa `LatLng` en orden latitud/longitud. [Mapas](mapas.md) explica ambos contratos y los parámetros visuales.

    **Ahora te toca.** Sustituye las coordenadas del lugar por otro punto público conocido. Actualiza tanto la fuente como la cámara; conserva los créditos. Aumenta o reduce el radio para observar qué propiedad corresponde a la apariencia y cuál a la posición. Después vuelve a las coordenadas de Madrid para continuar con datos comunes.

    ??? hint "Pista: los dos órdenes de coordenadas"

        GeoJSON recibe longitud y latitud; `LatLng`, latitud y longitud. Cambiar solo la cámara desplaza la ventana, pero no mueve los datos del marcador.

    **Comprueba.** El punto y el centro de cámara coinciden con el lugar elegido. Acercar el mapa cambia la escala; no modifica las coordenadas. Al restaurar Madrid, recargar vuelve a mostrar el círculo de referencia.

??? example "Obtener una posición puntual"

    **Observa el código existente.** `UbicacionActual.kt` ya resuelve la llamada de plataforma. Su método `obtener(resultado, fallo)` recibe dos lambdas: la primera recibe una posición o `null`; la segunda comunica falta de proveedor o autorización. `cancelar()` impide entregar una lectura abandonada. No reescribas este ayudante.

    En `MainActivity`, el receptor `permisos` está registrado y llama a `leerPosicion()` cuando hay autorización aproximada. La función `permiso(nombre)` comprueba una autorización actual. Los métodos `onStop` y `onDestroy` ya cancelan la lectura.

    **Ahora te toca — `mapa-permiso`.** Completa `solicitarUbicacion`: si existe permiso aproximado, lee; si falta, solicita aproximado y preciso juntos con el contrato preparado. No solicites nada al abrir el mapa y no presupongas que se concederá precisión exacta.

    ??? hint "Pista: el resultado se recibe después"

        La llamada `permisos.launch(...)` abre el flujo del sistema. La lectura posterior pertenece al callback ya registrado. El ejemplo de la solicitud está en [ubicación](ubicacion.md).

    **Ahora te toca — `mapa-lectura`.** Al iniciar `leerPosicion`, activa `leyendo` y el recurso `buscando`. Llama al ayudante. En ambos caminos de salida desactiva `leyendo`. Si llega `null`, utiliza `sin_posicion`; si falla, `sin_proveedor`. Si llega una posición, utiliza `posicion_lista`, prepara `coordenadas` con el recurso del mismo nombre y llama a `representarPosicion` pasando longitud y latitud.

    ??? hint "Pista: consulta el contrato del recurso"

        `strings.xml` espera latitud, longitud y precisión en metros. El formato se obtiene con `getString`. Una posición desconocida no es `(0, 0)`. Una devolución `null` no debe actualizar el mapa como si hubiera coordenadas.

    **Comprueba.** Hasta aquí, el mapa fijo sigue disponible. Deniega el permiso: aparece la explicación y no hay cierre inesperado. Con permiso, observa búsqueda y resultado, o el estado sin proveedor. En el emulador, activa ubicación y envía un punto desde **Extended controls → Location**. Las coordenadas recibidas deben concordar; todavía falta dibujar su marcador. Sal de la app durante la lectura y vuelve: debe poder iniciarse otra, sin dejar el botón bloqueado.

??? example "Representar y actualizar la posición"

    Ya conoces la fuente y la capa del lugar fijo. La posición utiliza otra identidad para conservar ambos datos. La teoría de [actualización de puntos](mapas.md#actualizar-un-punto-sin-duplicar-su-fuente) muestra las operaciones nuevas: buscar una fuente y modificar su GeoJSON.

    **Ahora te toca — `mapa-posicion`.** Completa `representarPosicion`. Obtén el estilo actual; si no existe, termina sin modificarlo. Busca la fuente `posicion`. Si falta, créala con el punto recibido y añade una capa `posicion-circulo`. Si existe, actualiza sus datos. Utiliza un círculo más pequeño que el del lugar y otro color, con borde visible. Centra la cámara en la lectura recibida a zoom 15. No sustituyas el marcador de referencia.

    ??? hint "Pista: una identidad por fuente"

        Repite el patrón de `mapa-referencia` solo para la primera lectura. En las posteriores utiliza `getSourceAs<GeoJsonSource>` y `setGeoJson`. Dar de alta otra fuente con el mismo ID no es actualizar su contenido.

    **Comprueba.** Envía dos posiciones simuladas diferentes y pulsa Obtener después de cada una. El círculo pequeño debe cambiar de lugar sin duplicarse; el grande permanece en Madrid. Las coordenadas visibles deben corresponder al pequeño. Recarga el estilo: reaparece el lugar de referencia y otra lectura vuelve a crear el marcador de posición.

??? example "Comprobación final y límites"

    | Caso | Resultado observable |
    | --- | --- |
    | Denegación | Explicación; lugar fijo utilizable |
    | Solo aproximada | Sin asumir GPS preciso; puede no haber proveedor de red |
    | Ubicación desactivada | Estado comprensible; posibilidad de reintentar |
    | Posición simulada | Marcador y coordenadas coherentes con el dato enviado |
    | Salir durante la lectura | Cancelación y botón disponible al volver |
    | Red interrumpida | Error de cartografía diferenciado de ubicación |

    Documenta dispositivo/API, acciones, resultado e incidencias. En un teléfono, prueba además posición real, aproximada/precisa e interior/exterior. La simulación no demuestra recepción de satélites, precisión física ni consumo real. Evita publicar coordenadas personales en el registro de pruebas.

[Descargar solución formativa — ZIP](../descargas/proyectos/multimedia-mapa-solucion.zip){ .md-button }

La solución contiene `MainActivity.kt`, el ayudante de posición, recursos y configuración. Ábrela en otra carpeta para conservar tu trabajo. Su README distingue las verificaciones técnicas de las comprobaciones físicas pendientes.
