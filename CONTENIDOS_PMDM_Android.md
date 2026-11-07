# Índice curricular — Desarrollo de aplicaciones Android

> **Estado:** propuesta curricular consolidada para PMDM (DAM2, curso 2026-2027).  
> **Publicación:** los títulos que aparecen bajo «Itinerario del curso» y «Ampliaciones» son los propuestos para la web. No se mostrarán números de unidades de trabajo ni de bloques.  
> **Tecnologías de referencia:** Android · Kotlin · Jetpack Compose · Material 3 · Room (SQLite) · HTTP/REST · Media3 · OpenStreetMap/MapLibre.

## Estructura de navegación de la web

- **Inicio:** presentación muy breve del itinerario, requisitos y cómo utilizar los materiales.
- **Itinerario del curso:** tres temas, uno por cada unidad de trabajo impartida. Contenido **obligatorio y evaluable**, según las evidencias e instrumentos de la programación.
  - **El ecosistema Android y el entorno de desarrollo**.
  - **Desarrollo de aplicaciones con Kotlin y Jetpack Compose**.
  - **Multimedia, localización y notificaciones en Android**.
- **Ampliaciones:** temas independientes, claramente identificados como **opcionales y no evaluables** en el itinerario ordinario. No constituyen requisitos previos de los temas anteriores.
- **Recursos:** documentación oficial y consulta rápida; sin introducir un temario adicional.

Las páginas interiores tampoco llevarán numeración curricular. Los títulos de apartado serán descriptivos. La correspondencia con las unidades de trabajo, los resultados de aprendizaje y las horas figura **solo en el anexo docente al final de este documento**; ese anexo no debe publicarse en la web del alumnado.

---

## Itinerario del curso

### El ecosistema Android y el entorno de desarrollo

**Alcance:** introducción breve, instalación y primera modificación de una aplicación. No se presupone conocimiento previo de Kotlin.

#### Tecnologías y características del desarrollo móvil
- Aplicaciones nativas, multiplataforma e híbridas: Android, iOS, Flutter, React Native y Kotlin Multiplatform. Comparativa de usos, ventajas y limitaciones, sin exigir programar con esas alternativas.
- Características y restricciones de los dispositivos: CPU, memoria, batería, conectividad, diversidad de pantallas y sensores.
- Versiones y compatibilidad de Android; niveles de API; significado de `minSdk`, `targetSdk` y `compileSdk`.
- Perfiles y configuraciones de dispositivos: teléfonos, tabletas, emuladores y dispositivos reales.

#### Preparación del entorno
- Instalación y configuración de Android Studio y Android SDK.
- Device Manager, creación de un dispositivo virtual y ejecución en emulador.
- Requisitos de virtualización y diagnóstico de problemas frecuentes en equipos Windows.
- Creación y apertura de un proyecto con plantilla Compose.

#### Primera aproximación a un proyecto Android
- Directorios y archivos esenciales: módulo `app`, código Kotlin, `res`, `AndroidManifest.xml` y scripts Gradle.
- Función de Gradle y Android Gradle Plugin; visión inicial de las dependencias y el proceso de compilación.
- Identificación de la `MainActivity`, la composición raíz y los elementos de interfaz.
- Ejecución de un proyecto existente, modificación sencilla y comprobación en emulador.
- Introducción al ciclo de vida de la aplicación, únicamente a nivel conceptual.

#### Práctica guiada
- **Abrir, comprender y modificar una aplicación de ejemplo**: identificar su estructura, cambiar un texto o un componente y comprobar la ejecución.

#### Repaso
- Comparación razonada entre tecnologías móviles, lectura de un árbol de proyecto, interpretación de los SDK Levels y resolución de incidencias comunes del entorno.

---

### Desarrollo de aplicaciones con Kotlin y Jetpack Compose

**Alcance:** construir progresivamente una aplicación Android con interfaz, navegación, acceso a datos y comunicación HTTP. La complejidad arquitectónica será la mínima necesaria para comprender y mantener el código.

#### Kotlin para quienes conocen Java
- `val` y `var`, tipos básicos, cadenas y plantillas de texto.
- Condicionales, expresiones `when`, bucles y funciones; parámetros y argumentos.
- Clases, constructores, propiedades y `data class`.
- Nulabilidad, llamadas seguras (`?.`), operador Elvis (`?:`) y tratamiento explícito de valores ausentes.
- Lambdas, funciones de extensión y colecciones (`map`, `filter`, `find`).
- Introducción práctica a corrutinas y `suspend` al trabajar con operaciones de datos; diferencia entre bloquear y suspender.
- Comparaciones cortas Java → Kotlin, evitando reexplicar la programación básica que ya conocen.

#### Estructura y ejecución de una aplicación Android
- `MainActivity`, `onCreate`, `setContent` y ciclo de vida básico.
- `AndroidManifest.xml`, recursos de la aplicación y permisos básicos.
- Gradle Kotlin DSL (`build.gradle.kts`), dependencias, catálogo de versiones (`libs.versions.toml`) y sincronización del proyecto.
- Configuración mínima de KSP al introducir Room; KAPT solo como referencia para reconocer proyectos antiguos.

#### Interfaces declarativas con Jetpack Compose
- Funciones `@Composable` y composición de pantallas.
- `Text`, `Button`, `TextField`, imágenes y componentes habituales de Material 3.
- Distribución con `Column`, `Row`, `Box` y `Modifier`; importancia del orden de modificadores.
- Herramientas Preview y ejecución en emulador.
- Eventos y formularios; validación básica de entradas.
- Estado y recomposición: `remember`, `mutableStateOf` y `rememberSaveable`.
- Elevación del estado (`state hoisting`) y separación de componentes con estado y sin estado.
- `LazyColumn` para mostrar listas, elementos identificados por clave cuando proceda.
- `Scaffold`, barra superior, diálogos y mensajes de interfaz.
- Tema Material 3, colores, tipografía y cambio claro/oscuro.
- Recursos de texto (`strings.xml`, `stringResource`), imágenes vectoriales y accesibilidad básica (`contentDescription`).

#### Navegación entre pantallas
- Pantallas, destinos, navegación de ida y vuelta y control de la pila.
- Rutas tipadas y paso de argumentos simples, preferentemente identificadores; recuperación de datos en destino.
- Gestión básica del estado al navegar y regresar.
- Uso de una solución de navegación de Compose seleccionada y fijada para el curso; no enseñar varias APIs equivalentes a la vez.

#### Organización y estado de la aplicación
- Separación básica de interfaz, estado y acceso a datos.
- `ViewModel`, estado de pantalla (`UiState`) y `StateFlow`.
- Observación del estado en Compose respetando el ciclo de vida.
- Flujo unidireccional: evento → ViewModel → estado → interfaz.
- Introducción práctica a `Repository`, sin Clean Architecture completa ni inyección de dependencias con Hilt.
- Estados de carga, éxito, lista vacía y error; mensajes comprensibles al usuario.

#### Consumo de servicios HTTP y APIs REST
- Internet en Android: permiso, HTTP frente a HTTPS y comunicación asíncrona.
- Cliente HTTP de referencia (Retrofit), endpoints y operaciones sencillas.
- Conversión de JSON a clases de datos Kotlin (serialización).
- Peticiones mediante corrutinas, actualización del estado de pantalla y tratamiento de errores HTTP o de conectividad.
- Integración de los datos recibidos en una lista o pantalla de detalle.

#### Almacenamiento local con Room
- Persistencia local y relación entre SQLite y Room.
- Entidades (`@Entity`), operaciones DAO (`@Dao`) y configuración de la base de datos.
- Operaciones CRUD básicas.
- Observación reactiva de consultas con `Flow` y actualización de la interfaz.
- Acceso desde un Repository sencillo; evitar consultas directas a la base de datos desde composables.
- Relaciones complejas, sincronización offline y DataStore: ampliaciones.

#### Intents e intercambio sencillo de información
- Intents explícitos e implícitos; apertura de acciones proporcionadas por Android y otras aplicaciones.
- Compartir texto e imágenes mediante `ACTION_SEND`/Android Sharesheet.
- Intercambio de datos multimedia con URIs cuando corresponda (se retomará en el tema de multimedia).
- **Conectividad inalámbrica mínima:** comprobar la disponibilidad de una red Wi-Fi y comunicar la aplicación con un servicio de pruebas accesible desde otro equipo de la misma red. Evitar introducir una pila completa de Bluetooth en el itinerario esencial.
- No se imparte programación específica de SMS/MMS.

#### Comprobaciones imprescindibles durante el desarrollo
- Detectar errores de compilación y ejecución, utilizar Logcat y depurar fallos sencillos.
- Comprobar manualmente navegación, formularios, persistencia, conexión y estados de error.
- Instalar y ejecutar una compilación de la aplicación en **un dispositivo Android real**, además del emulador, para acreditar el despliegue.
- Documentar de forma breve qué hace la aplicación, cómo se ejecuta y qué se ha comprobado.
- No se crea un tema obligatorio separado de testing, publicación ni DevOps.

#### Prácticas guiadas y de consolidación
- **Kotlin desde Java:** ejercicios breves de sintaxis, null safety, clases de datos y colecciones.
- **Primera aplicación Compose:** pantalla con formularios, eventos y componentes Material 3.
- **Aplicación con varias pantallas:** navegación y presentación de colecciones.
- **Datos remotos:** mostrar una lista procedente de una API REST, con carga y gestión de errores.
- **Persistencia:** guardar, consultar, modificar y eliminar elementos mediante Room.
- **Intercambio y ejecución real:** compartir contenido con otra aplicación, comprobar conectividad y ejecutar en un teléfono o tableta.

> Las prácticas pueden desarrollarse mediante una **misma aplicación incremental**, evitando empezar proyectos nuevos para cada apartado. La entrega evaluable específica se gestiona desde Moodle.

#### Repaso
- Lectura y corrección de código Kotlin/Compose, preguntas sobre estado y ciclo de vida, errores de navegación, interpretación de respuestas HTTP, consultas Room y problemas de persistencia.

---

### Multimedia, localización y notificaciones en Android

**Alcance:** desarrollar funcionalidades móviles visibles y motivadoras, manteniendo evidencias de captura, transformación, reproducción, eventos y animación multimedia. GPS/mapas y notificaciones se añaden por su valor formativo, aunque no sustituyen los criterios de multimedia.

#### Permisos y acceso seguro a funcionalidades del dispositivo
- Permisos normales y permisos solicitados durante la ejecución.
- Solicitar permisos únicamente cuando la funcionalidad los necesite; justificar su utilidad y gestionar la denegación.
- Introducción a contratos de Activity Result y URI; distinguir funcionalidades que requieren permiso de las que no.
- Localización aproximada y precisa; permiso de notificaciones cuando sea necesario.

#### Imágenes, cámara y galería
- Tipos de datos y formatos habituales de imagen; resolución, tamaño y compresión.
- Selección segura de imágenes mediante Photo Picker.
- Captura básica de fotografía mediante una aplicación de cámara del dispositivo y contrato adecuado; **CameraX con visor propio queda como ampliación**.
- Manejo de URI y acceso al contenido seleccionado/capturado.
- Procesamiento programado de imagen: redimensionado, rotación o ajuste sencillo.
- Conversión y guardado en otro formato compatible, comprobando el resultado.
- Intercambio del archivo resultante con otra aplicación cuando corresponda.

#### Reproducción multimedia y animaciones
- Diferencias entre audio, vídeo y otros contenidos multimedia; formatos y fuentes locales/remotas.
- Introducción a Media3/ExoPlayer: reproducir, pausar, detener y controlar el estado de reproducción.
- Gestión de eventos, errores y liberación de recursos del reproductor.
- Animación sencilla en Compose (`AnimatedVisibility` y/o animaciones de propiedades) asociada a cambios de estado.
- MIDI, streaming RTP y procesado avanzado de audio/vídeo se documentan como ampliaciones.

#### GPS, ubicación y mapas libres
- Localización del dispositivo: GPS/GNSS y proveedores de ubicación; precisión, consumo de batería y privacidad.
- Obtener una ubicación actual de forma puntual y manejar ubicación desconocida o permisos denegados.
- Emular localizaciones para probar el comportamiento de la aplicación.
- Mostrar mapas con **cartografía OpenStreetMap y una biblioteca MapLibre** adecuada para Android/Compose.
- Centrar el mapa, representar la ubicación y colocar marcadores simples.
- Diferenciar los **datos libres de OpenStreetMap**, el SDK que representa el mapa y el **proveedor de teselas**. Respetar atribución, licencia y política de uso del servidor de mapas.
- Seguimiento continuo, rutas, geocercas y mapas offline: ampliaciones.

#### Notificaciones locales
- Qué es una notificación del sistema y cuándo resulta útil.
- Canales de notificación, `NotificationCompat` y contenido básico.
- Permiso `POST_NOTIFICATIONS` en las versiones que lo requieran.
- Crear y mostrar una notificación local a partir de una acción de la aplicación.
- Apertura de una pantalla de la aplicación al pulsarla, a nivel introductorio.
- Notificaciones remotas, programación diferida y trabajos de fondo: ampliaciones.

#### Prácticas guiadas y de consolidación
- **Editor mínimo de imágenes:** seleccionar/capturar, transformar, convertir, guardar y comprobar una imagen.
- **Reproductor sencillo:** audio o vídeo con controles, estados de reproducción y gestión de errores; animación visual ligada a un estado.
- **Mapa de lugares:** mostrar la ubicación actual y al menos un marcador sobre cartografía OSM, manejando permisos y ausencia de ubicación.
- **Notificación local:** desencadenar una notificación desde una acción visible y comprobar su comportamiento.

> No se exige combinar todas estas prácticas en una gran aplicación antes del proyecto intermodular. El tiempo de clase debe concentrarse en funcionalidades acotadas, ejecutables y verificables.

#### Repaso
- Casos de elección de permisos, resolución de fallos con URIs, formatos y transformaciones de imágenes, estados del reproductor, escenarios de localización no disponible y comportamiento de notificaciones en distintas versiones de Android.

---

## Ampliaciones

> **Sección opcional y no evaluable del itinerario ordinario.** Cada elemento siguiente constituye un **tema independiente** de la web, con sus propios apartados, ejemplos y prácticas de ampliación. No se enumerarán como unidades de trabajo ni se mezclarán con la secuencia obligatoria.

### Kotlin avanzado, asincronía y efectos de Compose
- Programación funcional y colecciones avanzadas; extensiones y reutilización.
- Corrutinas en profundidad, cancelación, ámbitos de ejecución y manejo de excepciones.
- `Flow` avanzado y combinación de flujos.
- Efectos de Compose: `LaunchedEffect`, `rememberCoroutineScope`, `DisposableEffect` y claves de efecto.
- Optimización de recomposiciones y tareas asíncronas.

### Diseño visual avanzado, recursos y accesibilidad
- `LazyRow`, `LazyVerticalGrid`, claves y optimización de listas.
- Carga asíncrona de imágenes y caché con Coil.
- Pull-to-refresh, indicadores de progreso y skeleton/shimmer.
- Animaciones avanzadas, transiciones y elementos compartidos.
- Pantallas adaptables con Window Size Classes, tabletas y plegables.
- Navegación visual avanzada: barra inferior y Drawer.
- Iconos adaptativos, Vector Asset Studio, fuentes TTF/OTF.
- Internacionalización con recursos por idioma (`values-es`, `values-en`) y formatos regionales.
- Accesibilidad ampliada: semántica, TalkBack y contraste.

### Persistencia y arquitectura avanzada
- DataStore para preferencias y configuración.
- Relaciones Room 1:N y N:M; consultas complejas y migraciones.
- Persistencia offline-first, caché, coordinación entre Room y APIs remotas.
- Separación de capas UI, dominio y datos; casos de uso.
- Patrones Repository y fuentes de datos múltiples.
- Inversión de control e inyección de dependencias con Hilt: `@Inject`, `@HiltAndroidApp`, `@AndroidEntryPoint`, `@HiltViewModel` y módulos de provisión.

### Redes, mensajería y Bluetooth avanzado
- Alternativas de cliente HTTP como Ktor; serialización y tratamiento de respuestas complejas.
- Redes y protocolos móviles; comunicación con dispositivos próximos.
- Bluetooth clásico: permisos, emparejamiento, conexión e intercambio de datos.
- BLE: permisos `BLUETOOTH_SCAN`, `BLUETOOTH_CONNECT` y `BLUETOOTH_ADVERTISE`; exploración y conexión GATT.
- Lectura y escritura de características y tratamiento de desconexiones.
- Ejercicios con dispositivos compatibles, si el aula dispone de ellos.

### Firebase: backend y almacenamiento en la nube
- Configuración de proyectos y servicios de Firebase.
- Authentication con correo/contraseña y proveedores de identidad.
- Cloud Firestore: lectura/escritura y actualizaciones en tiempo real.
- Cloud Storage: subida y descarga de imágenes y archivos.
- Reglas de acceso, privacidad y seguridad básica.

### Notificaciones avanzadas y tareas en segundo plano
- Notificaciones con diferentes prioridades, iconos y estilos expandibles.
- Firebase Cloud Messaging: mensajes push, tokens y comportamiento en primer/segundo plano.
- WorkManager: tareas diferidas, restricciones de ejecución y reintentos.
- `BroadcastReceiver` y eventos del sistema; restricciones de versiones modernas.
- MediaSession y reproducción en segundo plano con controles del sistema.
- Diferencia entre notificación inmediata, trabajo programado y servicio en primer plano.

### Inteligencia artificial en aplicaciones Android
- IA en la nube frente a IA ejecutada en el dispositivo; modelos multimodales, AICore/Gemini Nano y limitaciones.
- Integración segura con servicios de IA mediante backend o mecanismos apropiados de Firebase AI Logic; protección de credenciales, cuotas y datos de usuario.
- Generación de texto con estado de carga y tratamiento de errores.
- Entrada multimodal: selección de fotografía y consulta sobre su contenido.
- Prácticas de privacidad, costes, límites y verificación de resultados.

### Cámara, edición y reproducción multimedia avanzada
- CameraX: visor integrado, captura de imágenes y vídeo.
- Recorte y edición de imágenes con bibliotecas mantenidas; rotación, compresión y efectos.
- `FileProvider`, URIs y compartición segura de archivos.
- Efectos sonoros con SoundPool y alternativas de reproducción.
- Media3 avanzado, streaming de medios, recursos remotos y Transformer para conversión y edición de audio/vídeo.
- Integración de contenido de YouTube mediante soluciones soportadas y sus limitaciones.
- Introducción a MIDI y otros formatos multimedia específicos.
- RTP, transmisión multimedia en tiempo real, control y monitorización de transmisiones; comparación con protocolos/soluciones actuales.

### Cartografía y geolocalización avanzada
- Actualizaciones periódicas de localización y ciclo de vida.
- Ubicación en segundo plano, consumo energético, privacidad y restricciones.
- Marcadores personalizados, polígonos, capas y control avanzado de cámara.
- Geocercas, rutas y servicios externos de geocodificación y cálculo de trayectos.
- Estrategias de caché y mapas sin conexión con fuentes y proveedores que lo permitan.
- Comparación de MapLibre Native y MapLibre Compose, atendiendo a la estabilidad de su API y los proveedores cartográficos.

### Sensores, biometría y hardware
- `SensorManager`, acelerómetro y giroscopio.
- Sensor de luz, barómetro y temperatura cuando el hardware exista.
- `BiometricPrompt` y autenticación biométrica.
- Vibración, respuesta háptica, cámara/linterna y disponibilidad de características.
- Permisos, consumo de recursos y pruebas con dispositivos reales.

### Calidad, pruebas, publicación y mantenimiento
- Pruebas unitarias de ViewModels y repositorios con JUnit y bibliotecas de dobles como MockK.
- Pruebas de interfaz de Compose con reglas, semántica, búsquedas y aserciones.
- Depuración avanzada: Layout Inspector, recomposiciones y Database Inspector.
- Monitorización de errores con Firebase Crashlytics.
- Variantes Debug/Release, `versionCode`, `versionName` y gestión de dependencias Gradle.
- Optimización de compilaciones, R8 y reglas de mantenimiento de código.
- Firmado, protección de keystores y gestión de secretos.
- APK frente a AAB; Play Console, canales de prueba, publicación y mantenimiento.

---

## Criterio editorial común para los temas de la web

La presentación de cada tema será ligera y repetible, **sin una introducción extensa de objetivos**:

- **Índice breve de contenidos** (preferentemente una lista de enlaces internos).
- **Explicaciones prácticas**, con ejemplos pequeños y comparación con Java cuando aporte valor.
- **Prácticas guiadas** con resultado verificable.
- **Ejercicios o retos de consolidación** que no reproduzcan la solución.
- **Repaso final**: preguntas de comprensión, interpretación y depuración de código y pequeños problemas.
- **Enlaces de ampliación** solo cuando aporten continuidad con lo aprendido.

Priorizar capturas y demostraciones cortas cuando faciliten comprender el resultado. Mantener coherencia de estilos y comportamiento con la web de ACDA, cambiando la identidad cromática a verde y sin trasladar el bloque de videojuegos a este repositorio. Los proyectos con código base o soluciones formativas se enlazarán desde Git; las soluciones de tareas evaluables permanecerán privadas hasta que proceda. No se crea ningún apartado de temario asociado al proyecto intermodular.

---

## Anexo de planificación docente — NO PUBLICAR EN LA WEB

### Correspondencia administrativa y horas

| Tema de la web | Unidad de trabajo | RA principal | Horas de la programación | Instrumentos actuales |
|---|---|---|---:|---|
| El ecosistema Android y el entorno de desarrollo | **UT5** | **RA1** | **2 h** | Test teórico |
| Desarrollo de aplicaciones con Kotlin y Jetpack Compose | **UT6** | **RA2** | **18 h** | Test teórico (25 %) y examen práctico (75 %) |
| Multimedia, localización y notificaciones en Android | **UT7** | **RA3** | **9 h** | Proyectos y tareas |
| **Total** | | | **29 h** | |

**UT8:** no tiene tema propio en la web. Sus horas se destinan al desarrollo del proyecto intermodular, empleando los conocimientos de los tres temas y consultando ampliaciones cuando el proyecto lo requiera.

### Distribución orientativa del tiempo

| Unidad | Distribución orientativa |
|---|---|
| **UT5 — 2 h** | Tecnologías y particularidades móviles (0,5 h); entorno, estructura y proyecto de ejemplo (1,5 h). Instalación preparada o guiada previamente para que no consuma toda la sesión. |
| **UT6 — 18 h** | Kotlin (2 h); Compose/Material 3 (4 h); estado, navegación y arquitectura básica (3 h); HTTP/REST (2 h); Room (3 h); Intents y comunicación inalámbrica básica (2 h); comprobaciones, ejecución real y documentación mínima (2 h). Los contenidos transversales se integran en las prácticas. |
| **UT7 — 9 h** | Selección/captura y transformación de imágenes (3 h); reproducción y animación multimedia (2 h); GPS y mapa (2 h); notificación local (1 h); integración, comprobaciones y documentación (1 h). Estas horas pueden distribuirse entre prácticas sin reservar necesariamente sesiones independientes. |

### Correspondencia con los criterios de evaluación

| Resultado | Criterios que deben quedar acreditados | Evidencias prácticas previstas |
|---|---|---|
| **RA1** | **a–e**: limitaciones, tecnologías, entornos, configuraciones y perfiles | Comparativa y análisis; instalación/configuración y lectura de proyecto. |
| **RA1** | **f–h**: estructura, modificaciones y emuladores | Abrir un proyecto preparado, localizar clases, modificarlo y ejecutar en emulador. El test actual no demuestra directamente todas estas acciones; revisar su instrumento el próximo curso. |
| **RA2** | **a–b**: estructura e interfaces | Clases Kotlin, Compose, formularios, menús, diálogos y navegación. |
| **RA2** | **c**: comunicación con dispositivos inalámbricos | Comprobar Wi-Fi en dispositivo real y comunicarse con un servicio/dispositivo de pruebas en la misma red mediante clases de conectividad y cliente HTTP. **Validar que la evidencia responde a la literalidad del criterio en la programación del departamento.** |
| **RA2** | **d**: intercambio de mensajes de texto y multimedia | Compartición de texto e imagen mediante Intents/Android Sharesheet; comprobar recepción en una aplicación destinataria. No se requiere programar SMS/MMS. |
| **RA2** | **e–f**: HTTP/HTTPS y persistencia | Consumo funcional de API REST y CRUD Room. |
| **RA2** | **g–i**: pruebas, despliegue y documentación | Prueba manual con emulador, ejecución en dispositivo físico y guía breve de ejecución/pruebas. No pueden omitirse por completo ni relegarse íntegramente a ampliaciones. |
| **RA3** | **a–e**: APIs multimedia, captura, conversión, transformación, estados y excepciones | Selección/captura de imagen; transformación y conversión a formato compatible; manejo de errores y eventos. |
| **RA3** | **f–g**: animación y reproductores | Animación Compose observable y reproducción de audio/vídeo con controles. |
| **RA3** | **h**: depuración y documentación | Verificación de la funcionalidad multimedia y descripción breve de las pruebas. |

**Advertencia de alcance:** GPS, mapas y notificaciones añaden valor práctico, pero **no son sustitutos de los CE específicos de RA3**. Para encajarlos en nueve horas, se prioriza una implementación pequeña por funcionalidad; procesamiento complejo de audio/vídeo, transmisiones en tiempo real y notificaciones remotas quedan en ampliaciones.

### Decisiones tecnológicas a revisar antes de redactar los tutoriales

- **Mapas:** MapLibre + datos OSM. MapLibre Compose es una opción coherente con Compose, pero su API es todavía evolutiva; fijar y probar una versión concreta antes de preparar el proyecto base. **No usar osmdroid como tecnología nueva de referencia**: repositorio archivado desde noviembre de 2024.
- **Teselas OpenStreetMap:** los datos OSM son abiertos, pero los servidores públicos de teselas no son infraestructura ilimitada ni un servicio con SLA. Verificar atribución, identificación y caché; no hacer descargas masivas u offline desde `tile.openstreetmap.org`.
- **Localización:** explicar que la posición puede proceder de varios proveedores y no siempre del GPS. Elegir entre APIs de Android y Fused Location Provider según el entorno del aula; la segunda depende de Google Play services. La ubicación puede ser nula o aproximada.
- **Notificaciones:** enseñar permiso de ejecución desde Android 13 cuando proceda, canales y notificaciones locales. No mezclarlo con FCM ni WorkManager.
- **Navegación:** fijar en la versión de Android elegida una única biblioteca de navegación para Compose; actualizar el código a su API documentada.
- **Paquetes y versiones:** fijar Android Studio, Android Gradle Plugin, Kotlin, Compose BOM, Room/KSP, Media3 y biblioteca cartográfica para que todos los ejemplos sean ejecutables y reproducibles.

### Fuentes oficiales para la preparación técnica

- Android Developers: https://developer.android.com/
- Kotlin: https://kotlinlang.org/docs/home.html
- Jetpack Compose: https://developer.android.com/develop/ui/compose
- Android arquitectura: https://developer.android.com/topic/architecture
- Room: https://developer.android.com/training/data-storage/room
- Permisos de localización: https://developer.android.com/develop/sensors-and-location/location/permissions
- Notificaciones en Compose: https://developer.android.com/develop/ui/compose/notifications/notification-permission
- Media3: https://developer.android.com/media/media3
- MapLibre Compose: https://maplibre.org/maplibre-compose/
- Política oficial de teselas OSM: https://operations.osmfoundation.org/policies/tiles/
- osmdroid (archivo histórico): https://github.com/osmdroid/osmdroid
