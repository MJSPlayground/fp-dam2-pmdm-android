# Permisos y acceso seguro

## Una capacidad no implica acceso ilimitado

Android aísla aplicaciones y limita el acceso a recursos del dispositivo. Antes de añadir un permiso, identifica qué dato necesita realmente la función. Seleccionar una fotografía no exige leer toda la biblioteca; enviar una notificación no justifica obtener la ubicación; reproducir un archivo elegido por la persona no requiere explorar su almacenamiento.

Hay permisos **normales**, declarados en el manifiesto y concedidos por el sistema según su contrato, como `INTERNET`. Otros requieren además una decisión en tiempo de ejecución, por ejemplo la ubicación. La declaración de un permiso peligroso en XML no equivale a que el usuario ya lo haya concedido.

Una funcionalidad puede recibir acceso mediante otro mecanismo: el selector de fotos devuelve una URI autorizada para el elemento elegido; una app de cámara puede escribir en la URI que le proporcionamos; Sharesheet entrega contenido al destinatario seleccionado. Estos mecanismos acotan el acceso y evitan pedir permisos amplios innecesarios.

La [guía de permisos Android](https://developer.android.com/training/permissions/requesting) explica la secuencia de declaración, comprobación, solicitud y respuesta. Es un contrato que puede variar por nivel de API y por función; no memorices una lista de permisos para añadirla a cualquier proyecto.

## Qué necesita cada laboratorio

| Función | Acceso utilizado | Qué no se presupone |
| --- | --- | --- |
| Elegir una imagen o vídeo | Photo Picker y URI seleccionada | Lectura de toda la galería |
| Capturar con la app de cámara | `TakePicture` y URI de salida propia | Uso directo de CameraX ni permiso preventivo `CAMERA` |
| Transformar un bitmap y escribir en almacenamiento privado | Acceso a la entrada seleccionada y directorio propio | Permiso general de almacenamiento |
| Mostrar un mapa remoto | `INTERNET` para el proveedor | Acceso automático a la ubicación |
| Obtener ubicación puntual | Permiso de ubicación durante uso | Seguimiento continuo o en segundo plano |
| Mostrar una notificación | Canal y, desde API 33, autorización de notificaciones | Mensajes push ni ejecución diferida |

Cuando delegamos la captura en una app de cámara instalada, no añadiremos `CAMERA` a nuestro manifiesto. Ese permiso corresponderá cuando nuestra propia aplicación utilice la cámara directamente, como en la ampliación CameraX. Añadirlo sin necesitarlo también puede cambiar el comportamiento exigido al lanzar la captura.

## Solicitar cuando el usuario entiende para qué

Una solicitud debe estar asociada a una acción reconocible: **Obtener ubicación** es un buen momento para explicar por qué se necesita ese dato. Pedir todos los permisos al abrir la aplicación por primera vez no da contexto y provoca denegaciones difíciles de interpretar.

Antes de acceder a una capacidad protegida, comprueba la autorización actual. Puede haber cambiado desde la última sesión: la persona puede retirarla desde ajustes y el sistema puede revocar permisos de aplicaciones que no se utilizan. No confíes únicamente en un booleano guardado por tu app.

`ContextCompat.checkSelfPermission` comprueba un permiso y devuelve un resultado comparable con `PackageManager.PERMISSION_GRANTED`. Para solicitar uno o varios, las APIs de **Activity Result** evitan gestionar manualmente códigos numéricos de resultado.

Este fragmento registra una respuesta en una Activity para un permiso individual. El bloque que se ejecuta cuando responde el usuario es el lugar donde continuamos o presentamos la alternativa, no una garantía de acceso antes de recibir la respuesta:

```kotlin title="Ejemplo de registro de un permiso en una Activity"
private val permiso = registerForActivityResult(
    ActivityResultContracts.RequestPermission()
) { concedido ->
    if (concedido) {
        // Continuar con la acción solicitada, comprobando sus demás requisitos.
    } else {
        // Mantener una pantalla útil y explicar la alternativa.
    }
}
```

`ActivityResultContracts` se importa de `androidx.activity.result.contract`; `registerForActivityResult` lo proporciona la Activity compatible. El registro se realiza como parte de su inicialización, antes de que pase al estado iniciado; no se crea un lanzador diferente por cada pulsación. Posteriormente `permiso.launch(nombreDelPermiso)` abre la solicitud cuando corresponde.

Las solicitudes de dos permisos, como ubicación aproximada y precisa, utilizan `RequestMultiplePermissions`: su callback recibe un mapa de nombre de permiso a resultado. Aprenderemos cómo interpretar ambas posibilidades en la [página de ubicación](ubicacion.md).

## Denegar también es un resultado normal

La app debe seguir siendo comprensible cuando se deniega el acceso. El mapa puede mostrar un lugar de ejemplo sin localizar al usuario. El laboratorio de notificaciones puede mostrar que falta autorización y explicar cómo habilitarla. No cierres la aplicación ni vuelvas a abrir inmediatamente el mismo diálogo en un bucle.

`shouldShowRequestPermissionRationale` ayuda a decidir si conviene explicar por qué se solicita un permiso antes de volver a pedirlo. Un resultado `false` no identifica por sí solo una situación única: puede ocurrir antes de la primera solicitud o cuando ya no se va a presentar el diálogo. Evita deducir «denegado permanentemente» solo con esa comprobación.

Si la persona desea habilitar una función bloqueada desde ajustes, ofrece una acción explícita para abrir la configuración de la app. No la redirijas allí sin contexto ni la obligues a conceder algo para utilizar capacidades independientes.

## Activity Result: entrada, lanzamiento y respuesta

Un **contrato** describe qué recibe una operación y qué resultado devuelve. `PickVisualMedia` recibe una solicitud con el tipo de medio y devuelve una URI o `null`; `TakePicture` recibe una URI de salida y devuelve un booleano; `RequestPermission` recibe el nombre de permiso y devuelve si se concedió.

```text
Registrar contrato y callback
             ↓
Acción del usuario → launch(entrada)
             ↓
Sistema u otra aplicación
             ↓
Callback(resultado) → nuevo estado de pantalla
```

No se puede leer el resultado como si `launch` fuera una función que devuelve inmediatamente la fotografía. El sistema puede abrir otra pantalla y volver después; entre ambos momentos puede recrearse nuestra Activity. Conserva la información necesaria para reconocer el resultado, por ejemplo la URI preparada para la captura, en estado restaurable apropiado.

La [guía de resultados de actividades](https://developer.android.com/training/basics/intents/result) explica el registro estable de callbacks y la separación entre registro y lanzamiento.

## Privacidad y duración del acceso

Una URI seleccionada no es un permiso para enviar contenido a un servidor. Para procesar una foto localmente no necesitamos subirla. Si otra funcionalidad introduce envío externo, debe explicar qué sale del dispositivo y por qué.

El acceso concedido a contenido puede tener una duración limitada. Si necesitas conservar una entrada para trabajo posterior, usa el mecanismo de persistencia que soporte el proveedor o copia el contenido necesario a un directorio privado adecuado. Gestiona la pérdida de acceso y la desaparición del recurso; una cadena URI guardada no mantiene vivo el archivo ni obliga al proveedor a conservarlo.

Evita registrar fotografías privadas, coordenadas precisas o URIs con información sensible en Logcat o repositorios. En las prácticas utiliza archivos preparados y lugares públicos. La seguridad incluye pedir menos datos y conservarlos durante menos tiempo, además de comprobar permisos.
