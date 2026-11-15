# Consolidación autónoma: catálogo de lecturas

Una biblioteca necesita consultar un pequeño catálogo aun cuando no dispone de Internet. Cada lectura tiene un título, un autor y etiquetas compartidas con otras lecturas. La persona puede marcarla como leída y elegir ver solo las pendientes; esa elección debe mantenerse al volver a abrir la aplicación.

Parte de una copia del [proyecto inicial de Lecturas](../descargas/proyectos/persistencia-inicial.zip). La configuración, los datos de demostración y la interfaz permiten concentrarte en completar el comportamiento. Decide cómo comprobar el conjunto sin seguir la secuencia de la guía.

## Requisitos observables

- La demostración local contiene dos lecturas del mismo autor. La primera tiene Android y Kotlin; la segunda, Android. Repetir la carga no duplica lecturas ni vínculos.
- Marcar una lectura conserva su identidad, cambia su estado y permite devolverla a pendiente. No puede modificar otra lectura por tener el mismo título.
- El filtro oculta las lecturas terminadas sin eliminarlas. Su selección y las marcas se mantienen después de finalizar el proceso.
- Actualizar incorpora las cinco publicaciones del recurso remoto configurado como fichas, manteniendo las decisiones locales de lectura. Una ausencia en ese lote parcial no elimina datos anteriores.
- Si falla la actualización, la persona entiende el fallo y conserva su catálogo; después puede reintentar.
- La actualización de una instalación con datos antiguos conserva autores y lecturas y asigna el estado inicial pendiente a las filas que no tenían esa información.

## Evidencias y límites

Entrega un registro breve con entradas y resultados: una etiqueta compartida, otra exclusiva, un cambio de estado, un reinicio, una actualización repetida y un fallo recuperado. Incluye el resultado de la prueba de migración desde la base anterior. Distingue la API controlada de pruebas y el servicio HTTPS real.

El servicio remoto es de demostración y no conserva tus marcas personales; no se requiere subida ni sincronización entre varios dispositivos. La aplicación no debe solicitar permisos de ubicación, cámara o acceso general a archivos.

## Solución de referencia

[Catálogo Lecturas completo — ZIP](../descargas/proyectos/persistencia-solucion.zip). Su README identifica el arranque, los datos y las comprobaciones correspondientes a estos requisitos.
