# Práctica guiada: avisos locales

Construirás un aviso de **nota guardada** siguiendo el patrón de una notificación básica ya implementada. Android muestra ambos mensajes en su bandeja y abre un detalle al pulsarlos. Analizarás canal, permiso y destino preparados; después crearás un aviso equivalente y comprobarás actualizaciones y bloqueos. Lee antes [notificaciones](notificaciones.md).

[Descargar proyecto inicial — ZIP](../descargas/proyectos/multimedia-notificaciones-inicial.zip){ .md-button .md-button--primary }

???+ example "Preparación y primera notificación resuelta"

    Abre el proyecto en Android Studio, espera la sincronización y ejecuta **app**. La pantalla tiene dos acciones: **Mostrar aviso de ejemplo** funciona; **Avisar nota guardada** tiene pendiente construir sus datos. El laboratorio simula el evento de guardado mediante ese botón; no implementa una base de datos ni declara haber guardado un documento real.

    **Observa el código existente.** En `Avisos.kt`, localiza `crearCanal()`, el ID `acciones`, su nombre en recursos y `IMPORTANCE_DEFAULT`. Después sigue `demostracion()`: devuelve ID, título, texto y destino. Esos datos se reciben en `publicar`, que comprueba ajustes, construye el aviso y llama a `notify`.

    En `MainActivity.kt`, sigue el evento del botón hasta `solicitar(false)`. Desde API 33 puede pedir permiso; su respuesta continúa la acción pendiente. No hay servicio, tarea diferida ni comunicación con Internet.

    **Comprueba.** Pulsa el aviso básico, concede permiso cuando corresponda y despliega la bandeja. Debe verse **Comprobación completada** con el texto de demostración y un icono pequeño. No se exige un banner flotante. Abre **Ajustes de notificaciones** y localiza el canal **Acciones de la aplicación**.

??? example "Del aviso al detalle"

    **Ejemplo resuelto.** En `Avisos.publicar`, el Intent explícito apunta a `MainActivity`. Su extra `detalle-aviso` contiene `demo` para el aviso básico. `PendingIntent.getActivity` utiliza el ID como request code y las opciones `FLAG_UPDATE_CURRENT` y `FLAG_IMMUTABLE`. La teoría explica por qué los extras no bastan para distinguir dos acciones pendientes.

    **Observa el código existente.** Sigue el dato en `MainActivity.recibir`: solo acepta `demo` y `nota`. Se utiliza desde `onCreate` y `onNewIntent`, porque la Activity puede arrancar o reutilizarse. La pantalla ofrece **Volver a las acciones** y elimina el extra al regresar.

    **Ahora te toca.** Cambia únicamente los recursos `titulo_demo` y `texto_demo` por otro mensaje formativo sin datos sensibles. Conserva los nombres de recurso y el destino. Predice si pulsar tres veces crea tres avisos o actualiza uno.

    **Comprueba.** Repite la acción tres veces y revisa que hay un aviso con el texto modificado. Púlsalo: abre el detalle de demostración. Vuelve a las acciones y gira el dispositivo; no debe reabrirse un detalle que ya abandonaste. Repite enviando el aviso con la app en primer plano y con la app al fondo.

??? example "Ahora te toca: aviso de nota guardada"

    `DatosAviso` reúne cuatro propiedades: `id`, recurso de `titulo`, recurso de `texto` y `destino`. La demostración ya enseña una operación completa. Ahora necesitas otra operación equivalente, sin reemplazar la clase.

    **Ahora te toca — `notificacion-nota`.** Completa `notaGuardada()` en `Avisos.kt`. Sustituye el retorno pendiente por los datos de un aviso con ID **2**, recursos `titulo_nota` y `texto_nota` y destino **nota**. Los recursos y la recepción del destino están preparados. No cambies `demostracion()` ni reutilices el ID 1: ambos avisos deben poder coexistir.

    ??? hint "Pista: separa tipo de aviso y categoría"

        Compara los cuatro argumentos de `demostracion()` con las propiedades de `DatosAviso`. El ID del aviso distingue mensajes; el ID del canal sigue siendo `acciones` en ambos. La pantalla ya llama a `notaGuardada` cuando recibe `true`.

    **Comprueba.** Pulsa la nueva acción: desaparece el mensaje de TODO y el sistema recibe **Nota guardada**. Pulsa su aviso: abre el detalle de nota, no el de demostración. Envíalo junto al básico y comprueba que coexisten. Vuelve a enviar solo nota: se actualiza ese aviso sin duplicarlo. No interpretes este mensaje de laboratorio como persistencia real de una nota.

??? example "Denegación, canal desactivado y comprobación final"

    **Ahora te toca.** Ejecuta los casos siguientes sin modificar el código para forzar permisos. Registra API, autorización, ajustes y resultado observable.

    | Caso | Qué debe ocurrir |
    | --- | --- |
    | API 33 o posterior; permiso denegado | Mensaje y uso normal de la pantalla |
    | Permiso concedido; canal desactivado | Estado de bloqueo; no se afirma que se ve el aviso |
    | Canal activo; mismo tipo varias veces | Un aviso actualizado por ID |
    | Pulsar cada tipo de aviso | Detalle correspondiente y acción Volver |
    | Girar tras volver al inicio | No reaparece el detalle anterior |
    | API 26–32, cuando esté disponible | Canal y aviso sin solicitar POST_NOTIFICATIONS |

    Puedes denegar al solicitar por primera vez o cambiar la autorización desde ajustes. El sistema puede dejar de presentar el diálogo tras denegaciones; no abras solicitudes en bucle. La acción **Abrir ajustes de notificaciones** permite revisar la decisión.

    Comprueba también claro/oscuro, orientación horizontal y fuente grande: botones, texto y Volver deben seguir accesibles mediante desplazamiento. Si solo utilizaste API 36, registra la rama 26–32 como pendiente. Que compile no demuestra que la bandeja muestre el aviso ni que su pulsación abra el destino.

[Descargar solución formativa — ZIP](../descargas/proyectos/multimedia-notificaciones-solucion.zip){ .md-button }

Incluye ambos tipos de aviso, canal, permisos, destino, Activity y recursos. La solución es independiente del inicial. Programación diferida, FCM y servicios de reproducción pertenecen a la ampliación correspondiente.
