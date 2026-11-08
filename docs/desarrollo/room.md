# Guardar datos con Room

## Persistencia frente a memoria

Una lista en el ViewModel puede sobrevivir a una rotación, pero desaparece al terminar el proceso. Una nota que el usuario ha confirmado debe existir cuando vuelva a abrir la app. Para conservar datos estructurados en el dispositivo utilizaremos **Room**, una biblioteca que trabaja sobre **SQLite**.

SQLite es el motor que almacena tablas y ejecuta SQL. Room añade declaraciones Kotlin, comprobación de consultas durante la compilación y generación de implementaciones. No sustituye la necesidad de comprender claves, consultas ni cambios de esquema.

El modelo mínimo tiene tres piezas: una **entidad** define una tabla; un **DAO** declara operaciones de acceso; una clase **RoomDatabase** reúne las entidades y permite obtener los DAO. El repositorio coordina su uso; un composable no ejecuta consultas por su cuenta. La [introducción oficial a Room](https://developer.android.com/training/data-storage/room) presenta esas piezas y su relación.

## Preparar el procesamiento de código

El proyecto añade `room-runtime`, las extensiones de Room necesarias y `room-compiler` mediante KSP. El script aplica el plugin `com.google.devtools.ksp` fijado para Kotlin. Es importante distinguir la biblioteca que se ejecuta en el dispositivo del procesador que genera código durante la construcción:

```kotlin title="app/build.gradle.kts — esquema de dependencias"
dependencies {
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
}
```

Los alias se corresponden con el catálogo del proyecto. La versión de Room debe coincidir entre sus artefactos. Los proyectos del curso fijan Kotlin, KSP y Room en una combinación común; no actualices KSP de forma aislada para eliminar un aviso.

En el mismo script del módulo se aplica el plugin y se indica la carpeta del esquema. Estas líneas se añaden a sus bloques correspondientes, conservando el resto de plugins Android y Kotlin:

```kotlin title="app/build.gradle.kts — fragmentos de configuración KSP"
plugins {
    alias(libs.plugins.ksp)
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}
```

`libs.plugins.ksp` apunta al plugin `com.google.devtools.ksp` del catálogo. `ksp(libs.room.compiler)` del bloque de dependencias elige el procesador Room que ejecutará ese plugin. Son referencias diferentes y hacen falta ambas; añadir solo `room-runtime` permite usar sus tipos, pero no genera los DAO.

Room puede exportar el esquema para conservar cómo era cada versión de la base. El proyecto configura la carpeta correspondiente mediante el argumento KSP `room.schemaLocation`. Esos archivos de esquema son documentación técnica de la base, no datos de las notas del alumno.

## Convertir Nota en entidad

Cada objeto `Nota` representará una fila. `@Entity` identifica la tabla y `@PrimaryKey` la clave primaria. Este archivo contiene el modelo completo de datos de la tabla sencilla:

```kotlin title="model/Nota.kt"
package es.mjsplayground.pmdm.desarrollo.cuaderno.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notas")
data class Nota(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val titulo: String,
    val contenido: String
)
```

Al insertar una nota nueva dejamos `id = 0` para que Room/SQLite asignen la clave autogenerada. Al editar, conservamos el identificador de la fila. Si creamos siempre otro objeto con cero al guardar cambios, estaremos pidiendo otra inserción en vez de identificar la nota existente.

Los títulos pueden coincidir: la identidad está en `id`, no en el texto. También por eso la navegación pasa un identificador y la lista usa ese valor como clave. La [guía de entidades](https://developer.android.com/training/data-storage/room/defining-data) explica nombres de tablas, columnas y claves.

## DAO: leer, guardar y eliminar

Un **DAO** es una interfaz o clase abstracta donde declaramos el acceso a datos. Room genera su implementación. No escribimos a mano la clase que abre un cursor para cada consulta.

Este contrato muestra las cuatro operaciones que necesita nuestro cuaderno: observar todas las notas, observar una, guardar y eliminar:

```kotlin title="Contrato de data/NotaDao.kt"
@Dao
interface NotaDao {
    @Query("SELECT * FROM notas ORDER BY id DESC")
    fun observar(): Flow<List<Nota>>

    @Query("SELECT * FROM notas WHERE id = :id")
    fun buscar(id: Long): Flow<Nota?>

    @Upsert
    suspend fun guardar(nota: Nota): Long

    @Delete
    suspend fun eliminar(nota: Nota)
}
```

El fragmento presupone imports de `androidx.room` para sus anotaciones, `kotlinx.coroutines.flow.Flow` y nuestro modelo `Nota`. Los archivos descargables incluyen todos los imports. `:id` es un parámetro enlazado: Room introduce el valor de forma apropiada. No construyas SQL concatenando el título que escribió una persona.

`observar` ordena por identificador descendente: en esta app sencilla las notas recién creadas aparecen primero. Una lista vacía es un resultado válido. `buscar` devuelve un tipo nullable porque puede no existir ninguna fila para ese identificador. La clave primaria garantiza que no habrá dos filas con el mismo `id`.

`@Upsert` inserta o actualiza según la existencia de la clave. Una nota nueva con clave autogenerada obtiene su identidad; una edición conserva la clave existente. Si necesitas distinguir estrictamente insertar de actualizar, Room también tiene `@Insert` y `@Update`, pero utilizaremos una única operación de guardado en esta práctica para no duplicar el recorrido.

El valor devuelto de una operación de upsert no debe interpretarse sin su contrato: una actualización no implica necesariamente obtener de nuevo el identificador como si fuera una inserción. Para editar ya conocemos `nota.id`. `@Delete` identifica la fila mediante su clave; borrar no requiere encontrar otra vez una coincidencia por título.

Las [operaciones DAO](https://developer.android.com/training/data-storage/room/accessing-data) explican la generación y los contratos de estas anotaciones.

## Una base de datos por aplicación

La clase de base de datos declara entidades y versión. Este fragmento muestra su estructura:

```kotlin title="Fragmento de data/NotaDatabase.kt"
@Database(entities = [Nota::class], version = 1, exportSchema = true)
abstract class NotaDatabase : RoomDatabase() {
    abstract fun notas(): NotaDao
}
```

Las anotaciones y `RoomDatabase` proceden de `androidx.room`. La versión identifica el esquema de la base, no la versión pública de la aplicación. Para construir una instancia se utiliza `Room.databaseBuilder`, un contexto de aplicación y un nombre de archivo, por ejemplo `cuaderno.db`.

La construcción se realiza en el punto donde se crean las dependencias y esa instancia se reutiliza. Abrir una base en cada recomposición crea trabajo innecesario y complica el ciclo de vida. No uses `allowMainThreadQueries()` como remedio para un error: las operaciones potencialmente costosas no deben bloquear la interfaz.

Cuando cambies columnas o relaciones, deberás definir cómo migrar datos de una versión a otra. No cambies el número al azar ni añadas una recreación destructiva si debes conservar notas. Las migraciones se estudian en la ampliación; durante la práctica mantén el esquema fijado y diferencia claramente tus datos de prueba de los que no puedes perder.

## Flow: el listado se actualiza desde la fuente

Los métodos `observar` y `buscar` no son funciones `suspend` que devuelvan una única fotografía. Devuelven un Flow que se recoge de forma asíncrona. Room vuelve a evaluar la consulta cuando se invalidan las tablas observadas y puede emitir el resultado actualizado.

El repositorio puede entregar esa observación al ViewModel:

```kotlin title="Fragmento del repositorio"
fun observar(): Flow<List<Nota>> = dao.observar()
```

El ViewModel recoge el flujo en su ámbito y actualiza el estado. Compose recoge ese estado con conciencia del ciclo de vida. Después de guardar, no hace falta que cada botón añada también una copia manual al listado: la base produce el dato actualizado. Mantener ambas modificaciones crearía dos fuentes de verdad.

Las escrituras son `suspend` porque esperamos su finalización sin bloquear el hilo principal. Room ofrece las operaciones asíncronas necesarias; la [guía de consultas asíncronas](https://developer.android.com/training/data-storage/room/async-queries) distingue consultas únicas y observables.

## Validar y transformar antes de guardar

El DAO conoce tablas y claves, pero no debe ser el único lugar donde se expresa el significado de una nota válida. El repositorio normaliza título y contenido y verifica las reglas de la app. La pantalla puede anticiparlas para ayudar al usuario, pero no sustituye esa validación.

Una importación remota es también una escritura. El DTO puede necesitar recorte o adaptación antes de convertirse en nota. No asumas que un servicio externo siempre respeta las longitudes del formulario local ni reutilices automáticamente su identificador como clave de Room.

En Cuaderno, `PublicacionDto.aNota()` quita espacios de los extremos, recorta deliberadamente el título a 80 caracteres y crea una nota nueva sin copiar la clave remota. El cuerpo también se recorta en sus extremos. Después se aplica la misma validación de escritura: una publicación con título de menos de tres caracteres o contenido vacío se rechaza. El truncado es una decisión visible del ejemplo, no una regla que pueda aplicarse silenciosamente a cualquier documento de un producto real.

Para importar una publicación en la práctica se crea una nueva nota local. Si se permite importarla varias veces, cada importación puede producir otra fila; si se quiere evitar duplicados remotos, habría que modelar explícitamente un identificador de origen y una regla de unicidad. No hay sincronización mágica por compartir un número.

## Comprobar CRUD y durabilidad

CRUD agrupa crear, consultar, actualizar y eliminar. La aplicación debe permitir recorrer las cuatro operaciones y observar consecuencias diferentes:

- Crear asigna identidad y añade una nota.
- Consultar la muestra sin duplicarla ni modificarla.
- Editar conserva identidad y cambia su contenido.
- Eliminar retira esa nota y deja las demás.

Reabrir una pantalla no demuestra durabilidad: el ViewModel podría seguir vivo. Cierra la aplicación y vuelve a abrirla; después prueba con un reinicio del proceso controlado. Una reinstalación o borrado de datos sí puede eliminar la base privada y es una operación distinta que no debes utilizar para comprobar conservación.

Durante depuración, **Database Inspector** permite observar una base de una app compatible en ejecución. Sirve para contrastar filas e identificadores con lo que presenta la pantalla; no reemplaza el recorrido del usuario ni las pruebas de errores. Si la consulta falla al compilar, revisa primero el nombre de tabla, propiedades y mensaje del procesador KSP antes de cambiar la UI.
