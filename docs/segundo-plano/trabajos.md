# WorkManager: restricciones y reintentos

## Elegir trabajo diferible

Una carga ligada a una pantalla normalmente pertenece a su ViewModel. Un trabajo que debe quedar registrado y ejecutarse cuando se cumplan condiciones puede pertenecer a WorkManager. No es una alarma exacta ni un mecanismo para mantener un bucle permanente.

El proyecto fija **WorkManager 2.10.5**, cuya [publicación oficial](https://developer.android.com/jetpack/androidx/releases/work#2.10.5) es compatible con la base de SDK del curso. La [guía inicial](https://developer.android.com/develop/background-work/background-tasks/persistent/getting-started) distingue solicitud, Worker y planificación.

Un `CoroutineWorker` recibe contexto de aplicación y parámetros; `doWork` ejecuta la operación suspendida. Devuelve éxito, fallo definitivo o petición de reintento. Su contexto no es una Activity y no debe conservar una referencia a una pantalla que puede haber desaparecido.

## Solicitud y condiciones

`OneTimeWorkRequestBuilder<RevisionWorker>()` construye una solicitud puntual. Las `Constraints` expresan condiciones como conectividad, batería suficiente o carga del dispositivo. El sistema puede detener trabajo si deja de cumplirse una condición; la operación debe tolerar cancelación y reejecución.

Fragmento del laboratorio, dentro de la construcción de la solicitud:

```kotlin
.setInitialDelay(10, TimeUnit.SECONDS)
.setConstraints(
    Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()
)
```

Diez segundos es una demora **mínima**, no una cita exacta. Sin red, el trabajo sigue esperando; con red, el sistema decide cuándo puede ejecutarlo. La práctica simula una sincronización sin enviar datos: la condición sirve para estudiar la planificación, no para certificar una conexión con otro equipo.

Las entradas se entregan como `Data`, apropiadas para valores pequeños. No introduzcas bitmaps, bases completas ni secretos dentro de esos parámetros; para archivos, conserva una referencia válida y considera su duración. El Worker puede iniciarse en otro proceso posterior, cuando las variables de una Activity ya no existan.

## Reintentar sin repetir indefinidamente

`Result.retry()` solicita otro intento con la política de espera; `Result.failure()` termina el trabajo fallido; `Result.success()` lo completa. Un fallo temporal de red puede permitir reintento. Una configuración inválida o un permiso que requiere decisión de usuario no se corrigen repitiendo silenciosamente la misma llamada.

El ejemplo utiliza un fallo simulado y backoff exponencial con una base de 10 segundos. `runAttemptCount` permite distinguir intentos. No se necesita bloquear con `sleep` hasta que vuelva la red: el sistema administra la espera. Para una operación real, define límites, idempotencia y qué cambios hacen válida una repetición. La [guía de solicitudes](https://developer.android.com/develop/background-work/background-tasks/persistent/getting-started/define-work) desarrolla reintentos y restricciones.

Idempotencia significa que repetir no produce un efecto duplicado indebido. Una subida con el mismo identificador o una escritura que reconoce que ya se aplicó son estrategias posibles. «Puede reintentarse» no basta si cada intento cobra de nuevo, envía otro correo o crea una segunda nota.

## Trabajo único, cancelación y periodicidad

`enqueueUniqueWork` asocia una cadena a un nombre. El laboratorio usa `"revision"` y `ExistingWorkPolicy.REPLACE`: una nueva solicitud sustituye a la pendiente anterior. `KEEP` conservaría la existente. Elige la política según lo que signifique repetir la acción; un nombre único para toda la app puede impedir tareas independientes que sí deben coexistir.

Cancelar por nombre pide detener el trabajo, pero una operación no cooperativa puede tardar en responder. También hay que evitar publicar un éxito tardío o continuar un efecto externo después de cancelar. WorkManager registra estados que la pantalla puede observar mediante Flow; no dupliques una bandera local que se quede obsoleta al recrearse la Activity.

El trabajo periódico tiene un intervalo mínimo y ventanas de flexibilidad; no sirve para avisar cada pocos segundos. Para una necesidad exacta y justificada existen otras APIs y restricciones. Consulta sus requisitos actuales antes de introducir permisos especiales o prometer puntualidad. Un trabajo diferido, incluso persistente, no garantiza ejecución tras un **force stop** mientras la aplicación permanezca detenida por la persona.

## Verificar cada capa

Una prueba con `TestListenableWorkerBuilder` ejecuta el Worker con parámetros controlados. Puede comprobar su decisión de reintento y el aviso generado. No demuestra que el planificador respete red, demora o reinicio.

Para ese recorrido, encola realmente desde la app, observa ENQUEUED/RUNNING/SUCCEEDED o FAILED, cancela y repite con la red desactivada. Registra tiempos observados como observaciones, no como garantías de plataforma. La prueba remota o física que no puedas ejecutar sigue pendiente aunque el Worker aislado pase.

`PlanificacionTest`, en la solución, envía el receiver propio, espera la solicitud real y su reintento hasta éxito, y comprueba después cancelación. No usa un reloj virtual: necesita un dispositivo con conexión y un tiempo de espera acotado. No sustituye probar la ausencia de red, reinicio ni políticas de fabricantes diferentes.
