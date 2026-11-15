# Actividades y repaso de calidad

## Actividad autónoma: una candidata de actualización

Una herramienta interna compara una versión instalada con una candidata antes de entregarla al equipo de pruebas. Para este ejercicio acepta la candidata únicamente si conserva paquete y huella del certificado de firma y su código de versión es mayor. El nombre visible puede cambiar libremente y no sustituye esa comparación.

Cada versión dispone de paquete, huella pública de certificado, código entero y nombre. No se utilizan claves privadas ni contraseñas. La función devuelve una decisión sin instalar nada y sin alterar las versiones recibidas.

Comprueba al menos: código mayor con misma identidad; código igual aunque aumente el nombre; código menor; otro paquete; y otra firma. La política simplificada no modela rotación de certificados ni todos los mecanismos de una tienda. El objetivo es separar los datos que identifican una entrega y escribir pruebas discriminantes.

[Solución y pruebas — ZIP](../descargas/proyectos/calidad-solucion.zip){ .md-button }

## Repaso razonado

1. Una prueba usa `assertTrue(tituloValido("Una nota"))` y pasa. ¿Qué límites importantes siguen sin comprobarse?
2. ¿Qué diferencia hay entre ejecutar `DemoRepository` y sustituir `NotaRepository` por un mock? ¿Cuál de las dos pruebas acredita acceso HTTP real?
3. ¿Por qué `coEvery` y `coVerify` aparecen en funciones suspendidas? ¿Qué debe conocer el ViewModel para recibir el doble?
4. ¿Qué controla `runTest` y qué trabajo puede quedar fuera de su reloj virtual? ¿Por qué Main se instala y restaura con una regla?
5. El estado final de una prueba es correcto, pero nunca se comprueba carga. ¿Qué regresión podría pasar inadvertida?
6. Un error de consulta desaparece al reintentar, pero una cancelación también se convierte en error. ¿Por qué conviene distinguirlos?
7. Una prueba instrumentada encuentra el botón y hace clic. ¿Demuestra contraste, tamaño táctil y orden de lectura con TalkBack?
8. ¿Por qué una pantalla que recibe estado y eventos puede probarse sin crear un Repository? ¿Qué integración queda por comprobar?
9. Layout Inspector muestra muchas recomposiciones. ¿Qué debes observar antes de cambiar la arquitectura?
10. ¿Por qué Database Inspector no puede mostrar una tabla de Lista de revisión? ¿Qué proyecto de estos materiales sí proporciona Room?
11. ¿Qué necesita Crashlytics para interpretar una pila de una Release ofuscada? ¿Una compilación correcta demuestra recepción de informes?
12. Explica `applicationId`, namespace, `versionCode` y `versionName` con un ejemplo de Debug y Release.
13. ¿Qué problema tiene añadir una regla R8 que conserva todas las clases para resolver un fallo de reflexión no investigado?
14. ¿Qué diferencia hay entre APK y AAB, y entre clave de firma y clave de subida?
15. ¿Por qué hay que probar la actualización con datos de una versión anterior y no solo una instalación limpia?
16. Un informe dice «todo verde», pero hay seis pruebas omitidas. ¿Qué evidencia puede afirmarse y cuál no?

[Respuestas](soluciones.md)
