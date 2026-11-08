# Integrar estado, repositorio y HTTP

Esta etapa de la [práctica de Cuaderno](proyecto.md) comienza tras el punto de control de navegación. Lee antes [ViewModel y repositorio](arquitectura.md) y [HTTP](http.md). Vamos a comprobar esas responsabilidades con **datos locales en memoria**, antes de introducir Room. La consulta de Internet y el guardado de una nota son acciones diferentes.

[Punto de control Remotos — ZIP](../descargas/proyectos/desarrollo-remotos.zip){ .md-button }

Conserva tu proyecto anterior. Este ZIP contiene la solución de esta etapa y una interfaz adaptada a recibir estado y eventos. Puedes comparar los archivos mientras integras los cambios en tu copia. Todos los archivos de código que se muestran aquí se extraen de esa referencia, para que sus imports y contratos coincidan con el proyecto.

## Preparar dependencias y permisos

Mantén la configuración de navegación tipada del punto anterior. En el catálogo incorpora las entradas de Lifecycle, corrutinas, Retrofit y Serialization de la configuración común. En el módulo aplica los plugins de Android, Kotlin, Compose y Serialization. **Este punto no utiliza Room ni KSP.**

La configuración completa de la etapa es:

```kotlin title="app/build.gradle.kts — checkpoint remotos"
--8<-- "proyectos/desarrollo/remotos/app/build.gradle.kts"
```

No copies solamente una llamada `implementation` sin su entrada correspondiente del catálogo. Puedes comparar el `gradle/libs.versions.toml` incluido en el ZIP. Sincroniza y corrige un posible error de alias antes de seguir con la interfaz.

El manifiesto declara `INTERNET` para consultar el servicio y `ACCESS_NETWORK_STATE` para comprobar el transporte de la red activa. Ninguno necesita diálogo en tiempo de ejecución. La excepción HTTP local solo existe en `src/debug/AndroidManifest.xml`; la configuración principal mantiene HTTPS.

## Declarar el contrato remoto

Crea `data/NotasApi.kt`. `PublicacionDto` contiene los campos que necesitamos del JSON; `NotasApi` declara una petición suspendida; `aNota` transforma una publicación en una nota local. Una consulta correcta todavía no escribe en la colección local.

```kotlin title="data/NotasApi.kt"
--8<-- "proyectos/desarrollo/remotos/app/src/main/java/es/mjsplayground/pmdm/desarrollo/cuaderno/data/NotasApi.kt"
```

El identificador de la publicación sirve para distinguir filas remotas. Al importar asignaremos otro identificador local. El límite de 80 caracteres normaliza el título al contrato de la nota; una publicación con contenido inválido no debe convertirse silenciosamente en un registro aceptado.

## Construir el repositorio en memoria

El repositorio recibe un cliente HTTP y conserva la colección local en un `MutableStateFlow`. Guardar actualiza esa colección; observarla ofrece sus cambios sin permitir que la pantalla reemplace sus datos directamente. En esta etapa el repositorio vive durante el proceso de la app, **no después de cerrarlo**.

```kotlin title="data/NotaRepository.kt — versión en memoria"
--8<-- "proyectos/desarrollo/remotos/app/src/main/java/es/mjsplayground/pmdm/desarrollo/cuaderno/data/NotaRepository.kt"
```

Sigue tres recorridos en este archivo: guardar una nota nueva, modificar una existente por ID e importar una publicación. En cada uno, comprueba dónde se valida, dónde se normalizan los textos y qué emite el flujo. `validarUrl` restringe el laboratorio HTTP a direcciones privadas y al build de depuración; no sustituye a TLS para servicios externos.

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

## Comprender las acciones del ViewModel

Abre `ui/CuadernoViewModel.kt` en el punto de control. Primero lee `CuadernoUiState`: separa datos y carga local de la consulta remota. Después localiza `guardar`, `cargarRemotas` e `importar`.

- `guardar` bloquea envíos duplicados, llama al repositorio y solo comunica éxito después de terminar. El callback de éxito permite que la UI vuelva a la lista; el ViewModel no conserva el controlador de navegación.
- `cargarRemotas` publica carga, ejecuta una petición y distingue error de lista vacía. Relanza `CancellationException`, porque cancelar una operación no es una respuesta de error del servidor.
- `importar` transforma y guarda una nota local. La marca `importada` muestra la confirmación; no implica que exista sincronización con el servidor.

Los errores tienen significado (`Fallo.Conexion`, `Fallo.Http`, `Fallo.Formato`, `Fallo.Url`, `Fallo.Validacion` y `Fallo.Datos`). La UI los transforma en recursos de texto, en vez de mostrar una excepción técnica al usuario. Los archivos completos de estado y pantallas están en el mismo ZIP, con sus imports; no necesitas deducir una implementación de DAO todavía.

## Comprobar antes de añadir Room

Ejecuta la etapa y realiza esta secuencia:

1. Crea dos notas válidas. Abre una, modifica su contenido y guarda. La lista debe reflejar el cambio.
2. Gira el dispositivo. El repositorio del proceso y el estado de pantalla deben seguir conectados; un borrador simple usa la política de restauración del formulario.
3. Entra en Remotos y carga el recurso inicial HTTPS. Observa carga y resultado; importa una publicación y vuelve a la lista.
4. Repite con el servidor local y su `/error`. Comprueba que el error conserva el código y permite reintentar.
5. Finaliza el proceso y vuelve a abrir. Las notas locales de esta etapa se pierden: se guardaban en memoria.

Si falla al arrancar, comprueba primero `android:name` y el package de la Application. Si la pantalla mantiene una lista distinta, elimina el estado duplicado que pertenecía a la antigua Activity. Si una clase no se reconoce, revisa su import y la dependencia que la proporciona.

El siguiente cambio del [proyecto guiado](proyecto.md#sustituir-memoria-por-room) sustituye esta fuente local por Room. Las pantallas y los eventos mantienen el mismo propósito; lo que cambia es dónde se conservan las notas.
