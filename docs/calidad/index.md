# Calidad, pruebas, publicación y mantenimiento

**Ampliación.** Después de construir una aplicación necesitamos detectar regresiones, investigar errores y preparar versiones reproducibles. El tema principal ya contiene las comprobaciones mínimas de [Cuaderno](../desarrollo/comprobaciones.md); aquí profundizamos sin convertir cada aplicación pequeña en una infraestructura de pruebas compleja.

- [Pruebas de lógica, Repository y ViewModel](pruebas.md).
- [Pruebas de interfaz Compose](interfaz.md).
- [Depuración, inspectores y Crashlytics](depuracion.md).
- [Variantes, dependencias y R8](compilacion.md).
- [Firma, distribución y mantenimiento](publicacion.md).
- [Práctica guiada: comprobar una lista de notas](practica.md).
- [Actividades y repaso](repaso.md), con [soluciones](soluciones.md).

El laboratorio **Lista de revisión** proporciona la aplicación y deja pendientes las pruebas. Utiliza JUnit, corrutinas de prueba y MockK; no necesita cuentas, red ni una base de datos. La parte de publicación explica el procedimiento y sus controles, pero no publica una aplicación ni registra servicios externos.
