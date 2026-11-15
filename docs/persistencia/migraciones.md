# Migrar sin perder datos

## El problema aparece en una instalación existente

Cambiar una clase Kotlin no modifica por sí solo una tabla ya guardada en un teléfono. La versión inicial de Lecturas tenía título y autor. La segunda añade el campo `leida`. Quien ya tiene un catálogo necesita conservarlo y recibir un estado inicial razonable para esa nueva columna.

Una instalación limpia crea directamente el esquema actual. Por eso abrir la aplicación después de borrar sus datos no demuestra una migración. Hay que partir de una base anterior con filas, ejecutar el cambio y comprobar tanto los datos como el nuevo esquema.

La [guía oficial de migraciones](https://developer.android.com/training/data-storage/room/migrating-db-versions) distingue migraciones automáticas y manuales. Las automáticas son útiles para cambios que Room puede deducir a partir de esquemas exportados. Renombrados, borrados o transformaciones ambiguas necesitan información o código explícito.

## Versión, valor por defecto y contrato

`LecturasDatabase` declara `version = 2` y `exportSchema = true`. Room exporta el esquema a `app/schemas`, configurado mediante KSP. Esos JSON documentan la estructura; no contienen los registros del usuario ni reemplazan la ejecución de una migración.

La propiedad Kotlin tiene `leida: Boolean = false`. Ese valor por defecto se aplica al construir un objeto Kotlin. Para que SQLite sepa qué poner en filas antiguas, la columna también declara `@ColumnInfo(defaultValue = "0")`. Son dos contratos diferentes que deben coincidir.

La base almacena el booleano como entero. En este caso cero significa pendiente y uno significa leída. Una columna nueva `NOT NULL` necesita un valor para las filas anteriores; añadirla sin predeterminado no resuelve qué hacer con ellas.

## Una migración manual pequeña

El objeto `MIGRACION_1_2` hereda de `Migration(1, 2)`. Su método recibe la base y ejecuta el cambio. Este ejemplo de técnica añade **otra columna ilustrativa** a una tabla distinta:

```kotlin title="Fragmento ilustrativo de una migración manual"
db.execSQL("ALTER TABLE preferencias ADD COLUMN compacto INTEGER NOT NULL DEFAULT 0")
```

En la práctica escribirás la sentencia equivalente para la tabla y propiedad reales de Lecturas. No elimines y recrees la tabla para evitar comprender el cambio: perderías los datos que la migración debe conservar.

El constructor de Room registra el objeto con `addMigrations(LecturasDatabase.MIGRACION_1_2)`. Declarar una migración que nunca se registra no crea un camino disponible para abrir una base vieja. Si hubiera una tercera versión, habría que probar los caminos desde todas las versiones que se sigan admitiendo.

## Por qué no usar una recreación destructiva

`fallbackToDestructiveMigration` puede borrar y recrear una base cuando no encuentra un camino. Solo tiene sentido si los datos son descartables y esa decisión está aceptada. No es una reparación genérica para un error al abrir el catálogo de una persona.

En este laboratorio no lo utilizamos. Un error de esquema o un TODO pendiente debe hacer fallar la prueba, con un diagnóstico, en vez de obtener un resultado verde después de eliminar las filas originales.

Las migraciones también tienen que preservar índices, claves foráneas y restricciones. Cambiar nombres con una tabla temporal puede exigir copiar columnas de forma explícita y reconstruir esos elementos. Una operación que funciona con una tabla vacía puede fallar con datos antiguos incompatibles.

## Prueba desde una base realmente anterior

`migracionUnoADosConservaFilasYAdmiteRelaciones` crea mediante SQLite el esquema de versión 1, inserta un autor y una lectura y fija su versión antes de cerrarlo. Después Room abre ese mismo archivo con la versión 2 y la migración registrada. Room valida el esquema final al abrirlo.

La prueba comprueba que siguen el título y la relación con el autor, que `leida` empieza en falso y que después puede actualizarse. Finalmente elimina solo su base temporal de prueba. No toca la base `lecturas.db` de la aplicación.

Este enfoque usa una definición explícita del esquema anterior para que el proyecto sea reproducible sin instalar una APK antigua. En productos mantenidos deben conservarse los esquemas históricos exportados y cubrir las migraciones con herramientas como `MigrationTestHelper`. No inventes un JSON histórico cambiando un número en el esquema actual.
