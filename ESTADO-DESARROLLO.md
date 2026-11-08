# Estado del encargo integral

El alcance aprobado es `CONTENIDOS_PMDM_Android.md`: tres temas principales y once ampliaciones completos. Se autoriza implementación y publicación progresiva en `main`; no se autoriza difusión en Moodle ni modificación de otros repositorios.

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
- Pages configurado en modo Actions. Pendiente confirmar primera ejecución remota después de push.
- `scripts/test_navigation.py`, `compileall`, `package_projects.py` y `git diff --check`: correctos.
- `scripts/export_pdfs.py`: PDF ecosistema (15 páginas), recursos (4) y global (19), con portada e índice; incluye avisos de construcción y captura real del emulador. Página de la captura renderizada y revisada: imagen y pie centrados, completos y dentro de A4, sin solapamientos observados.
- `scripts/check_links.py`: 10 páginas, 77 enlaces, 60 fragmentos y 47 entradas de búsqueda en la última comprobación previa al cierre.
- `scripts/check_site_ui.py`: Chromium, escritorio/móvil, claro/oscuro, teclado, búsqueda y navegación a resultado, contraste de pestañas, ZIP descargados/inspeccionados y PDF. Figura de la captura comprobada y centrada en ambos tamaños y temas; capturas locales revisadas.
- Ambos proyectos ecosistema: `:app:assembleDebug :app:lintDebug` correctos. Lint inicial 0 errores/8 avisos (versiones fijadas y recursos reservados para completar TODO); solución 0 errores/6 avisos (versiones fijadas).
- Solución: `:app:connectedDebugAndroidTest` en Pixel_9 API 36/Android 16: 1 prueba, 0 fallos, 0 errores, 0 omitidas. Comprueba saludo, clic y mensaje.
- Revisión didáctica independiente: corregidos árbol de rutas, enlace de solución desde ZIP, referencia oficial de estado y licencia incluida en descargas. Preparado para revisión docente; no equivale a aprobación pedagógica humana.

## Pendiente

Tema central en producción: aplicación incremental «Cuaderno», notas con título y contenido, Compose, navegación tipada, ViewModel/StateFlow, HTTP y Room. Pendientes multimedia y las once ampliaciones, después revisión global. Inicio y ampliaciones muestran su estado de construcción sin enlaces a páginas inexistentes.

La ejecución por Run dentro del IDE no se ha automatizado visualmente: build y emulador se han comprobado por Gradle/ADB. No hay teléfono físico conectado; despliegue físico y funciones de hardware posteriores se documentarán sin certificarlas por emulación.

La programación y hoja de presentación externas no están disponibles en el repositorio. El alcance ya acordado permite elaborar contenidos; no se publican ni se reconstruyen correspondencias administrativas.
