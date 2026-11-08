# AGENTS.md — PMDM · Android (2.º DAM)

## Ámbito y fuentes de verdad

Este repositorio contiene **exclusivamente materiales de desarrollo de aplicaciones móviles Android** del módulo PMDM. El bloque de videojuegos (Unity/C#) corresponde a otro repositorio.

Leer, por este orden, antes de realizar cambios:

1. `CONTENIDOS_PMDM_Android.md`: índice curricular **acordado**. Mantener la estructura, el alcance, las ampliaciones y la progresión de conceptos. No añadir o suprimir temas ni ampliar el itinerario obligatorio sin validación docente.
2. `README.md` y archivos reales del proyecto: situación del repositorio y decisiones ya implementadas.
3. Estándares transversales del repositorio privado [`MJSPlayground/ai-workspace`](https://github.com/MJSPlayground/ai-workspace): `AGENTS.md`, `WORKING-AGREEMENT.md`, `TEACHING-STANDARD.md` y las skills docentes aplicables. **Si no existe acceso, indicarlo; no inventar sus reglas ni bloquear tareas sencillas por ello.**
4. Hoja de presentación y programación 2026/2027 del módulo (aportadas en el proyecto ChatGPT, no publicadas aquí): referencia para RA, CE, horas e instrumentos. Si no están disponibles, solicitar o señalar el dato pendiente cuando sea indispensable.

El índice es la base aprobada de contenidos; la redacción concreta, las bibliotecas y las versiones deben contrastarse con documentación oficial reciente. No tratar las propuestas técnicas como decisiones ya validadas.

## Identidad, web y publicaciones

- **Una UT impartida equivale a un tema visible:** «El ecosistema Android y el entorno de desarrollo», «Desarrollo de aplicaciones con Kotlin y Jetpack Compose» y «Multimedia, localización y notificaciones en Android».
- Además, incorporar los temas de **Ampliación** del índice como temas autónomos, señalizados claramente. Son material complementario; no adelantarlo como requisito de la ruta principal.
- En **web, navegación, títulos, PDF y rutas públicas**, usar nombres de contenido: **no** mostrar `UT5`, `UT6`, `UT7`, numeración de unidades ni numeración de bloques. Evitar números en slugs y nombres de paquetes.
- No crear ninguna sección del proyecto intermodular. Los conocimientos que se necesiten deben existir en los temas o sus ampliaciones; el proyecto se gestiona fuera de esta web.
- No trasladar al sitio ni al repositorio público la tabla de correspondencia administrativa RA/CE, programación interna, exámenes, rúbricas, entregas evaluables de Moodle, soluciones reservadas o información del alumnado.
- Usar la **misma base visual y tecnológica que ACDA** (Zensical Modern y plantilla común, cuando se apruebe la implementación), cambiando la paleta a **verde**, con soporte claro/oscuro, navegación accesible, búsqueda y PDF por tema y global.
- Verde orientativo: principal `#2E7D32`, oscuro `#1B5E20`, acento `#81C784`. Verificar contraste, no copiar CSS a ciegas.
- En el pie público: **© Mikel Aingeru Jorge Soteras**. «MJS Playground» puede aparecer como marca.
- **Encargo integral autorizado (2026-11-08):** desarrollar todos los contenidos del índice y publicar progresivamente en GitHub Pages desde `main`, con commit y push por hitos verificados. Las secciones pendientes se identifican como «En construcción» o se mantienen fuera de navegación. No requiere aprobación entre temas. No autoriza difusión desde Moodle, cambios de visibilidad ni publicaciones ajenas a esta web.

## Modelo didáctico

- Alumnado de 2.º DAM: **conoce Java, pero empieza Kotlin y Android**. Introducir Kotlin mediante comparaciones breves con Java; explicar responsabilidades y flujo de ejecución, no solo sintaxis.
- Material de **autoaprendizaje**, suficiente para trabajar autónomamente con orientación de ritmo del docente.
- Cada tema comienza, como máximo, con **un índice breve**. Evitar páginas introductorias de objetivos, justificaciones genéricas, relleno y largos preámbulos.
- Teoría accesible y rigurosa, ejemplos pequeños ejecutables, **prácticas guiadas** con resultado observable, retos autónomos y ejercicios de repaso al cierre. No imponer una práctica por cada subapartado si dispersa el trabajo.
- Favorecer una **aplicación incremental** en UT6 para no crear un proyecto Android nuevo tras cada concepto. UT7 admite prácticas pequeñas e independientes (imagen, multimedia, mapa y notificación).
- Explicar antes de mostrar el código: qué problema resuelve, por qué se utiliza, cómo verificarlo y qué errores son frecuentes. Cuando se proporcione una clase completa, que tenga imports, dependencias y contexto necesarios para compilar.
- Las prácticas evaluables se entregan en Moodle según la programación; no publicar sus soluciones en el repositorio. Las soluciones **formativas públicas** deben identificarse y diferenciarse claramente de los proyectos iniciales.
- No inventar capturas, vídeos, resultados de pruebas ni proyectos descargables. Usar capturas reales y medios con permiso/licencia; registrar los recursos genuinamente necesarios pendientes **solo cuando empiece su elaboración**.
- Evitar que la teoría sea una lista de APIs: construir conceptos por pasos y con ejemplos adecuados al tiempo real de clase.

## Alcance y cobertura de aprendizaje

- La ruta presencial principal dispone de **29 horas**: introducción 2 h, desarrollo Android 18 h, multimedia/localización/notificaciones 9 h. Las 17 horas del proyecto intermodular son para desarrollar, no para impartir un nuevo temario.
- UT6: Kotlin, Android Studio/SDK/Gradle, Compose/Material 3, estado y navegación, ViewModel/Repository sencillo, HTTP/REST, Room, Intents e intercambio básico de información; pruebas y despliegue **mínimos** que exige el RA2.
- UT7: permisos necesarios, selección/captura y **transformación/conversión** de imágenes, Media3, animaciones, GPS/ubicación y mapas OSM/MapLibre, notificaciones locales.
- **GPS, mapas y notificaciones no sustituyen los CE de multimedia** del RA3: reservar evidencias para captura, procesamiento, conversión, eventos, animaciones, reproductores, depuración y documentación.
- No eliminar del itinerario las comprobaciones de interacción con emulador, despliegue en **dispositivo real** y documentación mínima del RA2, ni depuración/documentación del RA3.
- El CE sobre **comunicación con dispositivos inalámbricos** necesita una evidencia verificable; la simple consulta de una API remota no acredita automáticamente comunicación con otro dispositivo. Marcar cualquier insuficiencia para revisión docente, sin dar por cumplido el CE.
- Bluetooth BLE/GATT, Firebase, FCM, WorkManager, IA, arquitectura avanzada, sensores, publicación avanzada y pruebas automatizadas complejas: **ampliaciones** salvo nuevo acuerdo.

## Tecnología, código y validación

- Stack de referencia: **Kotlin, Jetpack Compose, Material 3, Gradle Kotlin DSL, Room/SQLite, Retrofit/HTTP, Media3, OpenStreetMap/MapLibre**.
- Antes de escribir tutoriales ejecutables, comprobar en la **documentación oficial** Android Studio estable, AGP, Kotlin, Compose BOM, Navigation, Room/KSP, Retrofit, Media3 y MapLibre. Fijar combinaciones compatibles y documentarlas en un único lugar. **No inventar versiones, APIs, métodos ni dependencias.**
- Diferenciar mapa OpenStreetMap, SDK y **proveedor de teselas**; respetar atribución y políticas de uso, sin sobrecargar los servidores públicos.
- Solicitar permisos cuando corresponda; priorizar privacidad, accesibilidad básica, gestión de errores y estados de carga. No almacenar claves API o información sensible en repositorios, código cliente ni proyectos base.
- Cada proyecto de ejemplo debe poder **abrirse en Android Studio, sincronizar Gradle, compilar y ejecutarse** en el entorno objetivo. Cuando una funcionalidad necesite dispositivo físico (GPS, cámara, notificaciones, Wi-Fi), documentar las limitaciones del emulador y la prueba pendiente.
- Verificar enlaces internos, snippets, componentes, código base y solución, resultados de éxito/error y claridad del flujo completo. Diferenciar pruebas realizadas de las que no se han podido ejecutar.

## Forma de colaborar

- Actuar con criterio técnico independiente; señalar problemas de alcance, versiones y cobertura curricular antes de incorporarlos. Respetar las decisiones docentes confirmadas.
- Trabajar en cambios coherentes, con **commits y push** cuando estén autorizados. No abrir PR por rutina; usar ramas de trabajo cuando haya publicación automática o contenido todavía sin validar, y no desplegar sin autorización.
- Mantener el repositorio sencillo: **no** crear carpetas vacías, scripts, workflows, código Android ficticio, planificaciones duplicadas o documentación burocrática por adelantado.
- Al terminar cada encargo, resumir qué se ha cambiado, qué se ha comprobado de verdad y qué queda pendiente.
