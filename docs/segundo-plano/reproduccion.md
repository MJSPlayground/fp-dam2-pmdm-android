# MediaSession y reproducción en segundo plano

## Separar pantalla y propietario del audio

En el reproductor del tema principal, la pantalla controla un reproductor ligado a su ciclo de vida. Para seguir escuchando al salir, la reproducción necesita otro propietario: `MediaSessionService`. La Activity se convierte en un controlador y deja de ser quien libera el reproductor al desaparecer.

La [guía de MediaSessionService](https://developer.android.com/media/media3/session/background-playback) relaciona reproductor, sesión, servicio y controles del sistema. El laboratorio conserva Media3 **1.9.4**, igual que el tema multimedia, añadiendo el artefacto `media3-session`.

| Pieza | Responsabilidad |
| --- | --- |
| `ExoPlayer` | Preparar y reproducir el medio |
| `MediaSession` | Publicar estado, comandos y metadatos |
| `MediaSessionService` | Poseer sesión y reproductor fuera de la Activity |
| `MediaController` | Conectar la UI con esa sesión y enviar acciones |
| Sistema | Presentar los controles multimedia y gestionar restricciones |

No crees un segundo ExoPlayer dentro de Compose para representar el mismo audio. Habría dos estados y dos recursos distintos. El controller se conecta asíncronamente a la sesión existente.

## Servicio y manifiesto

`ReproductorService.onCreate` construye un ExoPlayer, configura atributos de audio, carga el tono local y crea la sesión. `onGetSession` ofrece esa sesión a los controladores admitidos. `onDestroy` libera sesión y reproductor.

El manifiesto declara permisos de servicio en primer plano y de reproducción multimedia, el tipo `mediaPlayback` y la acción de `MediaSessionService`. La sesión permite integrarse con controles del sistema. No utilices un tipo de servicio distinto para ocultar lo que hace la app.

La reproducción se inicia al pulsar Reproducir en la pantalla. Preparar el servicio no reproduce automáticamente. Las [restricciones de inicio de servicios](https://developer.android.com/develop/background-work/services/fgs/restrictions-bg-start) siguen aplicándose: un servicio en primer plano no es una vía para ejecutar cualquier tarea desde fondo.

## Conectar y liberar el controller

La pantalla construye `SessionToken(contexto, ComponentName(...))` y solicita `MediaController.Builder(...).buildAsync()`. El futuro notifica cuando termina; solo entonces se habilitan los controles. No llames a `get()` para esperar bloqueando el hilo principal antes de recibir esa notificación.

En el código preparado, el listener se ejecuta en el executor principal, obtiene el controller ya disponible y actualiza la UI. `DisposableEffect` libera el futuro/controller cuando la pantalla desaparece. Esa liberación no debe destruir el reproductor del servicio que sigue sonando.

Pausar conserva la posición; detener interrumpe la reproducción y prepara el recorrido de reinicio. Si el player está en IDLE, Reproducir vuelve a prepararlo. El tono local de tres segundos se repite deliberadamente para observar el paso a segundo plano; la UI avisa y ofrece Detener. No dejes una prueba sonando al abandonar el laboratorio.

## Controles y foco de audio

Los metadatos dan título al contenido del sistema. La sesión comunica reproducción/pausa y permite controlarla desde la bandeja o pantalla de bloqueo. Los atributos de audio y el manejo de audio becoming noisy ayudan a responder a cambios de foco o salida; aun así, deben comprobarse interrupciones y auriculares en hardware real.

Las notificaciones de sesión multimedia tienen reglas propias, incluidas excepciones específicas respecto al permiso ordinario de notificaciones. No generalices esa excepción a un aviso de estudio ni confundas que el audio siga sonando con que todos los tipos de aviso sean visibles.

Si el proyecto fuera un reproductor público, habría que ampliar cola, errores, restauración, política de controles externos, autenticación de contenido y ciclo de vida del servicio. El ejemplo enseña una sesión local pequeña, no promete un servicio de música completo.

## Verificar el recorrido

Pulsa Reproducir, sal de la Activity y comprueba que la sesión sigue activa. Utiliza los controles del sistema, vuelve a la pantalla y detén. Repite pausa/reanudación y revisa que no se creen dos reproductores. Una prueba instrumentada puede observar estado `isPlaying` y una notificación activa; la audición y el comportamiento con auriculares requieren ejecución apropiada y no se deducen de ese booleano.

Una notificación inmediata termina al publicarse; WorkManager conserva una solicitud diferible; MediaSessionService mantiene una reproducción perceptible iniciada por la persona. Esa distinción evita utilizar un servicio de audio para programar tareas generales o exigir a un Worker que se comporte como un reloj exacto.
