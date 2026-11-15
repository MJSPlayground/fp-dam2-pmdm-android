# Estado del encargo integral

El alcance aprobado es `CONTENIDOS_PMDM_Android.md`: tres temas principales y once ampliaciones completos. Se autoriza implementación y publicación progresiva en `main`; no se autoriza difusión en Moodle ni modificación de otros repositorios.

## Situación actual — contenido integral validado, publicación en curso

Las fuentes de los tres temas principales y las once ampliaciones están desarrolladas. Firebase está cerrado con pruebas de sesión y selector real; IA con consulta simulada de texto e imagen. Multimedia avanzada está integrada con sus cuatro proyectos y QA local completado. La publicación de esta ronda sigue protegida en la rama de recuperación: no se declara desplegada hasta comprobar Actions y Pages.

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
