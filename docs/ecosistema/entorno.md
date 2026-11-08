# Preparar Android Studio y el emulador

## IDE, SDK y herramientas de compilación

**Android Studio** es el entorno donde editaremos, ejecutaremos y depuraremos. El **Android SDK** contiene las APIs de cada plataforma y herramientas como ADB, que comunica el ordenador con dispositivos. El **JDK** ejecuta las herramientas Java y Gradle. Son piezas relacionadas, pero instalar una no sustituye automáticamente a las otras.

Descarga una versión estable desde la [web oficial de Android Studio](https://developer.android.com/studio). Sigue su [guía de instalación y requisitos](https://developer.android.com/studio/install): el emulador necesita recursos adicionales respecto al editor. En un equipo del centro, comprueba primero si el entorno está instalado y si tienes permisos; no sustituyas una instalación compartida por iniciativa propia.

En el asistente inicial selecciona una instalación que incluya el SDK y el emulador. Anota la ruta del SDK que muestra el asistente. No guardes el SDK dentro del repositorio ni en una carpeta sincronizada como proyecto de clase.

## Instalar los componentes del SDK

Desde la pantalla inicial abre **More Actions → SDK Manager**; dentro de un proyecto también puedes acceder desde **Tools → SDK Manager**. Los nombres o la posición pueden variar entre versiones del IDE.

En **SDK Platforms**, instala Android API 36 para los proyectos del curso. En **SDK Tools**, comprueba que están instalados Android SDK Platform-Tools, Android Emulator y los Build-Tools que requiera Gradle. No necesitas el NDK para las prácticas introductorias.

La [configuración común del curso](../recursos/index.md) identifica las versiones fijadas. Al abrir un proyecto proporcionado, conserva sus scripts Gradle: aceptar una actualización automática del plugin puede cambiar requisitos de JDK y romper la reproducción de la práctica.

En **Settings → Build, Execution, Deployment → Build Tools → Gradle**, revisa **Gradle JDK**. Usa el JBR del IDE compatible con Gradle o un JDK compatible; no selecciones un Java instalado al azar. La base AGP 8.13 exige como mínimo JDK 17 y Gradle 8.13. El JDK que ejecuta Gradle y la versión del bytecode configurada para la app son decisiones distintas.

## Crear un dispositivo virtual

Abre **Tools → Device Manager**. Crea un dispositivo con un perfil de teléfono, elige una imagen de sistema API 36 y descarga la imagen si se solicita. Selecciona una arquitectura compatible con tu equipo: en equipos Windows x64 suele corresponder una imagen x86_64.

Ponle un nombre reconocible, como `TelefonoCurso`, y termina el asistente. Pulsa el botón de inicio del dispositivo. Espera a ver la pantalla de inicio de Android: que exista una entrada en Device Manager no significa que el sistema ya esté arrancado.

La primera puesta en marcha puede tardar más que las siguientes. No ejecutes varios dispositivos simultáneos en un equipo con poca memoria. Puedes separar el emulador en su propia ventana si la integración en el IDE deja poco espacio al editor.

## Virtualización y errores de Windows

La aceleración permite ejecutar las instrucciones del sistema virtual de forma eficiente. Comprueba si la virtualización está habilitada en el equipo y sigue la [guía oficial de aceleración del emulador](https://developer.android.com/studio/run/emulator-acceleration). En Windows, el backend compatible depende de las características habilitadas y del procesador. No instales antiguos tutoriales de HAXM como solución universal.

Si la virtualización está desactivada en firmware, necesitarás acceso a BIOS/UEFI o al administrador del centro. Activar características del sistema puede requerir reiniciar. No desactives protecciones del equipo para resolver el problema por ensayo y error.

| Síntoma | Qué comprobar primero | Cómo distinguir el resultado |
| --- | --- | --- |
| El emulador no arranca | Mensaje de Device Manager, espacio libre y aceleración | El dispositivo llega a la pantalla de inicio |
| Arranca, pero el IDE se queda sin memoria | Cerrar otros emuladores y programas; revisar requisitos | El editor y el dispositivo responden sin pausas continuas |
| El proyecto no sincroniza | Primera causa en la ventana Build, red y Gradle JDK | Termina la sincronización sin errores |
| `SDK location not found` | SDK instalado y ruta local del equipo | El IDE reconoce la plataforma y las herramientas |
| Falta una plataforma | Instalar la API solicitada en SDK Manager | Desaparece el error concreto de plataforma |
| No aparece un teléfono USB | Depuración USB, cable de datos, autorización RSA y controlador | El selector de dispositivos muestra el teléfono autorizado |

No confundas un fallo de descarga de dependencias con un error Kotlin. Guarda el primer mensaje relevante, la acción que lo desencadenó y las versiones utilizadas. La última línea «build failed» indica el resultado, pero normalmente no explica la causa.

## Crear y abrir proyectos

Para explorar una plantilla, elige **New Project → Empty Activity** con Jetpack Compose, lenguaje Kotlin y Gradle Kotlin DSL. Usa un nombre sin espacios para la carpeta y un identificador como `es.mjsplayground.pmdm.inicio`. Las plantillas evolucionan: la práctica se realiza con el proyecto proporcionado para que los archivos coincidan con la explicación.

Para abrirlo, descomprime el ZIP en una carpeta de trabajo y elige **Open** sobre la carpeta que contiene `settings.gradle.kts`. Espera a la sincronización. No abras únicamente `MainActivity.kt`: el IDE necesita la configuración del proyecto para reconocer las dependencias.

Cuando la sincronización termine, selecciona el módulo **app** y el dispositivo iniciado en la barra de ejecución. Pulsa **Run**. La primera compilación puede descargar dependencias; las siguientes reutilizan la caché. El resultado esperado es una aplicación instalada y abierta en el dispositivo, no solo una línea verde en Build.
