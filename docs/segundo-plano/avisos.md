# Canales, estilos y decisiones de notificación

## Avisar no es mantener una tarea viva

Una notificación informa; no garantiza que un proceso continúe ejecutándose. Pulsar un botón puede mostrar un aviso inmediato aunque la app no tenga ningún trabajo pendiente. Un Worker puede terminar y avisar después. Un reproductor en primer plano necesita un tipo de servicio y controles relacionados con la reproducción. Son contratos diferentes.

La [guía de notificaciones](https://developer.android.com/develop/ui/views/notifications/build-notification) describe `NotificationCompat.Builder`, contenido y acciones. El laboratorio reutiliza un canal, icono monocromo y `PendingIntent` para abrir la app. El permiso se solicita a partir de una acción visible en las versiones que lo necesitan.

## Importancia del canal y prioridad del aviso

Desde API 26, el canal agrupa notificaciones según una finalidad que la persona puede gestionar. Su importancia determina el comportamiento de interrupción, junto con ajustes del sistema. `NotificationCompat.setPriority` conserva utilidad para versiones anteriores; no sustituye la importancia del canal en las nuevas.

| Finalidad | Decisión razonable | Evitar |
| --- | --- | --- |
| Resumen ordinario | Importancia normal y contenido revisable | Interrumpir como una alarma urgente |
| Información silenciosa | Canal de baja importancia | Sonido obligatorio para todo evento |
| Aviso que exige atención inmediata | Canal apropiado y motivo real | Elevar importancia para forzar atención |

Cambiar el valor de importancia en el código después de crear un canal no reescribe libremente la elección existente de la persona. No crees canales nuevos continuamente para sortear una desactivación. Define IDs estables por finalidad y dirige a ajustes cuando la funcionalidad esté bloqueada.

`POST_NOTIFICATIONS` no es la única condición: la app o el canal pueden estar desactivados. `Avisos.permitido` comprueba permiso, habilitación general y canal; aun así, la autorización puede cambiar antes de `notify`. El código trata esa posibilidad sin cerrar la aplicación.

## Contenido expandible y agrupación

Una línea breve comunica el resultado esencial. `BigTextStyle` permite ampliar una explicación; `InboxStyle` presenta varias líneas relacionadas; `BigPictureStyle` muestra una imagen cuando aporta información. No utilices un estilo grande para esconder el resumen que se necesita en la vista contraída.

Este fragmento se añade al builder ya existente cuando el aviso es un resumen:

```kotlin
aviso.setStyle(
    NotificationCompat.BigTextStyle().bigText(textoCompleto)
)
```

`textoCompleto` es una cadena preparada por la aplicación. El proyecto la obtiene de recursos. Los estilos no cambian el mecanismo de programación ni convierten el aviso en una tarea de fondo.

Si se generan muchos avisos relacionados, `setGroup` y una notificación resumen pueden ayudar a ordenarlos. La agrupación no sustituye un identificador de negocio. Reutilizar el mismo ID actualiza un aviso; generar IDs distintos crea avisos separados. El laboratorio usa el ID 42 para que las comprobaciones no llenen la bandeja de copias.

## Iconos, privacidad y acción de apertura

El icono pequeño debe funcionar como silueta; una fotografía o un logotipo con un rectángulo opaco suele perder significado al adaptarse a la barra. Se incluye un vector original de notificación. El título y texto deben ser comprensibles también en la bandeja y con lector de pantalla.

Un `PendingIntent` permite que Android ejecute posteriormente la acción definida. En el ejemplo abre una Activity concreta y usa `FLAG_IMMUTABLE`, porque no se necesita que quien lo recibe cambie sus datos. Si una función requiriera mutabilidad, habría que justificar el contrato y limitar el alcance, no usarla por defecto.

No muestres información sensible completa en la pantalla bloqueada. La visibilidad y una versión pública permiten ajustar lo que se presenta, pero los ajustes de la persona también intervienen. Prueba notificación contraída/expandida, canal desactivado, permiso denegado y apertura; compilar el builder no demuestra que el aviso sea visible en ese dispositivo.
