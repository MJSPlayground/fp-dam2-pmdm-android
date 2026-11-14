# Notificaciones locales

## Del evento de la app a un aviso del sistema

Una notificación muestra información fuera de la pantalla de la aplicación. En este tema nace de una acción visible: pulsar un botón. **Local** significa que la app la construye en el dispositivo; no necesita Firebase, un mensaje push ni Internet.

La notificación no sustituye el estado de la pantalla. Si la persona deniega avisos, debe poder seguir utilizando la aplicación y entender qué ocurrió. El sistema decide la representación final: un aviso en la bandeja no implica necesariamente sonido o banner flotante. Consulta la [guía oficial de construcción](https://developer.android.com/develop/ui/views/notifications/build-notification).

## Canal: una categoría que controla el usuario

Desde API 26, toda notificación de esta app pertenece a un canal. El canal tiene un ID estable, nombre visible e importancia. La app crea el canal al arrancar; el usuario puede desactivarlo o cambiar sus preferencias desde ajustes.

Este fragmento del ayudante `Avisos` utiliza el `Context` que recibió al construirse:

```kotlin title="Avisos.kt — ejemplo de canal"
val canal = NotificationChannel(
    "acciones",
    contexto.getString(R.string.canal),
    NotificationManager.IMPORTANCE_DEFAULT
)
contexto.getSystemService(NotificationManager::class.java)
    .createNotificationChannel(canal)
```

Crear de nuevo el mismo canal permite asegurar su existencia. No reinicia decisiones del usuario ni debe usarse para eludirlas creando IDs nuevos. `NotificationCompat.Builder` debe usar el mismo ID; un texto visible parecido no reemplaza esa relación. Como el mínimo del proyecto es API 26, no necesitamos una rama sin canales.

## Permiso desde Android 13

`POST_NOTIFICATIONS` se declara en el manifiesto y se solicita en ejecución desde API 33. En API 26–32 no se solicita ese permiso, pero los ajustes de app y canal siguen pudiendo impedir los avisos. La [guía de permiso de notificaciones](https://developer.android.com/develop/ui/views/notifications/notification-permission) detalla el comportamiento.

La Activity registra `ActivityResultContracts.RequestPermission`. Al pulsar una acción comprueba la API y el permiso; si debe solicitarlo, conserva qué acción estaba pendiente. El callback continúa esa acción solamente si hay autorización. Una denegación muestra un mensaje; no provoca una excepción ni una cadena de diálogos. La pantalla ofrece abrir los ajustes si la persona desea cambiar su decisión.

Antes de publicar, el ayudante comprueba también `areNotificationsEnabled()` y la importancia del canal. El permiso puede revocarse entre comprobación y envío: por eso captura `SecurityException`. Estos controles permiten comunicar un bloqueo, pero no garantizan que la persona haya leído el aviso.

## Construir contenido e identificar avisos

`NotificationCompat.Builder` recibe contexto e ID de canal. Su icono pequeño debe ser una silueta propia legible; se entrega `ic_aviso.xml`. Título y texto se guardan en recursos. `setAutoCancel(true)` retira el aviso cuando se pulsa; no programa una hora de desaparición.

```kotlin title="Avisos.kt — fragmento del constructor"
val aviso = NotificationCompat.Builder(contexto, CANAL)
    .setSmallIcon(R.drawable.ic_aviso)
    .setContentTitle(contexto.getString(datos.titulo))
    .setContentText(contexto.getString(datos.texto))
    .setContentIntent(accion)
    .setAutoCancel(true)
    .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
    .build()
```

`datos` describe el mensaje; `accion` será su PendingIntent. El método `notify(datos.id, aviso)` envía la notificación. Reutilizar el ID **actualiza** ese aviso; otro ID permite otro aviso simultáneo. La demostración usa 1 y la nota, 2. No confundas esos números con el ID del canal, que representa una categoría y es una cadena.

`VISIBILITY_PRIVATE` limita contenido en la pantalla bloqueada según la configuración del sistema. Aun así, evita texto sensible. Este laboratorio usa mensajes formativos sin datos personales.

## Pulsar el aviso: PendingIntent

Un Intent expresa qué Activity abrir. Un **PendingIntent** permite que el sistema ejecute esa acción más tarde, cuando se pulsa el aviso. El ejemplo usa un Intent explícito, un dato sencillo `"demo"` o `"nota"` y `FLAG_IMMUTABLE`: otro proceso no debe modificar la acción que autorizamos.

`FLAG_UPDATE_CURRENT` actualiza los extras de una acción equivalente creada por nuestra app. El `requestCode`, aquí el ID del aviso, distingue los dos destinos pendientes. Los extras por sí solos no son una identidad distinta de PendingIntent.

La Activity puede recibir el dato al crearse en `onCreate`, o en `onNewIntent` si se reutiliza la instancia con `FLAG_ACTIVITY_SINGLE_TOP`. El ejemplo atiende ambas entradas, valida el valor recibido y muestra un detalle con una acción Volver. Tras volver elimina el extra para que un giro no reabra el detalle anterior. No se ejecuta un servicio ni se navega a una app externa.

## Casos que hay que distinguir

| Acción o condición | Resultado esperado |
| --- | --- |
| Permiso denegado | Mensaje en la app y uso normal de la pantalla |
| Canal desactivado | Estado de bloqueo, sin afirmar que el aviso es visible |
| Pulsar varias veces el mismo tipo | Un aviso actualizado con ese ID |
| Aviso básico y nota | Dos IDs distintos; pueden coexistir |
| Pulsar con app al fondo | Detalle correspondiente y regreso a acciones |
| Pulsar con Activity reutilizada | `onNewIntent` interpreta el nuevo destino |

La [práctica guiada](practica-notificaciones.md) parte del aviso básico completo y te pide construir uno equivalente asociado a una nota. La ampliación aborda notificaciones remotas, trabajo diferido y reproducción en segundo plano; aquí ninguna de esas capacidades es necesaria.
