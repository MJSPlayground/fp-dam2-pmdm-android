# Repaso del entorno Android

Resuelve las preguntas usando los archivos y mensajes del IDE. Las respuestas de referencia están al final, separadas de los enunciados.

## Elegir y justificar

1. Un equipo conoce Kotlin, necesita leer sensores Android y solo publicará para Android. Otro tiene una web de consultas que debe abrirse por enlace en cualquier teléfono. Propón una tecnología para cada caso y explica una limitación de tu elección.
2. ¿Por qué una app no debe suponer que todas las imágenes quepan en memoria aunque sus archivos ocupen poco espacio?
3. ¿Qué prueba añadirías en un teléfono físico después de simular una ubicación en un emulador? Explica qué demuestra cada una.

## Leer un proyecto

4. Encuentras `app/src/main/java/.../MainActivity.kt`. ¿Es un archivo Java? ¿Dónde buscarías el texto de un botón si la aplicación utiliza recursos?
5. Una compañera abre solo `MainActivity.kt` y el editor no reconoce `ComponentActivity`. ¿Qué debe abrir y qué proceso debe esperar antes de modificar imports?
6. Señala qué interviene en cada paso: resolución del modelo del proyecto en el IDE; generación del APK; instalación en el dispositivo. ¿Cuál de esos pasos prueba que un botón responde?

## Razonar sobre compatibilidad

7. Un proyecto declara `minSdk = 26`, `targetSdk = 36` y `compileSdk = 36`. Explica si puede instalarse en API 25, 26 y 37. ¿Garantiza esto que una función nueva de API 33 pueda ejecutarse directamente en API 26?
8. Un teléfono recibe una actualización del sistema. ¿Ha cambiado también la versión de Gradle del proyecto del ordenador? Justifica la respuesta.
9. ¿Por qué aumentar `compileSdk` no debe hacerse como remedio para cualquier error de compilación?

## Diagnosticar

10. La sincronización informa `SDK location not found`. ¿Qué comprobarías antes de cambiar código Kotlin?
11. El emulador existe en Device Manager, pero Run no puede instalar la app. Describe dos comprobaciones que distingan un problema de dispositivo de uno de compilación.
12. La pantalla muestra el texto modificado, pero al girar desaparece un mensaje que habías cambiado pulsando un botón. ¿Se ha guardado necesariamente ese mensaje? Distingue memoria, recreación y almacenamiento.

## Cambiar y comprobar

Sobre tu copia de la práctica, cambia el mensaje de bienvenida por una frase tuya y aumenta moderadamente el espacio entre los elementos. Comprueba que sigan siendo legibles en orientación vertical y horizontal y en claro y oscuro. Explica qué archivo modificaste para cada cambio. Este reto no requiere añadir bibliotecas ni otra actividad.

[Consultar las respuestas y la solución de referencia](soluciones-repaso.md).
