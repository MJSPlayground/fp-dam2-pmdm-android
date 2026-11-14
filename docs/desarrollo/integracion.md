# Integrar estado, repositorio y HTTP

Esta etapa de la [práctica de Cuaderno](proyecto.md) comienza tras el punto de control de navegación. Lee antes [ViewModel y repositorio](arquitectura.md) y [HTTP](http.md). Vamos a comprobar esas responsabilidades con **datos locales en memoria**, antes de introducir Room. La consulta de Internet y el guardado de una nota son acciones diferentes.

[Punto de control Remotos — ZIP](../descargas/proyectos/desarrollo-remotos.zip){ .md-button }

Conserva tu proyecto anterior: ese ZIP es una **referencia resuelta**, no un nuevo proyecto inicial. Continúa sobre tu Cuaderno. La interfaz, factorías, tipos de estado y clasificación de errores se facilitan como infraestructura de esta etapa; las operaciones que se indican como «Ahora te toca» las implementarás tú. Así puedes concentrarte en el recorrido evento → ViewModel → repositorio → estado después de haber construido el formulario y la navegación.

Los ejemplos completos de infraestructura se extraen de los archivos de referencia. Los fragmentos marcados como referencia explican una operación concreta y no sustituyen una clase completa.

???+ example "Preparación y contrato remoto"

    ## Preparar dependencias y permisos

    Mantén la configuración de navegación tipada del punto anterior. En el catálogo incorpora las entradas de Lifecycle, corrutinas, Retrofit y Serialization de la configuración común. En el módulo aplica los plugins de Android, Kotlin, Compose y Serialization. **Este punto no utiliza Room ni KSP.**

    La configuración completa de la etapa es:

    ```kotlin title="app/build.gradle.kts — checkpoint remotos"
    --8<-- "proyectos/desarrollo/remotos/app/build.gradle.kts"
    ```

    No copies solamente una llamada `implementation` sin su entrada correspondiente del catálogo. Puedes comparar el `gradle/libs.versions.toml` incluido en el ZIP. Sincroniza y corrige un posible error de alias antes de seguir con la interfaz.

    El manifiesto declara `INTERNET` para consultar el servicio y `ACCESS_NETWORK_STATE` para comprobar el transporte de la red activa. Ninguno necesita diálogo en tiempo de ejecución. La excepción HTTP local solo existe en `src/debug/AndroidManifest.xml`; la configuración principal mantiene HTTPS.

    ## Declarar el contrato remoto

    **Ejemplo resuelto.** Crea `data/NotasApi.kt` con el DTO y el contrato GET de [HTTP](http.md#declarar-el-cliente-con-retrofit). `PublicacionDto` contiene los campos del JSON y `NotasApi` declara una petición suspendida. Una consulta correcta todavía no escribe en la colección local.

    ```kotlin title="Fragmento del contrato HTTP — imports explicados en la teoría"
    @GET
    suspend fun publicaciones(@Url url: String): List<PublicacionDto>
    ```

    Este método pertenece a la interfaz `NotasApi`, no se coloca suelto en el archivo. Los imports `GET` y `Url` proceden de `retrofit2.http`; el DTO utiliza `kotlinx.serialization.Serializable`, como muestra la teoría.

    **Ahora te toca.** En el mismo archivo escribe la extensión `PublicacionDto.aNota(): Nota`. Debe crear una nota nueva con título y contenido recortados en los extremos, limitar el título a 80 caracteres y conservar el ID local inicial cero. Ya has usado extensiones, `data class`, `trim` y valores por defecto. La validación de escritura se mantendrá en el repositorio, no en este DTO.

    ??? tip "Pista — Dos identidades diferentes"

        El identificador de una publicación distingue filas remotas. No es la clave de nuestra nota. El constructor de `Nota` tiene un valor por defecto para una nota nueva; `take` limita cuántos caracteres conserva una cadena.

    **Comprueba.** Añade una prueba local que convierta una publicación con espacios y título de más de 80 caracteres: el resultado debe tener ID cero, título de longitud 80 y contenido sin espacios exteriores. Un título vacío seguirá siendo inválido para guardar: convertir no equivale a validar una escritura.

??? example "Consulta resuelta e importación propia"

    ## Construir el repositorio en memoria

    El repositorio recibe un cliente HTTP y conserva la colección local en un `MutableStateFlow`. Guardar actualiza esa colección; observarla ofrece sus cambios sin permitir que la pantalla reemplace sus datos directamente. En esta etapa el repositorio vive durante el proceso de la app, **no después de cerrarlo**.

    **Observa el código de referencia.** En `data/NotaRepository.kt` del punto de control, sigue primero `guardar`: valida, normaliza y añade una nota nueva o sustituye la de igual ID. Es el mismo comportamiento que ya programaste en memoria; ahora la colección pertenece a un repositorio con `MutableStateFlow`. Reproduce ese traslado conservando tu validación, y prepara `observar` y `buscar` según [Flow y StateFlow](arquitectura.md#flow-y-stateflow). El constructor recibe `NotasApi`.

    **Infraestructura preparada.** Puedes incorporar sin reescribir la función `validarUrl` del archivo de referencia, sus imports y las dos excepciones declaradas antes del repositorio: `UrlNoValidaException` y `NotaNoValidaException`. La validación de escritura debe lanzar esta última para que el clasificador preparado la identifique. La política de URL restringe el laboratorio HTTP a direcciones privadas y al build de depuración; no sustituye a TLS para servicios externos. Copia esas declaraciones de apoyo, no el repositorio completo.

    **Ejemplo resuelto: consultar.** Este método pertenece a `NotaRepository` y delega la petición sin modificar la colección local:

    ```kotlin title="Fragmento de NotaRepository — consulta remota"
    suspend fun consultarRemotas(url: String): List<PublicacionDto> =
        api.publicaciones(validarUrl(url, BuildConfig.DEBUG))
    ```

    `api` es el cliente recibido por el constructor; `BuildConfig` se importa del paquete de la app. Una respuesta vacía sigue siendo éxito. Las excepciones subirán al ViewModel, que decidirá el estado visible.

    **Ahora te toca.** Implementa `suspend fun importar(publicacion: PublicacionDto): Long` reutilizando tu conversión y el guardado del repositorio. No repitas la validación ni añadas directamente otra colección. Implementa también `suspend fun eliminar(nota: Nota)`: debe retirar solo el ID elegido y emitir la nueva lista.

    ??? tip "Pista — Reutilizar una operación"

        Importar combina dos operaciones que ya existen: transformar y guardar. El identificador que devuelve procede del guardado. Para eliminar en memoria recuerda qué operación de colecciones conserva los elementos que no cumplen una condición; usa la actualización del `MutableStateFlow`, como al guardar.

    **Comprueba.** Crea dos notas, elimina una y comprueba que la otra permanece. Al importar dos veces una publicación deben aparecer dos copias locales con identidades distintas; editar una no debe modificar la otra. Una publicación con contenido vacío debe rechazarse por la misma validación de escritura que una nota manual.

??? example "Infraestructura y conexión con la interfaz"

    ## Crear las dependencias una vez

    El contenedor construye el cliente y el repositorio. No lo crees dentro de una pantalla que puede recomponerse. Retrofit utiliza una URL base válida, el conversor de JSON y un cliente con tiempos máximos de conexión y lectura.

    ```kotlin title="data/Contenedor.kt — sin base de datos todavía"
    --8<-- "proyectos/desarrollo/remotos/app/src/main/java/es/mjsplayground/pmdm/desarrollo/cuaderno/data/Contenedor.kt"
    ```

    La clase Application ofrece un propietario con la duración del proceso. `by lazy` construye el contenedor la primera vez que se solicita y conserva esa instancia. En esta etapa el contenedor no necesita contexto: solo crea objetos Kotlin y el cliente de red. Al añadir Room recibirá contexto de aplicación, nunca una Activity que pueda recrearse.

    ```kotlin title="CuadernoApplication.kt"
    --8<-- "proyectos/desarrollo/remotos/app/src/main/java/es/mjsplayground/pmdm/desarrollo/cuaderno/CuadernoApplication.kt"
    ```

    En el `<application>` del manifiesto añade `android:name=".CuadernoApplication"`. Ese nombre indica a Android qué clase debe instanciar. Sin esa declaración, convertir `application` a `CuadernoApplication` desde la Activity provocaría un error de tipo al arrancar.

    ## Conectar la actividad y la interfaz

    La Activity obtiene el repositorio y se lo entrega a la composición raíz. Ya no mantiene su propia colección de notas:

    ```kotlin title="MainActivity.kt"
    --8<-- "proyectos/desarrollo/remotos/app/src/main/java/es/mjsplayground/pmdm/desarrollo/cuaderno/MainActivity.kt"
    ```

    La interfaz del punto de control se ha trasladado a `ui/CuadernoApp.kt`: conserva lista, editor y navegación; ahora recibe un repositorio y obtiene el ViewModel mediante una factoría. Utiliza ese archivo de interfaz preparado y compara sus eventos con los de tu pantalla anterior. Crear una clase ViewModel con un constructor no basta para conectarla al propietario y al ciclo de vida de Compose.

    ```kotlin title="ui/CuadernoApp.kt — factoría principal y observación"
    --8<-- "proyectos/desarrollo/remotos/app/src/main/java/es/mjsplayground/pmdm/desarrollo/cuaderno/ui/CuadernoApp.kt:vm-principal"
    ```

    Estos símbolos necesitan `androidx.lifecycle.viewmodel.compose.viewModel`, `androidx.lifecycle.viewmodel.viewModelFactory`, `androidx.lifecycle.viewmodel.initializer`, `androidx.lifecycle.compose.collectAsStateWithLifecycle` y el import `androidx.compose.runtime.getValue`. `initializer` describe la construcción de la primera instancia; `viewModel` la asocia al propietario y permite reutilizarla después de una recomposición.

    El destino de detalle tiene otra instancia y recibe los argumentos guardados de su entrada de navegación. `createSavedStateHandle` permite recuperar el ID de la ruta sin pasar un NavController al ViewModel:

    ```kotlin title="ui/CuadernoApp.kt — dentro del destino Detalle"
    --8<-- "proyectos/desarrollo/remotos/app/src/main/java/es/mjsplayground/pmdm/desarrollo/cuaderno/ui/CuadernoApp.kt:vm-detalle"
    ```

    Aquí interviene además `androidx.lifecycle.createSavedStateHandle`. El ViewModel de detalle observa `repository.buscar(id)` y distingue cargando, nota ausente, error y nota encontrada. No obtiene el contenido desde una copia serializada en la ruta.

??? example "Completar acciones y comprobar la integración"

    ## Comprender las acciones del ViewModel

    **Observa el código existente.** Incorpora los tipos de estado, factorías y gestión de errores del punto de control. En `ui/CuadernoViewModel.kt`, lee `CuadernoUiState`: separa datos y carga local de la consulta remota. Usa `guardar` y `cargarRemotas` como ejemplos resueltos; conserva la observación del repositorio. Deja `importar` para tu implementación, con la firma que necesita la interfaz.

    - `guardar` bloquea envíos duplicados, llama al repositorio y solo comunica éxito después de terminar. El callback de éxito permite que la UI vuelva a la lista; el ViewModel no conserva el controlador de navegación.
    - `cargarRemotas` publica carga, ejecuta una petición y distingue error de lista vacía. Relanza `CancellationException`, porque cancelar una operación no es una respuesta de error del servidor.
    - La marca `importada` mostrará la confirmación de tu importación; no implica sincronización con el servidor.

    Los errores tienen significado (`Fallo.Conexion`, `Fallo.Http`, `Fallo.Formato`, `Fallo.Url`, `Fallo.Validacion` y `Fallo.Datos`). La UI los transforma en recursos de texto, en vez de mostrar una excepción técnica al usuario. Los archivos completos de estado y pantallas están en el mismo ZIP, con sus imports; no necesitas deducir una implementación de DAO todavía.

    **Ahora te toca.** Completa `importar(publicacion: PublicacionDto)` del ViewModel siguiendo el patrón de una acción suspendida de `guardar`: evita duplicados mientras está ocupada, limpia el error anterior, llama al repositorio dentro de `viewModelScope` y comunica la confirmación solo cuando termine. Ante un fallo conserva la lista remota y publica el error. Relanza la cancelación y libera la marca de operación en todos los caminos.

    ??? tip "Pista — Comenzar y terminar son cambios distintos"

        Localiza en `guardar` el bloqueo de acciones duplicadas y cómo retira la marca de guardado tanto al terminar como al fallar. La importación no tiene que navegar: su éxito se expresa mediante `importada`. Reutiliza el clasificador de errores preparado; no muestres `Exception.message` directamente.

    **Comprueba.** Carga publicaciones e importa una: debe aparecer confirmación y una sola nota local. Intenta importar mientras la operación sigue ocupada: no debe duplicarse ese envío. Después provoca un fallo de validación con un dato de prueba; la colección remota debe conservarse y otra importación válida debe poder terminar.

    ## Comprobar antes de añadir Room

    Ejecuta la etapa y realiza esta secuencia:

    1. Crea dos notas válidas. Abre una, modifica su contenido y guarda. La lista debe reflejar el cambio.
    2. Gira el dispositivo. El repositorio del proceso y el estado de pantalla deben seguir conectados; un borrador simple usa la política de restauración del formulario.
    3. Entra en Remotos y carga el recurso inicial HTTPS. Observa carga y resultado; importa una publicación y vuelve a la lista.
    4. Repite con el servidor local y su `/error`. Comprueba que el error conserva el código y permite reintentar.
    5. Finaliza el proceso y vuelve a abrir. Las notas locales de esta etapa se pierden: se guardaban en memoria.

    Si falla al arrancar, comprueba primero `android:name` y el package de la Application. Si la pantalla mantiene una lista distinta, elimina el estado duplicado que pertenecía a la antigua Activity. Si una clase no se reconoce, revisa su import y la dependencia que la proporciona.

    El siguiente cambio del [proyecto guiado](proyecto.md#sustituir-memoria-por-room) sustituye esta fuente local por Room. Las pantallas y los eventos mantienen el mismo propósito; lo que cambia es dónde se conservan las notas.

