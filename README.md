# PMDM — Android

Materiales públicos de desarrollo Android para PMDM de 2.º DAM. Alumnado con conocimientos de Java que empieza Kotlin y Android.

**Publicación progresiva autorizada:** [web Android](https://mjsplayground.github.io/fp-dam2-pmdm-android/). El estado comprobado de cada hito y los pendientes figuran en [ESTADO-DESARROLLO.md](ESTADO-DESARROLLO.md); no se presenta el curso como terminado mientras falten temas.

## Alcance

El [índice aprobado](CONTENIDOS_PMDM_Android.md) fija tres temas principales y once ampliaciones. Las ampliaciones son opcionales y no introducen requisitos ocultos en el itinerario. No se publican exámenes, tareas de Moodle, información del alumnado ni planificación administrativa.

El contenido público comienza en `docs/`. Los proyectos Android, con starter y solución separados, se mantienen en `proyectos/<tema>/`. Las versiones reproducibles están en [Recursos](docs/recursos/index.md) y sus catálogos Gradle.

## Construir y comprobar

Python 3.11 y un navegador Chromium. En Windows:

```powershell
py -3.11 -m venv .venv
.\.venv\Scripts\python.exe -m pip install -r requirements.txt
.\.venv\Scripts\python.exe -m playwright install --only-shell chromium
.\.venv\Scripts\python.exe scripts\package_projects.py
.\.venv\Scripts\python.exe scripts\test_navigation.py
.\.venv\Scripts\python.exe scripts\export_pdfs.py
.\.venv\Scripts\python.exe scripts\check_links.py
.\.venv\Scripts\python.exe scripts\check_site_ui.py
```

El exportador construye Zensical, genera PDF por tema y global y reconstruye el sitio con las descargas. Los ZIP y PDF son artefactos generados; no se versionan. `zensical.toml` define el orden de navegación y los temas exportables. Las pruebas comprueban subruta pública, enlaces y fragmentos, búsqueda, temas, tamaños de pantalla y descargas. Las capturas de QA son locales.

Cada proyecto Android incluye Gradle Wrapper y un README con instrucciones de apertura desde Android Studio, ejecución y pruebas técnicas. No se requiere Gradle global. No versionar `local.properties`, cachés, APK, AAB, keystores ni credenciales.

## Publicación

`.github/workflows/validate.yml` construye y comprueba antes de desplegar en GitHub Pages desde `main`. Pages utiliza el modo **GitHub Actions**. El propietario ha autorizado la publicación progresiva; las secciones pendientes se identifican como en construcción. No se cambia la visibilidad del repositorio ni se difunde desde Moodle.

`scripts/github_pages.py status` permite consultar Pages y los últimos workflows con la autenticación del gestor Git local sin mostrarla. `enable` configura el modo Actions y requiere autorización de publicación. El workflow no necesita esa credencial local: utiliza los permisos efímeros de GitHub Actions.

## Procedencia y licencias

© Mikel Aingeru Jorge Soteras · MJS Playground.

Texto docente original: [CC BY-NC-ND 4.0](https://creativecommons.org/licenses/by-nc-nd/4.0/deed.es). Código original: [Apache 2.0](LICENSE), salvo aviso específico.

Plantilla visual, logo, codelabs y base PDF adaptados con autorización del propietario desde [ACDA](https://github.com/MJSPlayground/fp-dam2-acda), referencia local del conjunto `a60e79e2449c14b693d6db23d99b28424b9788ce`. El origen no contiene una licencia general explícita: se registra su procedencia y la autorización de reutilización, sin atribuirle una licencia de terceros inexistente. El Gradle Wrapper conserva los avisos de Gradle. Las bibliotecas externas mantienen sus propias licencias.

Los estándares compartidos se consultan en origen y no se copian ni modifican aquí.
