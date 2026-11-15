# Práctica guiada: comprobar una lista de notas

**JUnit · MockK · corrutinas de prueba · Compose Test.** Lista de revisión ya carga notas a través de un Repository y ViewModel. En esta práctica programarás pruebas que distingan límites, carga, error y recuperación. No necesitas volver a implementar la app ni contratar servicios.

[Proyecto inicial — ZIP](../descargas/proyectos/calidad-inicial.zip){ .md-button .md-button--primary }

???+ example "Preparar y reconocer lo que ya funciona"

    Abre el proyecto en Android Studio, sincroniza y ejecuta **app**. Se muestra carga y después dos notas; Volver a cargar repite la consulta local. El identificador Debug termina en `.debug` para diferenciarlo de una futura Release.

    **Observa el código existente.** `Notas.kt` contiene modelo, validación, contrato `NotaRepository`, `DemoRepository` y `RevisionViewModel`. El ViewModel recibe su colaborador por constructor y expone un estado de solo lectura. `RevisionPantalla` recibe estado y callback; no crea el repositorio.

    Lee [pruebas de lógica y ViewModel](pruebas.md) antes de abrir `test/RevisionTest.kt`. La prueba `tituloNormal` está resuelta. Las siguientes llevan `@Ignore`: se omiten deliberadamente mientras sus cuerpos están pendientes. Que el informe sea verde con casos omitidos no demuestra que esos requisitos se hayan comprobado.

    **Comprueba.** Ejecuta con Run la clase `RevisionTest` en el source set **test**. Debe ejecutarse el ejemplo normal y aparecer cuatro omitidas. Si necesitas Android para esa clase, revisa que elegiste **test**, no **androidTest**.

??? example "Límites: reproducir un patrón con entradas discriminantes"

    **Ejemplo resuelto.** `assertTrue(tituloValido("Tres letras"))` asegura un título normal. La función recorta espacios antes de contar y admite de 3 a 80 caracteres.

    **Ahora te toca.** Completa `limitesDeTitulo` y retira su `@Ignore`: incluye longitudes 2, 3, 80 y 81, además de una entrada solo con espacios y otra válida con espacios exteriores. No cambies la función para ajustarla a una expectativa incorrecta.

    ??? tip "Pista: construir el extremo"

        `"a".repeat(n)` prepara exactamente la longitud que quieras. Un caso demasiado corto y uno demasiado largo detectan errores diferentes en una condición de rango.

    **Comprueba.** Deben pasar el ejemplo normal y tus límites, mientras las demás pruebas siguen omitidas. Cambia temporalmente en tu copia el límite de 80 a 81: al menos un caso debe fallar. Restablece la función y comprueba que vuelve a pasar. Esto verifica que la prueba discrimina esa regresión concreta.

??? example "Repository y tiempo virtual"

    **Observa** que `DemoRepository.obtener` es suspendida y utiliza `delay(300)`. `MainDispatcherRule` ya instala y restaura Main para las pruebas del ViewModel; no debes copiar esa infraestructura en cada caso.

    **Ejemplo resuelto.** Dentro de una prueba, `runTest { val notas = DemoRepository().obtener() }` permite llamar a la operación suspendida. El planificador controla su demora sin mantener una espera real.

    **Ahora te toca.** Completa `repositorioDevuelveDatosTrasEspera` con `runTest`. Debe comprobar los IDs esperados y que el reloj virtual avanza 300 ms respecto a su valor inicial. Añade los imports desde Android Studio y activa solo esta prueba al terminarla. No uses `Thread.sleep` ni compares el reloj de pared del ordenador.

    ??? tip "Pista: dos relojes diferentes"

        El instante virtual está en `testScheduler.currentTime`. Guarda el inicial, ejecuta la operación y compara la diferencia. Esta comprobación corresponde a la demora simulada del laboratorio, no a una promesa de duración de una red real.

    **Comprueba.** El caso termina rápidamente y devuelve IDs 1 y 2. Si sustituyes temporalmente el `delay` por otro valor, la comprobación de duración debe detectarlo. Restablece después la producción.

??? example "ViewModel: un doble controlado y menos instrucciones"

    **Ejemplo resuelto.** Para una operación suspendida, `coEvery { repo.obtener() } returns lista` prepara la respuesta de un `mockk<NotaRepository>()`. El ViewModel recibe ese objeto por su constructor. No toca la base de datos ni la red.

    **Ahora te toca.** Completa `cargaExitoYUnaConsulta` usando MockK y el planificador de la regla: prepara una respuesta con demora suspendida y una nota propia, crea el ViewModel, observa carga antes de que termine y comprueba después datos, salida de carga y ausencia de error. Verifica que el repositorio se consultó una sola vez.

    ??? tip "Pista: observar antes del final"

        `runCurrent()` deja ejecutar el inicio de la corrutina sin saltar toda su demora. `advanceUntilIdle()` permite terminarla después. `coAnswers` admite un cuerpo suspendido para preparar el `delay`; `coVerify` comprueba la interacción.

    **Comprueba.** Si eliminas de tu copia la publicación de carga, la aserción intermedia debe fallar aunque la lista final sea correcta. Restablece después la producción.

    **Ahora te toca, con autonomía.** Completa `errorSeRecuperaConOtraCarga`: provoca el fallo conocido, comprueba el estado resultante, cambia la respuesta por una lista vacía y reintenta. La segunda carga debe ser un éxito vacío, sin conservar el error. Usa las mismas técnicas, sin reescribir el ViewModel ni su estado privado.

    **Comprueba.** Al terminar deben pasar las cinco pruebas JVM previstas en el inicial y no quedar omitidas en esa clase. La solución añade un caso de sustitución de carga lenta como referencia de regresión.

??? example "Compose: observar, implementar y comprobar el evento"

    Lee [pruebas de interfaz](interfaz.md). Abre `androidTest/RevisionUiTest.kt`: `estadoVacio` está preparado y entrega directamente `RevisionState()` a `RevisionPantalla`. Ejecuta esa clase con un emulador conectado; las otras dos pruebas están omitidas.

    **Ahora te toca.** Completa `listaYEventoDeRecarga`: entrega una nota propia y un callback que cuente llamadas, comprueba su título, pulsa Volver a cargar y verifica una única llamada. Completa después `errorOfreceReintento`, mostrando un estado de error y comprobando mensaje y botón visible. Retira cada `@Ignore` cuando tenga un cuerpo y aserciones.

    ??? tip "Pista: qué responsabilidad se comprueba"

        Aquí se verifica la pantalla y su evento; no hace falta crear otro mock del Repository. `runOnIdle` permite leer el contador tras el clic. El estado se prepara como entrada, sin manipular el ViewModel.

    **Comprueba.** Deben ejecutarse tres instrumentadas sin omisiones. Comprueba también la app por Run: el test de pantalla aislada y el arranque real son evidencias diferentes.

??? example "Revisar Release sin publicar"

    Lee [compilación](compilacion.md) y localiza la configuración Release ya preparada: R8 y reducción de recursos están activos; la firma privada no está configurada. En la ventana Gradle puedes ejecutar `assembleRelease` y `bundleRelease` para comprobar empaquetado y reducción.

    **Comprueba.** Localiza los artefactos y el mapeo en `app/build/outputs`. No los confundas con una app publicada ni con una instalación Release firmada probada. Registra el resultado real de pruebas JVM, omitidas, instrumentadas, lint y Release, con el dispositivo usado. La guía de [publicación](publicacion.md) explica el paso posterior cuando exista un encargo concreto de distribución.

[Solución formativa — ZIP](../descargas/proyectos/calidad-solucion.zip){ .md-button }

[Código y README de la solución](https://github.com/MJSPlayground/fp-dam2-pmdm-android/tree/main/proyectos/calidad/solucion). La solución mantiene la misma producción y aporta pruebas completas; no contiene configuraciones privadas de Firebase ni Play.
