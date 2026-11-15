# Práctica guiada: Avisos y reproducción

**NotificationCompat · WorkManager · Media3.** La aplicación muestra un aviso, programa una revisión local y reproduce un tono en segundo plano. La base prepara canal, permisos, pantalla y sesión multimedia; completarás un estilo equivalente, una solicitud diferible y un receiver. El adaptador remoto se estudia aparte y no necesita una cuenta para el recorrido local.

[Proyecto inicial — ZIP](../descargas/proyectos/segundo-plano-inicial.zip){ .md-button .md-button--primary }

???+ example "Preparación y código existente"

    Abre la carpeta con `settings.gradle.kts` en Android Studio, sincroniza y ejecuta **app**. Conserva las versiones de [Recursos](../recursos/index.md). En API 33 o posterior pulsa Comprobar permiso y elige si permites avisos. Una denegación no debe cerrar la aplicación.

    **Observa el código existente.** `Avisos.kt` crea canal y aviso básico; `Trabajos.kt` contiene Worker y los TODO de solicitud/receiver; `ReproductorService.kt` posee audio y sesión; `MainActivity.kt` conecta controles. `MensajesService.kt` y `ActivacionFCM.kt` solo se utilizan en una integración remota configurada por separado.

    **Comprueba.** Aviso inmediato debe aparecer con permiso y canal activos. Pulsa el aviso y vuelve a la app. Reproducir, Pausar y Detener están preparados; el tono solo empieza al pulsarlo. Programar revisión está deshabilitado hasta completar su solicitud. El botón de evento no encola nada mientras su receiver tenga el TODO.

??? example "Aviso expandible: de un ejemplo resuelto a otro equivalente"

    Lee [canales y estilos](avisos.md). **Observa** el builder básico: tiene icono pequeño, título, texto, ID estable y `PendingIntent` inmutable. No lo reescribas ni solicites permisos desde el Worker.

    **Ejemplo resuelto.** Para presentar varias líneas con un builder ya preparado se puede usar un estilo:

    ```kotlin
    aviso.setStyle(NotificationCompat.InboxStyle()
        .addLine("Comprobar permisos")
        .addLine("Revisar resultado"))
    ```

    El ejemplo muestra la relación builder/estilo; los textos reales del proyecto se obtienen de recursos.

    **Ahora te toca.** Completa `aviso-estilo` en `Avisos.kt`: cuando `resumen` sea verdadero, utiliza `BigTextStyle` y el recurso `R.string.resumen`. Cuando sea falso, conserva el aviso básico. Ambos deben abrir la app y reutilizar el mismo ID.

    ??? tip "Pista: un estilo distinto, el mismo builder"

        No necesitas otro canal ni otra llamada a `notify`. El método `bigText` recibe el texto completo; `setStyle` incorpora ese objeto al builder existente.

    **Comprueba.** Aviso inmediato presenta su resumen breve. Aviso expandido permite leer el texto largo en la bandeja; el anterior se actualiza en lugar de duplicarse. Desactiva el canal y repite: la app debe mantener un estado comprensible, sin excepción. Restablece el canal si vas a continuar.

??? example "WorkManager: configurar y comprobar una solicitud"

    Lee [trabajos](trabajos.md). **Observa** `RevisionWorker`: su primer intento puede devolver `retry`; cuando termina llama al aviso expandido. El código no hace una conexión HTTP: se simula el trabajo para estudiar sus estados.

    **Ejemplo resuelto.** `programar` usa `enqueueUniqueWork("revision", REPLACE, solicitud)`. Otra pulsación sustituye la revisión pendiente; Cancelar utiliza ese mismo nombre. La UI observa los estados publicados por WorkManager.

    **Ahora te toca.** Completa `crearSolicitud`: demora mínima de 10 segundos, condición de red conectada, entrada `fallos=1` y backoff exponencial con base de 10 segundos. Los imports y el builder están preparados. Después habilita el botón del TODO `trabajo-ui` usando el permiso, como las otras acciones que lo requieren.

    ??? tip "Pista: cada parte tiene un contrato"

        La demora pertenece a `setInitialDelay`, las condiciones a `Constraints` y los datos a `workDataOf`. La política de reintento no es otra demora manual dentro del Worker. Consulta las llamadas explicadas en la teoría; no crees un bucle de espera.

    **Comprueba.** Programa y observa ENQUEUED. La primera ejecución simula un fallo temporal y deja un reintento; la siguiente finaliza y muestra el aviso. No exijas que ocurra en un segundo exacto. Cancela otra solicitud pendiente: debe llegar a CANCELLED. Repite sin conexión y comprueba que la restricción impide empezar hasta recuperar red.

    Si no hay permiso o canal activo, el Worker termina con fallo porque repetir no puede concederlos. Restablece el permiso y programa una nueva solicitud; no confundas un intento terminado con uno aún pendiente.

??? example "Receiver: aplicar lo aprendido con menos ayuda"

    Lee [eventos](eventos.md). **Observa** el Intent explícito del botón Enviar evento propio y el receiver no exportado del manifiesto. No representa un cambio del sistema.

    **Ahora te toca.** Completa `receiver`: acepta únicamente `ACCION` y delega la misma revisión mediante contexto de aplicación. El callback no debe esperar, reproducir ni descargar nada. No cambies su exportación para facilitar la prueba.

    ??? tip "Pista opcional"

        El método de programación ya existe. El nuevo trabajo de esta etapa es validar la acción y escoger el contexto cuya vida no depende del callback.

    **Comprueba.** Con permiso y red, envía el evento y observa la solicitud. Repítelo: la política de nombre único evita dos cadenas independientes. En una copia de prueba, cambia la acción por otra y confirma que no programa. Restablece la constante después.

??? example "Sesión multimedia resuelta y comprobación de integración"

    Lee [reproducción en segundo plano](reproduccion.md). **Observa** quién crea y libera ExoPlayer, quién posee la sesión y qué libera `DisposableEffect` en la pantalla. La Activity no contiene otro reproductor.

    **Comprueba.** Inicia el tono y sal a otra app. La sesión debe mantenerse y ofrecer controles del sistema. Pausa desde esos controles, vuelve a la app, reanuda y detén. No dejes el bucle sonando al terminar. Repite con pantalla bloqueada cuando el entorno lo permita y registra por separado auriculares/foco en hardware real.

    La solución incluye una prueba del Worker con intentos controlados, otra que envía el evento propio y espera una planificación real con reintento/cancelación, y otra que detiene la Activity mientras comprueba la sesión. La prueba del planificador necesita red y espera condiciones reales del emulador; no acredita todos los estados de ahorro energético, audición, auriculares ni entrega FCM.

??? example "Mensajería remota y cierre"

    **Observa**, sin activar el servicio, `MensajesService`: la renovación conserva un token pendiente y el mensaje de datos `tipo=revision` delega trabajo. No aparecen claves de servidor ni llamadas de administración desde Android.

    Sigue [mensajería](mensajeria.md) si tienes un proyecto propio que debas integrar. No sustituyas la configuración real por valores inventados para eliminar un error. Mientras no se haya realizado el envío y recepción reales, registra FCM como pendiente.

    | Caso local | Resultado observable |
    | --- | --- |
    | Permiso denegado/canal desactivado | Sin cierre inesperado ni falsa confirmación de aviso |
    | Aviso básico y expandido | Contenido apropiado y apertura de la app |
    | Solicitud, reintento y cancelación | Estados de WorkManager coherentes |
    | Evento propio | Misma revisión única, sin trabajo lento en el callback |
    | Salir durante audio | Sesión activa y controles disponibles |
    | Detener | Finaliza el sonido |

[Solución formativa — ZIP](../descargas/proyectos/segundo-plano-solucion.zip){ .md-button }

[Código y README](https://github.com/MJSPlayground/fp-dam2-pmdm-android/tree/main/proyectos/segundo-plano/solucion). El tono es un recurso original de estos materiales, con su procedencia indicada en el proyecto.
