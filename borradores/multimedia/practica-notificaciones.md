# Práctica: notificación local

## Preparar el proyecto

Lee [notificaciones locales](notificaciones.md) y la distinción de [permisos](permisos.md). Esta práctica muestra un aviso del sistema después de una acción visible de la aplicación; no utiliza Firebase ni un servicio de segundo plano.

[Proyecto inicial — ZIP](../descargas/proyectos/multimedia-notificaciones-inicial.zip){ .md-button .md-button--primary }

Ejecuta **app** e identifica la acción para mostrar el aviso y el lugar donde se comunica el resultado. Para comprobar el permiso de notificación necesitas Android 13/API 33 o posterior; en una versión anterior debes comprobar el camino sin ese permiso de ejecución.

## Crear el canal

En `notificacion-canal`, crea un canal con un identificador estable, nombre comprensible e importancia proporcionada al ejemplo. La aplicación tiene mínimo API 26, por lo que los canales están disponibles en todos sus dispositivos admitidos.

El identificador vincula el aviso al canal. Crear un canal con un nombre visible diferente del identificador no es un error; pasar al constructor de la notificación un identificador de canal inexistente sí lo es. Repetir la creación con el mismo ID permite asegurar que existe, pero no devuelve a la app el control sobre las preferencias que el usuario haya cambiado.

Ejecuta y abre los ajustes de notificaciones de la aplicación. Debe aparecer el canal previsto. No concluyas que el aviso funciona solo porque este paso no produce una excepción.

## Construir el contenido

En `notificacion-contenido`, utiliza `NotificationCompat.Builder` con ese canal. Añade un icono pequeño monocromo propio, título y texto. La notificación es una representación del sistema; no uses como icono pequeño una fotografía o un recurso transparente vacío.

Asigna un identificador de notificación reconocible. Reutilizarlo actualiza el aviso correspondiente; utilizar uno nuevo crea otro. El laboratorio evita acumular avisos repetidos por cada pulsación.

La acción de mostrar debe comunicar si pudo publicar o si existe una restricción. Una llamada a `notify` no garantiza que el usuario vea un banner flotante: influyen el canal, sus ajustes y las condiciones del sistema.

## Pedir permiso cuando corresponde

En `notificacion-permiso`, comprueba el nivel de API. Desde API 33, `POST_NOTIFICATIONS` se solicita al pulsar la acción que necesita el aviso. Explica su utilidad y gestiona el resultado del contrato.

Si se concede, continúa con la publicación. Si se deniega, muestra un estado y conserva el uso normal de la app. No abras solicitudes en bucle ni trates la denegación como un fallo excepcional del sistema.

También debes comprobar si las notificaciones de la aplicación o del canal están desactivadas. Conceder un permiso no implica que todos los canales puedan avisar. En API 26–32 no debe aparecer una petición de `POST_NOTIFICATIONS`, pero los ajustes del usuario siguen pudiendo impedir avisos.

## Abrir la aplicación al pulsar

En `notificacion-destino`, prepara un Intent explícito hacia la Activity de la aplicación y los datos sencillos que identifican el detalle. Encapsúlalo en un PendingIntent inmutable; Android ejecutará esa acción más tarde, al pulsar el aviso.

La Activity interpreta el dato tanto al crearse como cuando recibe un nuevo Intent estando ya abierta. Comprueba ambas situaciones: aplicación cerrada y aplicación visible. La pantalla de detalle debe ofrecer una forma coherente de volver al contenido principal.

No pongas información sensible en el texto de una notificación que pueda mostrarse en la pantalla bloqueada. En este ejemplo basta un mensaje formativo sin datos personales.

## Ejecutar los casos

1. En Android 13 o posterior, solicita el aviso y deniega el permiso. Verifica que la app explica el estado y sigue respondiendo.
2. Autoriza las notificaciones desde el flujo permitido por el sistema y vuelve a pulsar. Despliega la bandeja y comprueba título, texto e icono.
3. Pulsa el aviso con la app en segundo plano. Debe abrir el detalle previsto.
4. Repite con la app ya abierta. Comprueba la recepción del nuevo Intent.
5. Desactiva el canal desde ajustes y repite. La app debe tratar el bloqueo sin afirmar que el aviso es visible.
6. Cuando dispongas de API 26–32, comprueba que funciona el canal sin solicitar el permiso introducido en 33.

Anota sistema, permiso y ajustes de canal de cada prueba. Si solo probaste API 36, no escribas que ejecutaste todas las ramas por debajo de 33.

## Consolidación

Cambia título y texto mediante recursos y utiliza dos mensajes distintos con el mismo identificador de aviso. Predice si la bandeja mostrará una actualización o dos notificaciones. Después compara con otro identificador y explica el resultado. Mantén el ejemplo acotado a notificaciones inmediatas: programación diferida y FCM se estudian en la ampliación.

[Solución formativa completa — ZIP](../descargas/proyectos/multimedia-notificaciones-solucion.zip){ .md-button }

Contiene canal, comprobación de permisos y ajustes, PendingIntent, Activity y recursos. Consulta su README para ejecutar las pruebas y conocer los caminos todavía pendientes de dispositivo o versión.
