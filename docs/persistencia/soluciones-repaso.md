# Soluciones del repaso

1. El filtro configura la presentación y se expresa con un booleano. Autores y lecturas necesitan identidad, relaciones, búsquedas y transacciones entre filas. DataStore no aporta SQL ni claves foráneas por guardar una representación textual.
2. El valor Kotlin se usa al construir un objeto nuevo. SQLite debe asignar un valor a filas que ya existían antes de la columna; además Room valida tipo, nulabilidad y predeterminado del esquema final.
3. Existe una fila de etiqueta y un vínculo por cada lectura. La clave primaria compuesta de `LecturaEtiqueta` impide repetir el mismo par; no impide que cada extremo participe en otros pares.
4. `INNER JOIN` omitiría autores sin coincidencias. `COUNT(*)` contaría la fila que produce una unión exterior aunque falte lectura. Contar `lecturaId` cuenta solo claves presentes.
5. Reunir el objeto principal y sus relacionados puede exigir varias consultas. Una transacción evita combinar versiones incompatibles del catálogo si hay una escritura entre ellas.
6. No. Una respuesta limitada no expresa que el resto haya desaparecido. Haría falta un contrato de instantánea completa, o instrucciones explícitas de borrado/versionado del recurso, y una política que distinga datos locales.
7. El mapeo trató la fila remota como si fuera dueña de todos los campos. El repositorio debe conservar el estado local del mismo ID al aplicar datos remotos. Room y Retrofit no infieren esa regla.
8. Comprueba de forma repetible el camino de error y que la caché permanece. No prueba DNS, TLS, acceso real al servicio ni el comportamiento de una radio física.
9. Borrar datos evita la actualización del esquema. La prueba suficiente crea la base anterior con registros, la cierra, abre el mismo archivo con la nueva versión y migración y comprueba datos, relaciones y nueva columna.
10. `@Inject` permite construir una clase propia desde su constructor, como `FuenteRemota`. `@Provides` construye un objeto mediante código, como la base de Room. `@Binds` elige `DatosRepository` cuando se pide `LecturasRepository`.
11. No. El alcance es el componente de aplicación dentro de ese proceso. La persistencia posterior corresponde a los archivos y la base, y otras instalaciones tienen memoria y datos independientes.
12. Concentra validación de identidad y alternancia de estado con una dependencia sustituible. Es útil si varias entradas comparten ese comportamiento. Para una coordinación pequeña utilizada una sola vez, mantenerla en el ViewModel también puede ser proporcionado.
13. Cambiar una variable de Compose no garantiza escribir el archivo. Pondría un breakpoint en `cambiarSoloPendientes` y comprobaría la llamada a `edit`, el valor entregado y después la emisión del flujo. También verificaría que se utiliza el mismo nombre de archivo y clave.
14. No, el registro pudo desaparecer o el ID ser incorrecto. Dos títulos iguales no identifican necesariamente la misma fila. La comprobación del número de filas evita confirmar una acción que no modificó nada.
