# 📱 Programación Multimedia y Dispositivos Móviles · Android

Materiales de **desarrollo de aplicaciones Android** para el módulo **PMDM de 2.º de Desarrollo de Aplicaciones Multiplataforma (DAM)**. Proyecto de documentación y autoaprendizaje de **MJS Playground**.

> **Estado:** repositorio en preparación. La web de GitHub Pages y los proyectos prácticos **aún no están implementados ni publicados**.

El desarrollo de videojuegos se trabaja en **otro repositorio**: aquí solo habrá aplicaciones móviles.

## Itinerario de aprendizaje

Cada tema corresponde a una unidad de trabajo impartida y se organiza en apartados prácticos:

- **El ecosistema Android y el entorno de desarrollo:** tecnologías, versiones, Android Studio, SDK, Gradle, estructura y primera aplicación.
- **Desarrollo de aplicaciones con Kotlin y Jetpack Compose:** Kotlin desde Java, Material 3, estado, navegación, arquitectura básica, HTTP/REST, Room, Intents y ejecución.
- **Multimedia, localización y notificaciones en Android:** permisos, imágenes y conversión, audio/vídeo, animaciones, ubicación y mapas OpenStreetMap/MapLibre, notificaciones locales.

La web contará además con temas propios de **Ampliación**: Kotlin/Compose avanzado, diseño adaptable, arquitectura y persistencia avanzadas, redes y Bluetooth, Firebase, segundo plano, IA, multimedia avanzada, cartografía avanzada, sensores y calidad/publicación.

El índice íntegro aprobado, con contenidos y prácticas previstas, se mantiene en [`CONTENIDOS_PMDM_Android.md`](CONTENIDOS_PMDM_Android.md).

## Cómo se utilizarán los materiales

La documentación se diseñará para el **autoaprendizaje**: explicaciones comprensibles, código contextualizado, prácticas guiadas, actividades de consolidación y repaso. Se parte de conocimientos previos de **Java**, pero no de Kotlin.

Las entregas y pruebas evaluables se gestionarán desde **Moodle**. Este repositorio público **no** contendrá exámenes reservados, soluciones de evaluaciones, datos personales del alumnado ni planificación docente confidencial.

## Tecnologías de referencia

- **Kotlin**, **Android Studio**, **Gradle Kotlin DSL** y Android SDK.
- **Jetpack Compose** y **Material 3** para la interfaz.
- **ViewModel**, estado observable y separación básica de responsabilidades.
- **Retrofit / HTTP REST** para conectividad y **Room / SQLite** para datos locales.
- **Media3**, permisos, geolocalización, **OpenStreetMap / MapLibre** y notificaciones locales.

Las versiones exactas y las librerías de ejemplo se fijarán y verificarán antes de desarrollar los materiales correspondientes. No se incluye todavía un proyecto Android ejecutable.

## Web y desarrollo

La web utilizará **Zensical Modern**, heredando la estructura visual de [ACDA](https://mjsplayground.github.io/fp-dam2-acda/) y diferenciándose mediante una **paleta verde**, con soporte de tema claro/oscuro, búsqueda, navegación y PDF por tema y completo.

**GitHub Pages todavía no está configurado.** Los scripts, el generador, la estructura `docs/` y los proyectos de ejemplo se incorporarán cuando comience el desarrollo real del sitio, reutilizando la plantilla común en lugar de duplicarla prematuramente.

Las instrucciones para agentes de desarrollo y documentación están en [`AGENTS.md`](AGENTS.md). Los estándares transversales se mantienen en el repositorio privado `MJSPlayground/ai-workspace`, accesible únicamente al personal autorizado.

## Autoría y licencias

**© Mikel Aingeru Jorge Soteras.**

El repositorio conserva la licencia **Apache 2.0** existente para el código y los archivos sin aviso específico. Los **materiales docentes textuales originales** se indicarán expresamente bajo **CC BY-NC-ND 4.0**; los ejemplos y proyectos de código podrán mantener permisos distintos. No incorporar material de terceros sin comprobar sus condiciones de uso y atribución.
