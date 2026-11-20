# Estado del encargo integral

## Adaptación al aula — verificada y publicada (20 de noviembre)

- Registro cronológico de `adaptacion/entorno-aula-java25`, integrada por avance directo en `main`; los cierres posteriores sustituyen los pendientes del piloto. El árbol inicial estaba limpio y coincidía con `origin/main` en `f1b6495`.
- Referencia solicitada: Android Studio Rabbit 2 2026.2.2 Patch 1 (AI-262.10968.63.2622.16840690), JBR 25.0.4 / Java 25.0.4.1, SDK 37.0, Build Tools 36.0.0 y AVD API 37. No se instalan otros JDK/SDK ni se cambia la configuración global.
- Causa reproducida del fallo anterior: Gradle 8.13 y su compilador Kotlin DSL no interpretan Java 25; `help --stacktrace` falla en `JavaVersion.parse`, antes de configurar Android. La ausencia inicial del SDK ya se ha resuelto parcialmente mediante la instalación realizada por el propietario.
- Piloto limitado a cinco soluciones: Ecosistema, Cuaderno, Persistencia/Hilt, Reproductor/Media3 y Firebase. Candidata: AGP 9.2.1, Gradle 9.4.1, Kotlin/Compose Compiler 2.2.21 con soporte Kotlin integrado de AGP, KSP 2.3.4 y Hilt 2.59.2. Se conserva el resto de bibliotecas; `compileSdk` sube a 37 y se mantienen `targetSdk 36`, `minSdk 26` y bytecode 17.
- Ecosistema, Cuaderno y Persistencia ya superan assembleDebug, testDebugUnitTest, lintDebug y assembleDebugAndroidTest con JBR 25. Cuaderno: 13 JVM correctas; Persistencia: una JVM correcta; Ecosistema no contiene JVM. Media3/Firebase e instrumentación aún pendientes en este punto.
- El emulador instalado termina durante el arranque, antes de ejecutar apps: código de acceso inválido `3221225477` en el intento directo. Se están comprobando variantes temporales de arranque; no se certifica ejecución Android ni sincronización visual del IDE por los builds Gradle.
- Evidencias locales ignoradas: `qa-captures/entorno-aula/`. No se ha modificado el temario ni se declara terminada la adaptación.

### Piloto compilado y extensión a las 39 variantes

- Las cinco soluciones superaron las cuatro tareas Gradle con JBR 25. Firebase: cuatro JVM y 13 comprobaciones de reglas con Emulator Suite local y Java 25 correctas. No se eliminaron tests ni funcionalidades; hasta este punto no hicieron falta cambios del código de las apps.
- Ecosistema se abrió en Android Studio Rabbit 2 y terminó la sincronización real del IDE en 2 min 24 s; registro del 20/11 a las 09:57. Esto acredita sincronización del IDE de ese proyecto, no de los otros 38.
- Tras los intentos fallidos, el emulador API 37 arrancó con opciones temporales `-gpu host -feature -Vulkan -no-snapshot`; autorización ADB solo para la sesión, sin marcar autorización permanente. La imagen expone API 37 y boot completo. La instrumentación del piloto está en curso; se conserva el diagnóstico de los fallos previos.
- Extendida en rama la configuración a las 39 variantes; Wrapper 9.4.1 regenerado (scripts/JAR) y checksum oficial de la distribución. Comprobador de coherencia de catálogos, SDK, bytecode y Wrapper correcto. Compilación completa local en curso con dos procesos y CI preparada con cuatro grupos y Java 25. El despliegue queda restringido a main también en workflow_dispatch.
- Referencia técnica actualizada en Recursos, README y AGENTS, instrucciones Gradle de los proyectos y fragmentos afectados. Se conservan target/min/bytecode, temario, aplicaciones y estructura de la web. Publicación y descargas aún pendientes de validación final.

### Cierre del piloto de ejecución en API 37

- Los cinco pilotos superaron build/JVM/lint/AndroidTest y 13 instrumentadas reales: Ecosistema 1, Cuaderno 3, Persistencia/Hilt 5, Media3 1 y Firebase 3. Firebase conserva además sus cuatro JVM y 13 comprobaciones de reglas locales con Java 25. No se ha conectado a servicios de producción.
- Primera incompatibilidad de ejecución: Compose resolvía Espresso Core 3.5.0 transitivamente; su llamada reflectiva a InputManager.getInstance falla en Android 17. Se declaró Espresso estable 3.7.0 explícitamente en los 39 catálogos; Ecosistema y los demás pilotos pasaron después sin retirar comprobaciones.
- Photo Picker API 37 utiliza Compose, no expone icon_thumbnail y requiere confirmar con Done. XML real inspeccionado: miniatura mediante descripción accesible en la imagen en inglés y barra con una selección y Done. Se adaptaron los tests de Firebase e IA conservando el ID antiguo como alternativa y todas las verificaciones de subida, descarga, aislamiento de sesiones y contenido multimodal. Las esperas toleran la ausencia transitoria de raíces Compose mediante su parámetro público, conservando los plazos y la condición final. Firebase 3/3 correcto tras el ajuste; IA compilado y pendiente de su ronda de ejecución. No hubo cambios del código de producción de las apps.
- Se añadió únicamente la confirmación Done a las instrucciones de selección de imágenes afectadas. No se cambiaron las actividades, TODO, soluciones ni metodología.
- Incidencias de coordinación de QA: dos builds locales coincidieron sobre Firebase/solucion y se interfirieron al regenerar recursos; reintento aislado completo correcto. Mapa/solucion agotó el bloqueo de la caché compartida durante la validación paralela: se repetirá sin caché de compilación, sin borrar cachés ni alterar validaciones. Los logs fallidos se conservan, no se presentan como compilaciones correctas.
- Preflight remoto 44707109555 (0b9db6c): web y cuatro grupos Android correctos, 39 variantes comprobadas y deploy omitido por rama. Es anterior a las últimas correcciones de pruebas/documentación; no sustituye la CI final de publicación.
- Web local comprobada con 140 páginas, dos tamaños/dos temas, teclado y 39 ZIP comparados archivo por archivo; se reforzó el verificador para detectar archivos omitidos. Tablas técnicas PDF renderizadas/revisadas sin recortes tras separar herramientas y bibliotecas. Es necesario regenerar las descargas tras los últimos cambios de tests y la nota Done antes de publicar.
- Las omisiones ya existentes de pruebas en starters se conservan para las etapas que debe completar el alumnado; no se ha añadido ningún Ignore. Continúan la ronda local completa, Release/R8, instrumentadas restantes y publicación.

### Verificación local definitiva del entorno del aula

- 39/39 variantes independientes: resolución/configuración, assembleDebug, testDebugUnitTest, lintDebug y assembleDebugAndroidTest correctos en Windows con Java/JBR 25 y el SDK instalado. Los dos fallos operativos iniciales (solapamiento de Firebase y bloqueo de caché de Mapa) se conservan en el historial de resultados; ambos se repitieron correctamente de forma aislada. No se borraron cachés ni se cambiaron instalaciones del aula.
- JVM: 127 casos informados por XML, 114 ejecutados, 13 omisiones preexistentes, cero fallos/errores. Lint: 39 informes, cero errores y 666 avisos: 442 de versiones de dependencias, 106 recursos sin uso, 39 de target y 39 de AGP, más recomendaciones menores. Dos ExportedService corresponden al servicio Media3 exportado ya existente en Segundo plano; se conservan para revisión específica, sin silenciar lint ni cambiar su comportamiento en este encargo.
- Instrumentación real en el AVD Android 17/API 37: 40 pruebas correctas entre los cinco pilotos y las otras 18 suites; siete omisiones preexistentes informadas por el runner en proyectos iniciales. Incluye Room/Hilt/migración, HTTP/checkpoint, Photo Picker y sesiones Firebase, IA multimodal simulada, CameraX virtual, transformación/crop/Media3/SoundPool, mapas, notificaciones, WorkManager y controles de sensores. No se presenta la simulación como prueba de hardware físico.
- IA pasó sus dos instrumentadas, incluida selección real con Done y envío de imagen al backend simulado. Asincronía necesitó esperar también el resultado positivo tras filtrar: la desaparición de un resultado durante carga no demostraba que la nueva consulta hubiera terminado. Se conservan todas las aserciones y el plazo. Notificaciones se repitió con POST_NOTIFICATIONS, precondición ya documentada del test, y se restauró el permiso anterior al terminar.
- Calidad inicial y solución: APK y AAB Release con R8 construidos correctamente, sin firma privada ni publicación en tiendas. Backend IA: cinco unittest; servidor de cartografía: seis; servidor LAN: tres, todos correctos. Firebase: 13 comprobaciones de reglas locales con Java 25 correctas.
- Los únicos cambios Kotlin afectan a tres archivos de androidTest (Firebase, IA y Asincronía). No se cambió código de producción, funcionalidad, permisos, seguridad, esquema Room, minSdk, targetSdk ni bytecode. Los servicios de laboratorio Firebase/IA se detuvieron al finalizar sus comprobaciones.
- Evidencias locales: inventarios inicial/final, build-all.json con intentos anteriores y reintentos, recheck.json, instrumentadas.json, extra-instrumentadas.json, informes XML/lint y logs en qa-captures/entorno-aula. La configuración oficial se consulta únicamente en Recursos.
- La regeneración, CI, publicación y prueba de descargas quedaron completadas en el cierre siguiente. Se conserva la sincronización real comprobada de Ecosistema en Android Studio; Run visual sigue sin verificarse.
- Límites: un AVD API 37 en inglés; no ejecución en API 26–36 ni teléfono físico, GPS/Bluetooth/MIDI/biometría/salida audible reales, TalkBack, Firebase/FCM de producción ni inferencia remota. El emulador requirió opciones temporales de arranque host/sin Vulkan; no se cambió su configuración guardada ni se instalaron controladores u otros JDK/SDK.

### Cierre de publicación y descargas del aula

- Código publicado: `d7e81c72d4633c5b82795445ed1d0bd565a232df`. [GitHub Actions 44722281139](https://github.com/MJSPlayground/fp-dam2-pmdm-android/actions/runs/44722281139) correcto: web, cuatro grupos Android (10/10/10/9) y despliegue. Auditoría de sus logs: 39 BUILD SUCCESSFUL y 39 rutas únicas. CI usa Java 25, plataforma 37.0 y Build Tools 36.0.0.
- [Pages](https://mjsplayground.github.io/fp-dam2-pmdm-android/) comprobado tras desplegar: 140 páginas, dos tamaños, claro/oscuro, teclado, búsqueda, 15 pares de enlaces PDF y 39 ZIP cuyo inventario y contenido coinciden con las fuentes. Los 16 PDF públicos coinciden por SHA-256 con el artefacto de esta CI. Global publicado: 261 páginas en Linux; local Windows: 262. Recursos: cinco páginas, versiones contrastadas y tablas renderizadas completas en ambas plataformas.
- Cinco ZIP descargados realmente desde Pages y extraídos en directorios temporales nuevos: Ecosistema, Cuaderno, Persistencia/Hilt, Reproductor/Media3 y Firebase, todos solución. Los cinco superan assembleDebug con el Java 25 del sistema y el SDK instalado, sin editar archivos, sin local.properties y con todos los hashes de las fuentes extraídas intactos después del build. Las rutas de SDK se proporcionaron únicamente al proceso de validación. El tiempo de Cuaderno incluye una pausa larga de la sesión y no sirve como medida de rendimiento.
- README, AGENTS, Recursos, instrucciones técnicas de proyectos y fragmentos afectados actualizados. El entorno del aula es la referencia oficial; no se aceptan actualizaciones automáticas sin validación previa. El comprobador sencillo verifica coherencia de los 39 catálogos, SDK, bytecode, criterios JVM y componentes del Wrapper. Se conservan los proyectos independientes, el temario y el diseño. No se modificaron otros repositorios ni instalaciones/configuración global.
- Resultado local definitivo: 39/39 builds y lint sin errores; 114 JVM correctas y 13 omisiones preexistentes; 40 instrumentadas correctas y siete omisiones preexistentes; dos variantes Calidad con APK/AAB Release correctos. Los 666 avisos de lint siguen visibles, incluidos los dos ExportedService anteriores para revisión específica. No se eliminaron ni desactivaron pruebas, seguridad o funcionalidades.
- Límites finales: sincronización real del IDE verificada únicamente en Ecosistema; apertura/sincronización visual del resto y botón Run del IDE sin certificar. El AVD completó las 40 instrumentadas; los últimos intentos de abrir su ventana fallaron con errores OpenGL en esta sesión y se detuvo nuestro lanzador fallido. No se tocaron controladores ni configuración persistente. Se mantienen las limitaciones físicas, versiones Android inferiores y servicios de producción descritas arriba.
- Evidencias ignoradas adicionales: ci-final-logs.zip, ci-publicacion-final.json, ci-final-pages.zip, publico-builds.json, cinco ZIP públicos y sus logs, web-publico-final.log y capturas reales. El commit posterior de cierre solo actualiza este registro; no cambia las fuentes, descargas o web verificadas en d7e81c7.

El alcance aprobado es `CONTENIDOS_PMDM_Android.md`: tres temas principales y once ampliaciones completos. Se autoriza implementación y publicación progresiva en `main`; no se autoriza difusión en Moodle ni modificación de otros repositorios.

## Publicación anterior a la adaptación del aula (histórico)

Los tres temas principales y las once ampliaciones están desarrollados y publicados en [GitHub Pages](https://mjsplayground.github.io/fp-dam2-pmdm-android/): 140 páginas, 39 variantes descargables (iniciales, checkpoints y soluciones), 15 PDF de temas/recursos y un global de 261 páginas en la publicación Linux. No quedan temas pendientes ni fuentes en borradores. Las guías de ecosistema/Cuaderno se mejoraron conservando sus proyectos y su progresión; las nuevas guías alternan ejemplos, tareas, pistas y comprobaciones. Las soluciones están separadas.

Código verificado: 33a19d5. [Workflow final 43710364117](https://github.com/MJSPlayground/fp-dam2-pmdm-android/actions/runs/43710364117) completado con éxito: cuatro grupos Android, web y despliegue. Registros contrastados: 39 BUILD SUCCESSFUL y 39 rutas únicas (10/10/10/9). JUnit de IA descargado del artefacto: cuatro pruebas, cero fallos/errores/omisiones tras corregir la carrera del test. El primer intento fallido se conserva documentado más abajo.

Pages comprobado después del despliegue: 140 páginas, dos tamaños, dos temas, teclado, búsqueda, 15 pares PDF y 39 ZIP íntegros y contrastados con las fuentes. Los 16 PDF públicos coinciden por SHA-256 con el artefacto final de CI. Global con las cabeceras de las 140 páginas; revisión visual representativa en Windows y Linux, no inspección manual de cada página. La paginación local de Windows es de 262 páginas.

Las limitaciones físicas y de servicios reales están documentadas: no se certifican por compilación/emulador ni se declaran cubiertas administrativamente. El material queda preparado para revisión docente, sin atribuirle aprobación pedagógica humana.

Las secciones siguientes conservan el registro cronológico; sus pendientes históricos quedan sustituidos por los cierres posteriores. Las limitaciones físicas y de servicios externos se mantienen cuando no aparece una prueba explícita posterior.

## Hitos y orden

1. Infraestructura: adaptar ACDA, verde, Zensical Modern, PDF por tema/global, descargas, enlaces, QA responsive y workflow Pages.
2. Ecosistema: tecnologías → entorno → estructura → práctica inicial/solución → repaso. Cerrar con compilación, emulador, web y PDF.
3. Desarrollo: Kotlin desde Java → Compose/estado → navegación → ViewModel/StateFlow/Repository → HTTP → Room → Intents/Wi-Fi/dispositivo real → guiado incremental, consolidación y repaso.
4. Multimedia: permisos → imagen/captura/transformación/conversión → Media3/animaciones → ubicación/MapLibre/OSM → notificaciones → prácticas, consolidación y repaso.
5. Once ampliaciones, consecutivamente y en el orden del índice: cada una con explicación, integración, prácticas/soluciones y limitaciones verificables.
6. Auditoría global didáctica y UX separadas; proyectos, enlaces, PDF, CI y publicación.

## Hito verificado: infraestructura y ecosistema

- Repositorio sincronizado por `git fetch origin`: `main...origin/main` sin divergencia y sin cambios previos del propietario.
- Leídas las fuentes locales aprobadas y la base ACDA. No se han modificado estándares compartidos.
- Infraestructura adaptada y primer tema desarrollado con teoría, práctica, starter/solución y repaso con respuestas separadas.
- Base Android conservadora: AGP 8.13.2, Gradle 8.13, Kotlin 2.2.21, Compose BOM 2025.12.00, API 36 y mínimo 26. No confundir versiones elegidas con últimas versiones disponibles.
- Pages configurado en modo Actions. Publicación verificada del commit `9fbf386`: [workflow 42452408186](https://github.com/MJSPlayground/fp-dam2-pmdm-android/actions/runs/42452408186), Android/web/deploy correctos. Corregido en ese commit el uso del paquete SDK retirado `tools`.
- `scripts/test_navigation.py`, `compileall`, `package_projects.py` y `git diff --check`: correctos.
- `scripts/export_pdfs.py`: PDF ecosistema (15 páginas), recursos (4) y global (19), con portada e índice; incluye avisos de construcción y captura real del emulador. Página de la captura renderizada y revisada: imagen y pie centrados, completos y dentro de A4, sin solapamientos observados.
- `scripts/check_links.py`: 10 páginas, 77 enlaces, 60 fragmentos y 47 entradas de búsqueda en la última comprobación previa al cierre.
- `scripts/check_site_ui.py`: Chromium, escritorio/móvil, claro/oscuro, teclado, búsqueda y navegación a resultado, contraste de pestañas, ZIP descargados/inspeccionados y PDF. Figura de la captura comprobada y centrada en ambos tamaños y temas; capturas locales revisadas.
- Ambos proyectos ecosistema: `:app:assembleDebug :app:lintDebug` correctos. Lint inicial 0 errores/8 avisos (versiones fijadas y recursos reservados para completar TODO); solución 0 errores/6 avisos (versiones fijadas).
- Solución: `:app:connectedDebugAndroidTest` en Pixel_9 API 36/Android 16: 1 prueba, 0 fallos, 0 errores, 0 omitidas. Comprueba saludo, clic y mensaje.
- Revisión didáctica independiente: corregidos árbol de rutas, enlace de solución desde ZIP, referencia oficial de estado y licencia incluida en descargas. Preparado para revisión docente; no equivale a aprobación pedagógica humana.

## Hito verificado: Kotlin y desarrollo Android

- Teoría completa, prácticas de Kotlin, guiado incremental, integración concreta, consolidación y repaso con soluciones separados, más figura propia de flujo.
- Cuaderno tiene cinco puntos de control del mismo applicationId: inicial, Compose, navegación, ViewModel/HTTP en memoria y solución Room. Todos compilan y pasan lint sin errores.
- Solución: 13 pruebas JVM (ejercicios, límites, mapeo y Retrofit real en loopback) y 3 instrumentadas (Room CRUD, formulario/recreación/diálogo y error/reintento), sin fallos, en Pixel_9 API 36.
- Remotos: 14 pruebas JVM y 2 instrumentadas, sin fallos. El proyecto inicial omite deliberadamente 6 pruebas de ejercicios hasta completar sus TODO; no se contabilizan como aprobadas.
- Servidor Python: `scripts/test_lan_server.py`, 3 pruebas correctas de contrato HTTP/JSON, casos vacío y formato incompatible, 503/404/400.
- Corregidos hallazgos de revisión didáctica: dependencia oculta de factorías/Application; etapa intermedia anterior a Room; aliases y recursos de ejemplos; significado de mensajes; explicación de la observación del detalle.
- Revisión Android independiente sin defectos bloqueantes/altos confirmados: 360/411/600/840 dp, horizontal 800 × 360, claro/oscuro, fuente normal/1.3/2.0, teclado y scroll hasta Guardar, CRUD propio y cancelación/confirmación de borrado. 21 controles activos medidos, mínimo 48 dp. Capturas y XML reales en QA local; entorno restaurado al terminar.
- GET público a JSONPlaceholder comprobado desde la app, además del servidor local y MockWebServer. No se enviaron datos personales ni escrituras al servicio externo.
- `export_pdfs.py`: ecosistema 15 páginas, desarrollo 63, recursos 4 y global 80. Páginas de capturas, progreso y código largo renderizadas/revisadas sin pérdidas ni cortes. Corregido título de código huérfano; copia real de snippet contrastada con fuente.
- `check_links.py`: 26 páginas, 752 enlaces, 683 fragmentos y 181 entradas de búsqueda. `check_site_ui.py`: escritorio/móvil, claro/oscuro, búsqueda, teclado, 3 pares de PDF, 7 ZIP y 6 marcas de progreso con persistencia y reinicio; correcto.
- Revisiones didáctica y UX separadas completadas con correcciones. Material preparado para revisión docente; no se declara aprobado pedagógicamente.

## Pendiente

El hito central `50ead86` está publicado y su workflow 42453872684 finalizó correctamente. Continúan las ampliaciones y la revisión global; solo se incorporan a navegación las versiones comprobadas.

La ejecución por Run dentro del IDE no se ha automatizado visualmente: build y emulador se han comprobado por Gradle/ADB. No hay teléfono físico conectado; despliegue físico y comunicación Wi-Fi entre equipos siguen pendientes. TalkBack y diálogo de eliminación con IME abierto no se han verificado. Estas limitaciones no se certifican por emulación ni por compilación.

La programación y hoja de presentación externas no están disponibles en el repositorio. El alcance ya acordado permite elaborar contenidos; no se publican ni se reconstruyen correspondencias administrativas.

## Recuperación y continuación (noviembre)

- Antes de editar: main y origin/main coincidían en 50ead86; no había cambios staged ni unstaged en archivos rastreados. Había diez borradores y ocho proyectos nuevos de multimedia. Cachés, compilaciones, ZIP/PDF y evidencias QA ignorados conservados localmente.
- Recuperación íntegra de fuentes sin reset/clean/pull: fd4db12 subido a recuperacion/multimedia-noviembre. El workflow solo despliega main, por lo que esta copia no publicó proyectos incompletos. Escaneo de patrones de secretos y revisión de archivos del commit sin hallazgos; no se incluyeron builds, credenciales ni configuración local.
- Segundo resguardo 5024df7: starters de imágenes y reproductor ejecutables, ejemplos preparados y TODO de técnica equivalente; guías Nexo, consolidación autónoma y repaso separado.
- Corregidas las guías de ecosistema y Cuaderno sin cambiar sus soluciones/checkpoints: etapas desplegables, ejemplos/tareas, pistas y comprobaciones; se conservan cabeceras y seis casillas. Integración HTTP evita mostrar de inmediato el repositorio y DTO completos. Revisión independiente realizada; no equivale a aprobación docente.
- Multimedia: teoría, cuatro guiadas, consolidación, depuración y repaso; fuentes trasladadas de borradores a docs/multimedia conservando historial. Ocho variantes compilan y lint sin errores. Imágenes: 4 instrumentadas correctas API36 (EXIF, giro, formato, proveedor, dimensiones). Reproductor: 1 instrumentada correcta (eventos, controles, final/error/liberación); primer intento Gradle perdió conexión del emulador, repetición directa ADB correcta. Mapa/notificaciones: 1 instrumentada correcta cada solución.
- Mapa: teselas reales OpenFreeMap y marcador fijo observados; posición simulada -3.69/40.42 recibida y representada. Notificaciones: bandeja real y apertura del detalle comprobadas. El resumen automático del sistema se distingue de avisos propios en pruebas. No certifica GPS físico ni versiones inferiores.
- Revisión independiente multimedia: sin bloqueantes de secuencia; corregida diferencia entre tamaño exacto ImageDecoder y muestreo API26–27 en teoría/repaso. Hardware, TalkBack, aproximada/denegada manual y matriz visual completa siguen pendientes.
- Asincronía: ampliación con teoría, guiada, proyectos y actividades/repaso; solución 9 pruebas JVM correctas. Validación de interfaz en curso.
- Sensores: ampliación con teoría, biometría y hardware, guiada, inicial/solución separados. Ambos compilan y pasan lint; solución 2 pruebas JVM correctas. Se fijó Fragment 1.8.9 para compatibilidad de Activity Result y Biometric 1.1.0; no se silenció el error de lint. Interfaz/hardware aún en revisión.
- PDF inicial de integración: ecosistema16p, desarrollo65p, multimedia38p, asincronía19p, sensores10p, recursos4p, global143p. Exportador abre todas las pistas/etapas; etapas largas pueden dividirse entre páginas. Falta cierre visual de esta ronda y regeneración tras últimas correcciones.
- Enlaces estáticos: 58 páginas, 1028 enlaces, 877 fragmentos y 323 entradas de búsqueda correctos. QA navegador ampliado a contenido desplegado, pistas inicialmente cerradas y destinos hash dentro de etapas.

### Trabajo en curso y próximo cierre

Diseño visual, persistencia/arquitectura y cartografía se desarrollan en rutas propias y no se publican como terminados. Después: redes/Bluetooth, Firebase, tareas de fondo, IA, multimedia avanzada y calidad/publicación; aún faltan hasta completar las once ampliaciones. No se declara finalizado el encargo integral.

### Cierre QA del hito multimedia y primeras ampliaciones

- Asincronía: además de 9 JVM, 1 instrumentada Compose correcta en Pixel_9 API36: filtros, favoritos, error/recuperación y snackbar.
- Sensores: 1 instrumentada Compose correcta API36: cambio de sensores, acciones alcanzables y recreación. No verifica biometría/flash/vibración físicos. Dos JVM correctas y lint sin errores en inicial/solución.
- Navegador: 58 páginas, 2 tamaños, 2 temas, teclado, 6 pares PDF y19 ZIP descargados/inspeccionados correctos. Pruebas abren también contenido plegado y comprueban hash interior y pistas cerradas. Capturas PDF renderizadas/revisadas de guiadas Cuaderno/imágenes/asincronía: código, pistas y continuidad entre páginas legibles, sin cortes observados.
- En esta sesión el emulador presentó capturas obsoletas y UiAutomator sin root después de iniciar Photo Picker. Se reinició sin borrar datos y las pruebas Compose de sensores volvieron a pasar. No se certifica selección/captura física ni salida audible por esas pruebas; la lógica de procesamiento sí tiene instrumentación real.
- Las compilaciones locales de web pueden recoger temas no versionados que otros agentes están redactando. La publicación se valida sobre una instantánea Git aislada del hito, para que los borradores no entren accidentalmente en Pages. Se conserva el árbol de trabajo original.

## Reanudación protegida y ampliaciones (15 de noviembre)

Resguardo 38c696a subido a recuperacion/multimedia-noviembre antes de desarrollar: incluye cambios Firebase y todas las fuentes de agentes recuperadas, sin builds/cachés/credenciales. main sigue en a9713fc y su workflow43654834529 terminó correctamente; el worktree QA permanece en esa instantánea.

| Tema | Estado real de esta ronda |
| --- | --- |
| Ecosistema, Kotlin/Compose y multimedia | Publicados en main; guías Nexo revisadas sin reiniciar proyectos |
| Asincronía y sensores | Publicados; pruebas y limitaciones del hito anterior conservadas |
| Diseño | Desarrollado: build/lint ambos,5JVM y1Compose; matriz visual amplia pendiente |
| Persistencia | Desarrollado: build/lint ambos,1JVM y5instrumentadas; migración/relaciones/DataStore/caché/UI,APIreal y reinicio comprobados |
| Redes | Desarrollado: build/lint ambos,11JVM solución y1Compose; Ktor contra servidor propio; Bluetooth físico pendiente |
| Cartografía | Desarrollado: build/lint ambos,14JVM solución,1Compose y6HTTP; búsqueda/ruta propias desdeAndroid; seguimiento físico/fondo pendiente |
| Firebase | Corrección en validación final: sesiones/selector, reglas trim y pruebas ampliadas; no declarar cerrado aún |
| Segundo plano | Cerrado localmente: build/lint ambos,3JVM y3instrumentadas; planificación real/reintento/aviso/cancelación/MediaSession; FCMreal/Doze/reinicio pendientes |
| IA | Teoría/guía/clientes/backend completos;4JVM y5pruebas servidor; preparación versionada y respuesta parcial; UI pendiente |
| Calidad | Desarrollado: build/lint ambos,7JVM y3Compose solución; ReleaseR8 y AAB construidos sin firma privada/publicación |
| Multimedia avanzada | En elaboración en borradores, fuera de navegación |

Se integran navegación y descargas de los diez temas de ampliación desarrollados, todavía en rama de recuperación. El siguiente cierre requiere terminar Firebase/IA, exportar/revisar PDF y web globales, y completar multimedia avanzada antes de afirmar encargo terminado. Los borradores no se integran indiscriminadamente en main.

Revisiones independientes de prácticas realizadas sobre las fuentes nuevas: sin bloqueantes didácticos en Diseño/Persistencia/Calidad/IA y guiadas del itinerario; se corrigieron UTF8 de README/comentarios y precisión de borde de geocerca. No equivalen a aprobación docente. Hardware físico, TalkBack y servicios reales se conservan como pruebas pendientes específicas, no como cobertura inferida.

### Firebase cerrado sobre la corrección final

- SesionUsuario versiona cambios de identidad, no la salida a Photo Picker. Cada lectura/callback conserva turno y valida también UID real antes de publicar; el turno del selector se captura antes de abrirlo y se restaura con la sesión al recrear.
- Se limpian notas, imagen, borrador, contraseña y carga al cambiar de cuenta. La reanudación de la misma cuenta no borra el trabajo activo ni resetea su carga; callbacks de login anteriores no alteran el estado de otra identidad. Scaffold respeta IME y tests desplazan acciones.
- Ambos proyectos: assemble/lint y APK de test correctos. Solución4JVM: límites/lectura acotada e intercalados de sesión. Reglas13checks reales tras trim: acceso propio/ajeno/anónimo, datos inválidos, tiempo real y archivos.
- Suite final3instrumentadas correctas,0fallos/0omitidas,35.358s en5556: cuenta/notas/archivo/cambioA→B; PhotoPicker REAL con imagen sintética→subida→vista previa y limpiezaB; lecturaURI retenida→cambioA/B→liberación→3descargasBsin archivoA. Provider diferido existe exclusivamente en androidTest.
- No se activaron servicios de producción, proveedores externos ni facturación. Captura y log: qa-captures/firebase-picker-imagen.png y firebase-cierre-instrumentadas.log.

### Integración local de diez ampliaciones

- IA cerrada localmente: ambos proyectos assemble/lint correctos;4JVM (validación y preparación concurrente),5checksPython (HTTP/contrato/límites/salida parcial) y1Compose API36 (respuesta simulada,error,recuperación,cancelación sin resultado tardío) correctos. No se hizo inferencia remota ni envío a proveedor real. La preparación de fotos tiene trabajo/versionado y bloquea consulta hasta terminar; Quitar y sustitución descartan resultados antiguos.
- Web de esta ronda:126páginas,1390enlaces,1080fragmentos y587entradas búsqueda; navegador2tamaños/2temas/teclado,14paresPDF y35ZIP íntegros. Auditoría independiente32variantes de8guiadas y8páginasPDF sin cortes/solapamientos. Diagrama de persistencia ahora ampliable.
- Se detectó ZIPIA anterior a la corrección de salida parcial: se reempaquetará antes de publicar. check_site_ui ahora compara cada archivo descargado con la fuente, además de estructura/ausencia de generados. Empaquetador poda node_modules/cachés antes de recorrerlos.
- Multimedia avanzada ya tiene pruebas CameraX1/1 (foto/vídeo virtual),Editor3/3 (recorte,Transformer,SoundPool) y MIDI2JVM; documentación/streaming todavía en elaboración fuera del sitio. No se declara aún completo.

- IA multimodal completada: suite final2instrumentadas correctas,20.341s,sin omisiones. PhotoPicker real con imagen sintética→preparaciónJPEG→backendsimulado confirma texto+imagen; Quitar→consulta solo texto. Captura compacta oscura revisada, tema restaurado. Primer timeout no persistente se estabilizó cerrando IME en el test; no hubo cambio de producción ni inferencia real.
- Multimedia avanzada en borrador ya tiene CameraX1/1,Editor4/4,MIDI2JVM y StreamTest1/1 contra emisor propioRTSP/TCP. Documentación y QAvisual aún en cierre. Se protege ese código también en la rama de recuperación antes de integrarlo; no se publica el borrador.

## Cierre de multimedia avanzada e integración curricular

- Catorce páginas y dos guías Nexo con seis tareas reales entre iniciales, pistas cerradas, comprobaciones y soluciones separadas. Cuatro proyectos ejecutables trasladados conservando historial a proyectos/multimedia-avanzada; no quedan fuentes pendientes en borradores.
- Build/lint de las cuatro etapas correctos. Captura: una instrumentada produce JPEG y MP4 con cámara virtual. Editor: tres JVM por etapa y cuatro instrumentadas normales (recorte PNG real, Transformer, SoundPool y primer fotograma), más una ejecución RTSP/TCP contra MediaMTX/FFmpeg propios. La salida se decodificó independientemente: H.264 192×144 sin audio.
- Hallazgo independiente corregido: reinicio de primer fotograma y métricas al comenzar otra sesión de reproducción. Prueba RTSP afectada repetida correctamente. Editor revisado en claro, oscuro, fuente 150 % y horizontal; recreación reinicia la selección temporal, limitación explicada en guía/README.
- Auditoría didáctica independiente del índice completo: correspondencia de los tres temas principales y las once ampliaciones, sin bloqueantes nuevos confirmados; guías con prerrequisitos enseñados, iniciales sin soluciones de sus retos y actividades autónomas sin recetas. No equivale a aprobación docente.
- Integración local validada: 39 ZIP regenerados; 15 PDF por tema/recursos y global de 262 páginas. Zensical sin incidencias. Enlaces: 140 páginas, 1465 enlaces, 1121 fragmentos y 651 entradas de búsqueda. Navegador: 140 páginas, dos tamaños, dos temas, teclado, 15 pares PDF y 39 ZIP íntegros y contrastados con sus fuentes. Revisión independiente adicional: ocho variantes web de las dos guías multimedia y cuatro páginas PDF renderizadas, sin recortes ni solapamientos confirmados. Es una revisión representativa, no inspección visual de cada página global.

### Límites de la verificación

No hay teléfono físico conectado. No se certifican captura/orientación real, GPS/seguimiento/geocercas físicos, Bluetooth clásico/BLE entre equipos, instrumento MIDI, salida audible/háptica/biometría ni TalkBack. Hay instrucciones y pruebas pendientes específicas para aula; emulador y JVM no sustituyen esas evidencias. Se han probado servicios locales Firebase y backend IA simulado; no Firebase de producción, FCM real, inferencia remota/Nano, Crashlytics de producción, credenciales de firma ni publicación en Play. No se han activado facturación ni proveedores pagados. La matriz visual Android de cada aplicación se limita a las variantes registradas; no se declara una auditoría universal de dispositivos/versiones. Run visual desde Android Studio y despliegue físico no se han automatizado.

## Incidencia de CI y corrección de la prueba IA

El primer intento integral, commit e3cdfb6 y workflow43703439971, NO se desplegó: web correcta, Android con fallo y deploy omitido. Diecinueve proyectos compilaron, pasaron JVM/lint y ensamblaron AndroidTest; el vigésimo (IA solución) falló en PreparacionTest. El latch se completaba dentro de IO antes del retorno a Main, y el test podía ejecutar resetMain demasiado pronto. No era un fallo demostrado de producción: se corrigió el test para esperar estado.first con respuesta final y carga terminada, y comprobar exactamente un envío con el adjunto nuevo. Cuatro JVM locales tras recompilar, cero fallos/omisiones. Revisión independiente del parche sin bloqueantes.

El log mostró pausas largas en mergeExtDexDebug seguidas de builds correctos, sin OOM ni errores de red; no se trató como éxito ni se ocultó el fallo. Se conserva el enlace al intento fallido. Para completar la validación con trazabilidad, CI reparte las mismas cuatro tareas Android entre cuatro grupos deterministas (10/10/10/9), registra proyecto y duración y conserva XML JUnit/lint incluso al fallar. Caché Gradle básica de GitHub, sin backend comercial; el despliegue sigue esperando el éxito de todos los grupos y de la web. Partición, YAML y Bash revisados localmente. Pendiente el resultado del nuevo workflow y Pages.

PDF del primer artefacto Linux/CI: global261 páginas, cabeceras de las140 páginas presentes. Seis páginas renderizadas y revisadas de Cuaderno/multimedia avanzada sin defectos visuales confirmados. La paginación local262 difiere sin pérdida de encabezados; la inspección visual sigue siendo representativa.

## Registro final de entrega

- Fuentes recuperadas y protegidas sin reset, clean, force push ni pérdida de cambios. Hitos relevantes: 38c696a (resguardo), b11c797 (Firebase y selector real), 48b2be9 (multimedia avanzada), e3cdfb6 (web integral local) y 33a19d5 (carrera del test IA y CI distribuida).
- Los tres temas y once ampliaciones están en navegación y exportación, con teoría, guiadas, actividades autónomas/repaso y soluciones formativas. Se conservan las comprobaciones Android y los límites específicos de cada hito registrados arriba.
- Workflow final y Pages correctos. Evidencias locales: qa-captures/web-publicada-final.log, pdf-publicados-integridad.json, ia-jvm-ci-final.json, ci-pdf-review y multimedia-final. El informe de CI conserva XML JUnit/lint por grupo; no se suben caches, APK, credenciales ni capturas QA al repositorio.
- Este último registro no modifica código, apuntes ni artefactos publicados; se versiona con skip ci para conservar el resultado verificado del código 33a19d5 sin repetir las mismas compilaciones por un archivo de estado externo a la web.
- Servicios de prueba propios detenidos, emulador auxiliar 5556 cerrado y sus cambios de tamaño/densidad revertidos. Emulador 5554 libre con configuración restaurada por el productor. El worktree QA antiguo se conserva sin borrar sus evidencias.
- No queda desarrollo curricular pendiente. Quedan las pruebas físicas/TalkBack y servicios de producción enumerados en Límites de la verificación, y la revisión docente de las evidencias que requieran aula, dispositivo y programación externa.
