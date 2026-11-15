# Práctica guiada: Zona de paseo

MapLibre Native, GeoJSON y LocationManager permiten representar una zona, seguir una posición visible y consultar servicios de prueba. El resultado mostrará **Dentro**, **Fuera** o **Incierta**, además de un punto buscado y una línea de ruta. La parte nueva se aprende sobre geometrías propias y un servidor sintético: no requiere coordenadas personales ni consultas a proveedores públicos.

Observarás un polígono y una ruta resueltos; después completarás clasificación, presentación de posición y búsqueda. Los TODO se concentran en esos tres objetivos. Lee antes [seguimiento](seguimiento.md), [capas](capas.md) y [servicios](servicios.md).

[Descargar proyecto inicial — ZIP](../descargas/proyectos/cartografia-inicial.zip){ .md-button .md-button--primary }

???+ example "Preparación: un esquema que arranca sin red"

    Abre en Android Studio la carpeta del ZIP que contiene `settings.gradle.kts`, sincroniza y ejecuta **app**. El inicio muestra una zona sombreada y su centro sobre un fondo liso. **No son calles**: el estilo local está incluido en el código. La simulación no pide permiso y el seguimiento todavía no comienza.

    **Observa el código existente.** En `MainActivity.cargarMapa`, localiza `fromJson`, el anillo de puntos de la zona y las dos capas que comparten su fuente. Sigue `punto` para identificar creación frente a actualización. En `encuadrar`, distingue límites y margen de cámara. No reescribas el ciclo de vida ya preparado.

    **Ejemplo resuelto.** El polígono visual aproxima un radio de 500 m alrededor de Madrid. `Geometria.distancia` calcula la distancia esférica entre dos puntos; las coordenadas del polígono no sustituyen esa operación. Abre su código y comprueba las conversiones de grados a radianes.

    **Comprueba.** Puedes mover la vista y volver con **Encuadrar zona y ruta**. En modo avión, el esquema sigue visible tras reiniciar la app. Si pruebas **Cargar cartografía remota**, necesitarás red; puedes volver a local incluso si el proveedor falla. No interpretes el esquema liso como un fallo de descarga ni como un mapa de calles offline.

??? example "Ahora te toca: clasificar una posición incierta"

    En `Geometria.kt`, completa `cartografia-zona` dentro de `clasificar`. Ya se validan argumentos finitos y dominios; conserva esos controles. Debe devolver Dentro si todo el intervalo de incertidumbre cabe en el radio, incluido su borde; Fuera si queda completamente fuera; e Incierta en los casos restantes. La tabla de [seguimiento](seguimiento.md#distancia-y-precision) define las desigualdades y los casos límite.

    **Ahora te toca.** Implementa las tres salidas con `when` o condiciones equivalentes. No compares la distancia sola: una lectura a 10 m del centro con precisión de 800 m no permite afirmar pertenencia a la zona de 500 m.

    ??? hint "Pista: empieza por los dos casos concluyentes"

        Compara el extremo más lejano con el radio para Dentro; después compara el más cercano para Fuera. Todo lo demás es incertidumbre. No utilices igualdad exacta de números decimales para decidir si una coordenada es el centro.

    **Comprueba.** Abre `GeometriaTest`. Las pruebas de distancia y coordenadas funcionan desde el inicio; la de clasificación está omitida hasta activarla. En la ventana Gradle ejecuta `:app:testDebugUnitTest` con argumento `-Paprendizaje.geocerca=true`. Debe superar los casos 490 ± 10, 500 ± 10, 510 ± 10 y 511 ± 10 m con radio 500 m. Una prueba omitida no acredita el TODO.

??? example "Conectar posición, marcador y mensaje"

    **Observa el código existente.** `Seguimiento.kt` ya selecciona proveedor autorizado y registra un receptor. En la Activity, Iniciar solicita permisos si hacen falta y llama a `comenzar`. `onStop` retira el receptor. Los botones de simulación llaman a `recibir` con puntos públicos fijos y precisión de 5 m, por el mismo camino que una lectura.

    **Ahora te toca — `cartografia-posicion`.** Completa `recibir`: conserva el punto en `posicion`, actualiza su marcador mediante `punto`, calcula la distancia al centro y clasifica con la precisión recibida. Elige el recurso Dentro/Fuera/Incierta y prepara `estado` con `R.string.resultado`. Los parámetros de ese recurso son mensaje, distancia y precisión. Mantén azul y radio 6 para la posición; el centro tiene otro tamaño.

    ??? hint "Pista: reutiliza los ayudantes preparados"

        `Geometria.centro`, `radioMetros`, `distancia` y `clasificar` ya expresan los datos y operaciones. No vuelvas a construir fuentes manualmente. Usa `when` sobre `Zona`; las constantes de texto están en `strings.xml`. El identificador `posicion` del marcador debe conservarse entre lecturas.

    **Comprueba.** Simular centro informa Dentro y distancia 0. Simular exterior informa Fuera y mueve el círculo pequeño, sin duplicarlo. Con seguimiento activo, cambia a otra app y vuelve: debe ofrecer Iniciar y comunicar que se detuvo. Denegar permiso mantiene la simulación disponible. La precisión real solo se comprueba con un dispositivo y proveedor disponibles.

??? example "Observar una ruta resuelta y completar la búsqueda"

    El ZIP incluye `servidor.py`. Su README explica cómo arrancarlo con Python 3; el emulador utiliza `http://10.0.2.2:8091`. Este servicio devuelve respuestas propias, no consulta Internet. Conserva el esquema local para distinguir el tráfico del servicio de las teselas remotas.

    **Observa el código existente.** En `consultar`, la rama `esRuta` ya llama al cliente suspendido, representa `puntosRuta`, encuadra y actualiza el mensaje. El método lanza una corrutina, muestra carga, captura error y mantiene la cancelación. `dibujarRuta` actualiza una fuente en lugar de duplicarla.

    **Comprueba el ejemplo resuelto.** Activa la casilla de consultas, pulsa **Consultar ruta pública de prueba** y verifica una línea naranja de tres puntos. Es una geometría sintética, sin validez para navegar. No se envía tu posición GPS.

    **Ahora te toca — `cartografia-busqueda`.** Completa la otra rama utilizando `servicio.buscar(base, consulta)`. Conserva el resultado en `busqueda`. Si es `null`, retira la capa y fuente de una búsqueda anterior y comunica `sin_lugar`. Si hay punto, representa el marcador `busqueda`, centra la cámara con `LatLng` y comunica `lugar_listo`. Utiliza color naranja y radio 7. El manejo de errores común ya está preparado; no lo dupliques ni sustituyas el vacío por un error.

    ??? hint "Pista: las entradas no son idénticas"

        La ruta devuelve una lista; la búsqueda, un punto o ausencia de resultado. Busca cómo el cliente interpreta `[]`. GeoJSON y `LatLng` mantienen órdenes de coordenadas distintos. No coloques HTTP dentro del cuerpo de un composable.

    **Comprueba.** `Museo` representa un punto y cambia la cámara. Un nombre distinto devuelve el mensaje vacío. `ERROR` produce error HTTP y `ROTO`, error de contrato, sin cerrar la app. Detén el servidor y repite con otra consulta: aparece error, no una posición inventada. Una consulta ya almacenada en caché puede responder sin red; usa otra para verificar el fallo.

??? example "Comprobación final y pruebas separadas"

    | Camino | Qué debes observar |
    | --- | --- |
    | Simulación interior/exterior | Clasificación y marcador coherentes |
    | Lectura de baja precisión junto al borde | No afirmar certeza inexistente |
    | Salir durante seguimiento | Receptor retirado y sesión detenida |
    | Modo local desde inicio limpio sin red | Zona propia y controles utilizables |
    | Cambio de estilo | Capas reconstruidas sin fuentes duplicadas |
    | Servicio vacío/error | Mensajes distintos de un éxito |
    | Consulta cacheada | Reutilización sin repetir tráfico |

    Comprueba claro/oscuro, horizontal y fuente grande mediante desplazamiento hasta los controles. Registra dispositivo/API, acciones y resultados. Las pruebas de geometría no acreditan recepción GPS, consumo real, geofencing del sistema ni descarga de regiones offline.

[Descargar solución formativa — ZIP](../descargas/proyectos/cartografia-solucion.zip){ .md-button }

La solución conserva la misma estructura, con los tres TODO completados. Incluye los resultados de referencia de geometría y el código separado del ejercicio de métricas del repaso. No necesitas abrirla para acceder al servidor sintético, disponible también en el inicial.
