# Práctica guiada: Lecturas

Lecturas conserva un catálogo con autores y etiquetas, permite marcar lecturas y recordar un filtro. Room es la fuente de la pantalla; Retrofit actualiza la caché y DataStore conserva la preferencia. Hilt prepara los colaboradores. Observarás una relación y una importación ya estructuradas, completarás operaciones equivalentes y terminarás comprobando una migración.

Lee [DataStore](datastore.md), [relaciones](relaciones.md), [migraciones](migraciones.md), [offline](offline.md), [capas](arquitectura.md) e [Hilt](hilt.md). Puedes alternar cada página con su etapa correspondiente.

[Proyecto inicial — ZIP](../descargas/proyectos/persistencia-inicial.zip){ .md-button .md-button--primary }

???+ example "Preparación y recorrido del código existente"

    Abre en Android Studio la carpeta con `settings.gradle.kts`, espera la sincronización y ejecuta **app**. En una instalación nueva, la pantalla empieza vacía. Pulsa **Cargar demostración local**: aparecen dos lecturas, ambas de Biblioteca local y con etiqueta Android. La API no es necesaria para ese primer recorrido.

    **Observa el código existente.** Todos los archivos Kotlin están bajo `app/src/main/java/es/mjsplayground/pmdm/persistencia/lecturas`. Sigue este recorrido:

    ```text
    MainActivity → LecturasViewModel → DatosRepository → LecturasDao → Room
                                             ↑
                                        FuenteRemota
    ```

    Abre `LecturasApplication` y `di/Modulos.kt`. Localiza Application y Activity anotadas, la base con migración registrada, el cliente y el enlace de interfaz a implementación. Esa infraestructura está preparada. No necesitas reconstruir un contenedor ni modificar Gradle para cada TODO.

    | Archivo | Preparado | Completarás |
    | --- | --- | --- |
    | `data/Preferencias.kt` | Delegado, clave y lectura Flow | `preferencias-escritura` |
    | `data/Modelos.kt`, `LecturasDao.kt` | Entidades, consultas y ambas relaciones | Analizar contratos |
    | `data/DatosRepository.kt` | Observación, demo y mapeo remoto | `relacion-etiqueta`, `lectura-estado`, `cache-escritura` |
    | `data/LecturasDatabase.kt` | Versión y registro del cambio | `migracion-lectura` |
    | `domain/AlternarLectura.kt` | Regla de alternancia | Seguir sus argumentos |

    **Comprueba.** Repite la demostración: siguen dos lecturas, sin duplicarse. La casilla de filtro y el botón de marcar todavía no producen el resultado definitivo porque sus escrituras son TODO. Actualizar puede contactar con la API, pero aún no incorpora las lecturas a la caché. La app debe seguir abierta y los datos locales visibles.

??? example "Una preferencia: del ejemplo a otra escritura"

    **Observa el código existente.** En `Preferencias`, sigue el delegado compartido, `booleanPreferencesKey` y el `map` que utiliza falso si falta la clave. En el ViewModel, identifica cómo se combina ese flujo con Room.

    **Ejemplo resuelto de la técnica.** [DataStore](datastore.md#editar-como-una-transaccion) guarda una clave de vista compacta con `edit`. Esa clave solo ilustra la operación; no la añadas al proyecto.

    **Ahora te toca — `preferencias-escritura`.** Completa `cambiarSoloPendientes(valor)`: guarda el parámetro usando la clave `pendientes` ya declarada. No crees otro DataStore ni mantengas una copia permanente adicional del filtro en Compose.

    ??? tip "Pista — No estás guardando la colección"

        El bloque `edit` entrega preferencias mutables. La clave ya es booleana y el método ya es suspendido. El resultado debe llegar a la pantalla mediante el flujo preparado, no mediante otro setter de la UI.

    **Comprueba.** Activa el filtro y finaliza el proceso. Al volver, la casilla debe conservarse. Todavía se ven ambas lecturas porque ninguna está marcada como leída. Desactivar el filtro tampoco elimina registros.

??? example "Relaciones: leer una y completar otra"

    **Observa el código existente.** En `Modelos.kt`, identifica la clave foránea autor/lectura, `AutorConLecturas` y `LecturaCompleta`. Sigue `@Embedded`, `@Relation` y `Junction`. En el DAO, compara `porAutor` con `observar`: devuelven resultados de forma distinta, pero ambos reúnen relaciones con una transacción.

    **Ejemplo resuelto.** `demostracion` crea autores y etiquetas antes de los vínculos. La lectura 100 ya se asocia con Android mediante un par `LecturaEtiqueta`. Las claves impiden duplicar el mismo vínculo al repetir la demostración.

    **Ahora te toca — `relacion-etiqueta`.** Completa la demostración para que la lectura 100 también tenga Kotlin, sin quitar Android y sin añadir Kotlin a la 101. La etiqueta ya existe con ID 11; no cambies el texto de la etiqueta 10 para simular el resultado.

    ??? tip "Pista — Se añade un vínculo, no otra lectura"

        La tabla intermedia tiene un ID de lectura y otro de etiqueta. Sigue el par existente y decide qué extremo cambia. El orden de la lista no determina la relación: la determinan las claves.

    **Comprueba.** Vuelve a pulsar la demostración. La primera ficha debe mostrar Android y Kotlin; la segunda, solo Android. El catálogo sigue teniendo dos lecturas, incluso tras repetir la acción. El agregado `resumen()` debe contar dos pendientes del mismo autor.

??? example "Cambiar un estado local sin perder identidad"

    **Observa el código existente.** `AlternarLectura` recibe una ficha y solicita el estado opuesto por el mismo ID. El ViewModel no ejecuta SQL. La consulta `LecturasDao.marcar` ya devuelve cuántas filas se actualizaron.

    **Ahora te toca — `lectura-estado`.** Implementa `DatosRepository.marcar`. Actualiza la fila pedida y rechaza el caso en que el DAO no encuentra exactamente una. No insertes otra lectura para marcar la anterior.

    ??? tip "Pista — El resultado de UPDATE importa"

        Ya conoces `check` y condiciones Kotlin. El número que devuelve el DAO distingue una modificación de una acción sobre un ID inexistente. La excepción se traducirá al estado de error preparado.

    **Comprueba.** Marca la lectura 100. Con el filtro de pendientes activo, desaparece solo esa ficha. Desactívalo: reaparece como Leída. Reinicia el proceso: mantiene el estado. Repite para devolverla a Pendiente; no debe aumentar el número de lecturas.

??? example "Actualizar la caché y conservar cambios propios"

    **Observa el código existente.** `actualizar` consulta la red antes de llamar a `guardarRemotas`. Este último valida el lote, abre una transacción, prepara autores/etiquetas y calcula `actualizadas`. Identifica dónde se conserva la marca `leida` del mismo ID.

    **Ahora te toca — `cache-escritura`.** Termina la transacción: guarda las lecturas preparadas y después sus vínculos con la etiqueta API, de ID 1. Reutiliza las operaciones del DAO que acabas de analizar. No borres la tabla ni cambies el estado local de una lectura recibida de nuevo.

    ??? tip "Pista — Las claves foráneas fijan un orden"

        Los autores y la etiqueta se preparan antes del TODO. Un vínculo solo puede apuntar a una lectura ya existente. Puedes transformar la lista recibida en pares del mismo modo que transformaste datos en fichas.

    **Comprueba.** Con conexión, pulsa Actualizar: aparecen cinco publicaciones adaptadas a lecturas, además de la demostración local. Marca una remota, actualiza otra vez y comprueba que conserva su marca. Desactiva la red y reintenta: debe aparecer error conservando el catálogo y sus estados. Restablece la conexión y vuelve a actualizar. Registra si la petición HTTPS real pudo verificarse.

??? example "Migración: integrar y comprobar con datos anteriores"

    **Ahora te toca — `migracion-lectura`.** Completa `MIGRACION_1_2` con una sentencia que añada `leida` a `lecturas`, como entero no nulo y valor predeterminado cero. Aplica la técnica de [migración manual](migraciones.md#una-migracion-manual-pequena). No cambies la versión para saltarte la prueba ni borres registros.

    ??? tip "Pista — Un valor Kotlin no modifica SQLite"

        Compara la columna declarada en `Lectura` con la definición del esquema anterior de la prueba. El nombre, tipo, nulabilidad y predeterminado deben concordar con el esquema final que Room valida.

    **Comprueba con las pruebas.** En `app/src/androidTest/.../PersistenciaTest.kt`, retira el `@org.junit.Ignore` de la clase cuando hayas completado las etapas. Ejecuta desde Android Studio, con un emulador, sus cuatro pruebas de datos. La clase `LecturasUiTest` tiene otra prueba de pantalla: retira también su `@Ignore` para comprobar la integración; reinicia la base del laboratorio, así que conserva aparte cualquier dato que quieras mantener. La de migración crea su propia base antigua: no borres datos de la app para conseguir que pase. Debe conservar título y relación, añadir la marca como pendiente y permitir actualizarla.

    Las pruebas de relaciones verifican los vínculos y el agregado; las de caché simulan una API controlada y un fallo. No las confundas con una petición real de Internet. `AlternarLecturaTest`, en `src/test`, se ejecuta sin emulador y comprueba identidad y estado opuesto.

    **Comprobación final.** Demostración, etiquetas, filtro persistido, marcado, actualización, fallo de red y recuperación deben seguir funcionando juntos. Comprueba horizontal, oscuro y fuente grande: los botones y fichas deben seguir accesibles mediante desplazamiento. Registra qué ejecutaste y qué quedó pendiente.

## Solución formativa

[Solución completa — ZIP](../descargas/proyectos/persistencia-solucion.zip){ .md-button }

Contiene las mismas clases y los cinco TODO resueltos, con las cinco pruebas instrumentadas activas. La interfaz y el grafo Hilt son equivalentes: puedes comparar una operación concreta sin reemplazar tu proyecto completo. Continúa con la [consolidación autónoma](consolidacion.md).
