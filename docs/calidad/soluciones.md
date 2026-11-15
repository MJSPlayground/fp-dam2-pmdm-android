# Soluciones de calidad

## Actividad autónoma

[Proyecto solución — ZIP](../descargas/proyectos/calidad-solucion.zip){ .md-button }

`Versiones.kt` define `VersionInstalable` y `candidataCompatible`; `VersionesTest` comprueba las cinco decisiones del enunciado. Ejecuta esa clase desde Run en Android Studio, dentro de **test**, sin emulador ni cuentas. Los datos de huella son identificadores de prueba; no contienen material de firma real.

La decisión del ejercicio exige paquete y certificado iguales y código mayor. El nombre visible no participa. Esta política local no sustituye la validación del instalador o la tienda ni pretende soportar rotaciones de firma.

## Repaso

1. Faltan los extremos 2/3 y 80/81, el caso vacío o solo espacios y el recorte exterior. Varios títulos normales no detectan necesariamente un límite incorrecto.
2. La primera ejecuta el comportamiento del repositorio de demostración; la segunda controla respuestas para aislar el ViewModel. Ninguna acredita HTTP porque el repositorio de este laboratorio es local y el mock no realiza la operación real.
3. Son las variantes de MockK para suspensiones. El ViewModel depende del contrato de interfaz recibido por constructor, sin conocer si el colaborador es real, fake o mock.
4. Controla corrutinas y demoras asociadas a sus dispatchers de prueba. Una espera bloqueante, un hilo externo o un dispatcher con otro reloj pueden quedar fuera. La regla proporciona Main en JVM y lo restaura para no contaminar otros casos.
5. La UI podría no avisar nunca de la operación o mostrar carga permanente en otro camino. Observar el estado intermedio protege el contrato completo, no solo la lista.
6. Un fallo esperado debe informar; una cancelación puede ser la sustitución normal de una carga. Mostrarla como error produce avisos que no corresponden a un fallo del servicio.
7. Demuestra esa búsqueda y acción semántica en ese entorno. Contraste, objetivos táctiles, foco y lector requieren otras comprobaciones de ejecución y visuales.
8. El estado es una entrada y el callback una salida observable. Aún faltan pruebas de ViewModel/Repository y del arranque e integración de la aplicación completa según el cambio.
9. Mide coste, trabajo y fluidez durante una interacción reproducible. Muchas ejecuciones pequeñas no equivalen automáticamente a una interfaz lenta; una operación costosa puede bloquear con pocas recomposiciones.
10. No utiliza SQLite ni Room: sus datos se generan en memoria. Cuaderno en su solución Room sí ofrece una base que el inspector puede consultar.
11. Necesita el mapeo correspondiente a esa compilación y la configuración correcta del servicio. Hay que provocar y recibir un informe de pruebas, con versión y pila interpretables, para verificar el recorrido remoto.
12. El paquete instalable puede ser `es.ejemplo.notas.debug`, con namespace Kotlin `es.ejemplo.notas`. El código interno, por ejemplo 9, identifica una nueva entrega; el nombre, como `1.1-debug`, se presenta a las personas. El sufijo Debug no renombra automáticamente las clases.
13. Puede desactivar parte de la optimización y ocultar la causa. Investiga la referencia dinámica y conserva únicamente las clases o miembros que el contrato de la biblioteca requiera.
14. APK es instalable firmado; AAB es un formato del que el distribuidor genera APKs. La clave de firma autentica las instalaciones distribuidas; la de subida autentica las entregas a Play en su esquema de firmado gestionado.
15. Una migración, cambio de recursos o lógica puede fallar solo con datos existentes. La instalación limpia no protege conservación ni compatibilidad de la entrega anterior.
16. Solo los casos ejecutados y superados constituyen pruebas. Los omitidos representan cobertura pendiente y no deben sumarse al recuento de éxitos.
