# Relaciones y consultas con Room

## Identidad, referencia y resultado de consulta

Una lectura pertenece a un autor. Un autor puede tener varias lecturas: relación **1:N**. Además, una lectura puede tener varias etiquetas y una etiqueta describir varias lecturas: relación **N:M**. Son reglas del catálogo, no dos formas de escribir la misma lista.

`data/Modelos.kt` separa entidades —filas almacenadas— de clases que reúnen resultados. `Autor`, `Lectura`, `Etiqueta` y `LecturaEtiqueta` son tablas. `AutorConLecturas` y `LecturaCompleta` son composiciones de consulta; no necesitan una tabla con ese nombre.

```text
Autor ───< Lectura ───< LecturaEtiqueta >─── Etiqueta
  1           N               N                 1
```

La clave de cada lectura es `lecturaId`. Dos títulos iguales pueden representar lecturas diferentes. El campo `autorId` referencia una clave de autor, y no su nombre, que podría cambiar.

## Relación 1:N y claves foráneas

La entidad `Lectura` declara una `ForeignKey` hacia `Autor`, con índice sobre `autorId`. La clave foránea impide guardar una lectura que apunte a un autor inexistente. El índice permite buscar las lecturas de ese autor sin recorrer necesariamente toda la tabla.

El laboratorio utiliza `RESTRICT` al borrar un autor: primero habría que decidir qué sucede con sus lecturas. La eliminación de vínculos de etiquetas utiliza `CASCADE`, porque un vínculo deja de tener sentido si desaparece alguno de sus extremos. Esas políticas no se eligen por comodidad; deben respetar los datos que queremos conservar.

Room no recorre relaciones de objetos mediante carga perezosa como algunos ORM de servidor. Declara un resultado y realiza las consultas necesarias. Este resultado agrupa un autor con sus lecturas:

```kotlin title="Resultado 1:N de Modelos.kt"
data class AutorConLecturas(
    @Embedded val autor: Autor,
    @Relation(parentColumn = "autorId", entityColumn = "autorId")
    val lecturas: List<Lectura>
)
```

`@Embedded` incorpora las columnas del autor al objeto principal. `@Relation` indica qué columna del resultado principal se compara con la columna de la entidad relacionada. El DAO consulta autores, no un SQL que invente una columna «lista de lecturas».

`porAutor(id)` lleva `@Transaction`, además de `@Query`, porque reunir los objetos puede requerir varias consultas que deben observar una visión consistente. La [guía de relaciones 1:N](https://developer.android.com/training/data-storage/room/relationships/one-to-many) desarrolla este patrón. Un autor sin lecturas devuelve una lista vacía; un ID de autor inexistente devuelve `null` en este contrato.

## Relación N:M con una tabla de unión

Guardar una lista de IDs separados por comas en una columna no es equivalente a una relación consultable. `LecturaEtiqueta` almacena un par por vínculo. Su clave primaria compuesta impide repetir el mismo par; un índice adicional sobre etiqueta facilita el recorrido inverso.

Para la demostración local, la lectura 100 tiene Android y Kotlin, y la 101 tiene Android. Son tres vínculos. No se copia el texto Android dentro de cada lectura: ambas apuntan a la misma etiqueta.

El resultado `LecturaCompleta` utiliza una unión para recuperar las etiquetas:

```kotlin title="Propiedad de resultado N:M — dentro de LecturaCompleta"
@Relation(
    parentColumn = "lecturaId",
    entityColumn = "etiquetaId",
    associateBy = Junction(LecturaEtiqueta::class)
)
val etiquetas: List<Etiqueta>
```

`Junction` identifica la tabla intermedia; sus columnas deben coincidir con las claves que enlaza. La relación no crea vínculos automáticamente: después de guardar ambas entidades se insertan los pares. El proyecto usa `IGNORE` únicamente para pares ya existentes, de modo que repetir la demostración no los duplica.

Consulta la [guía N:M](https://developer.android.com/training/data-storage/room/relationships/many-to-many). Las anotaciones anteriores son fragmentos de una clase de resultado; los archivos del proyecto incluyen imports de `androidx.room` y el constructor completo.

## Consultas agregadas sin perder el significado

Una lista de objetos relacionados responde qué lecturas tiene un autor. Para saber cuántas tiene y cuántas quedan pendientes, el DAO también ofrece `resumen()`. Su SQL combina `LEFT JOIN`, agrupación y agregados:

```sql title="Consulta de LecturasDao.resumen"
SELECT a.nombre, COUNT(l.lecturaId) AS total,
       SUM(CASE WHEN l.leida = 0 THEN 1 ELSE 0 END) AS pendientes
FROM autores a LEFT JOIN lecturas l ON a.autorId = l.autorId
GROUP BY a.autorId, a.nombre
ORDER BY a.nombre
```

El `LEFT JOIN` conserva autores sin lecturas. `COUNT(l.lecturaId)` cuenta claves reales; `COUNT(*)` contaría también la fila sin coincidencia producida por la unión. El `CASE` suma uno por lectura pendiente y cero en el resto. Los alias coinciden con `ResumenAutor`.

No agrupamos solo por nombre: dos autores pueden compartirlo y seguir teniendo identidades diferentes. La consulta devuelve `Flow<List<ResumenAutor>>`; Room vuelve a emitir cuando cambian las tablas observadas. Para una consulta que necesite menos datos, una proyección evita cargar todas las etiquetas y lecturas en memoria.

## Orden de escritura y atomicidad

Una importación guarda autores, lecturas, etiquetas y vínculos. Las claves foráneas obligan a crear los extremos antes de sus pares. `db.withTransaction` confirma el conjunto o lo revierte si una escritura falla. No metas una llamada de red dentro de la transacción: bloquearía su duración por una operación ajena a la base.

Las pruebas del proyecto comprueban ambas cardinalidades, las etiquetas compartidas y que el agregado pase de dos pendientes a una después de marcar una lectura. Contar únicamente el número total de filas no detectaría una etiqueta asignada a la lectura equivocada.
