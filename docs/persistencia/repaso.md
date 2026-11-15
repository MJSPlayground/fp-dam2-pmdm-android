# Repaso de persistencia y arquitectura

Responde explicando el contrato y una comprobación que permita distinguir el resultado correcto de uno que solo parece funcionar.

1. ¿Por qué el filtro de pendientes pertenece a una preferencia, mientras autores y lecturas pertenecen a Room? ¿Guardar una lista como texto en DataStore daría las mismas consultas y relaciones?
2. `leida: Boolean = false` existe en Kotlin. ¿Por qué una base anterior todavía necesita un valor predeterminado al añadir la columna?
3. Dos lecturas comparten etiqueta. Describe qué filas representan el vínculo y qué evita repetir exactamente el mismo par.
4. Un autor sin lecturas debe aparecer en un resumen con total cero. ¿Qué problema introducirían `INNER JOIN` y `COUNT(*)`?
5. ¿Por qué los resultados de `@Relation` pueden necesitar una transacción aunque se esté leyendo, sin modificar ninguna fila?
6. Una API devuelve solo cinco elementos y faltan veinte del catálogo anterior. ¿Es suficiente para borrar esos veinte? ¿Qué contrato necesitarías para hacerlo?
7. Una actualización importa el nuevo título de una lectura, pero esta vuelve de leída a pendiente. ¿Qué regla faltaba y en qué responsabilidad se decide?
8. ¿Qué demuestra una prueba con una API que lanza `IOException` y qué comprobación de conectividad no demuestra?
9. La aplicación arranca después de borrar datos. ¿Por qué eso no acredita la migración? Diseña una prueba que sí la acredite.
10. ¿Qué diferencia hay entre `@Inject`, `@Provides` y `@Binds`? Pon un ejemplo del proyecto para cada uno.
11. La base lleva `@Singleton`. ¿Significa que sus objetos Kotlin se conservarán tras terminar el proceso? ¿Y que todos los teléfonos comparten esa instancia?
12. ¿Qué aporta `AlternarLectura` y cuándo sería razonable dejar esa coordinación en el ViewModel? Evita justificar una capa solo por su nombre.
13. Una casilla cambia inmediatamente pero al volver a abrir recupera el valor anterior. Distingue cambio visual, escritura y observación; indica dónde pondrías un breakpoint.
14. La consulta SQL devuelve cero filas afectadas al marcar. ¿Debe comunicarse éxito? Explica por qué el ID y el título no son equivalentes.

[Soluciones razonadas](soluciones-repaso.md).
