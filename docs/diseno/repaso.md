# Actividades y repaso de diseño

## Actividades autónomas

### Ordenar una selección

La galería quiere presentar primero las láminas favoritas y después las restantes. Dentro de cada grupo, el orden debe ser por ID creciente. Si una entrada repite un ID, conserva la primera aparición, aunque sus demás datos sean distintos. El resultado no modifica la entrada.

Con el catálogo de IDs 1, 2, 3 y 4 y favoritos 2 y 4, el resultado es 2, 4, 1, 3. Volver a añadir la primera lámina no cambia la cantidad de resultados. Una lista vacía sigue vacía.

### Verificar dos colores opacos

Una herramienta de apoyo al diseño recibe dos colores RGB opacos y calcula su relación de contraste. Debe dar el mismo resultado al invertir primer plano y fondo. Negro y blanco producen 21:1 y un color consigo mismo produce 1:1.

Utiliza el cálculo de luminancia relativa y relación de contraste de [WCAG](https://www.w3.org/WAI/WCAG22/Understanding/contrast-minimum.html). La herramienta debe devolver un valor numérico, no decidir que toda una pantalla es accesible por una pareja de colores. No maneja transparencias: esas requieren obtener primero el color compuesto.

[Soluciones de las actividades — ZIP](../descargas/proyectos/diseno-solucion.zip){ .md-button }

## Repaso razonado

1. ¿Qué diferencia hay entre `GridCells.Fixed(2)` y `GridCells.Adaptive(160.dp)`? ¿Cómo ocupa un encabezado todas las columnas?
2. ¿Por qué una categoría o una fila usa una clave estable? ¿Sería correcto usar siempre la posición al filtrar?
3. Una tableta muestra la app en una ventana de 500 dp de ancho. ¿Qué decisión del laboratorio corresponde? ¿Basta conocer su modelo físico?
4. ¿Qué hace `PullToRefreshBox` por sí mismo y qué responsabilidad conserva la aplicación? ¿Por qué existe además un botón Actualizar?
5. Se pulsa Actualizar y una miniatura remota sigue siendo la anterior. ¿Implica que Coil ha fallado? Explica el contrato del laboratorio y el papel de la caché.
6. ¿Por qué `AsyncImage` puede tener descripción nula en una tarjeta y necesitarla en otro contexto? ¿Qué información ofrece el título?
7. En una transición compartida todas las tarjetas usan la clave `"foto"`. ¿Qué identidad falta? ¿Para qué sirve el ámbito de visibilidad animada?
8. ¿Qué cambia si `AnimatedContent` ignora el parámetro de su lambda y siempre lee el estado exterior más reciente?
9. ¿Puede `remember` asegurar favoritos después de terminar el proceso? ¿Qué diferencia hay con restaurar una configuración y con guardar en disco?
10. Una aplicación concatena texto español con un importe formateado manualmente con punto decimal. ¿Qué parte corresponde a recursos y cuál al formato regional?
11. ¿Cuál es la diferencia entre nombrar una acción y describir su estado? ¿Qué anunciarías para guardar una lámina concreta?
12. ¿Superar lint demuestra que TalkBack, contraste, fuente al 200 % y transiciones son correctos? ¿Qué pruebas de ejecución faltan?

[Respuestas y referencias de código](soluciones.md)
