# Mensajería remota con Firebase Cloud Messaging

## Servidor, transporte y presentación

Una notificación local nace de una decisión de la app en el dispositivo. FCM permite que un backend autorizado envíe mensajes dirigidos a instalaciones. El transporte no sustituye autenticación del usuario, acceso a sus datos ni una estrategia de sincronización.

El proyecto incorpora el SDK `firebase-messaging` mediante la BOM **34.7.0**, coherente con la integración Firebase del curso. Utiliza el módulo principal, no un artefacto KTX retirado; la [referencia de versiones](https://firebase.google.com/support/release-notes/android) explica la organización de la BOM. El adaptador compila, pero el inicio automático remoto está desactivado y no se entrega un `google-services.json` inventado.

Para probar entrega real necesitas un proyecto Firebase propio, registrar el `applicationId`, configurar la app con sus valores reales y utilizar un dispositivo con los servicios compatibles. La [configuración Android](https://firebase.google.com/docs/cloud-messaging/android/client) detalla estos requisitos. No confundas la configuración pública de cliente con una cuenta de servicio: las credenciales administrativas nunca pertenecen al APK.

## Token de instalación y registro autenticado

Un token identifica una instalación a efectos de entrega; no es el ID de una persona ni una contraseña válida para autorizar operaciones de negocio. Puede cambiar al reinstalar, restaurar o renovarse. El backend debe actualizar sus asociaciones y retirar tokens inválidos.

En la copia configurada, `activarMensajeria` comprueba que exista una `FirebaseApp`, activa FCM y solicita el token. Recibe un callback para registrarlo mediante el backend autenticado; la práctica local no inventa ese servidor ni hace llamadas a una dirección de ejemplo.

`MensajesService.onNewToken` conserva el token pendiente en almacenamiento privado. En una integración real se envía por HTTPS junto a la sesión de usuario y metadatos mínimos, se confirma su recepción y se vuelve a intentar si falla el registro. Al cerrar sesión, revisa la asociación para no dirigir avisos de una cuenta a otra que usa el mismo dispositivo. No publiques tokens en capturas ni Logcat.

## Datos y notificación no se comportan igual

El resultado depende del tipo de mensaje y del estado de la app. La [guía de recepción](https://firebase.google.com/docs/cloud-messaging/android/receive) explica las rutas de entrega:

| Mensaje | App en primer plano | App en segundo plano, en el caso ordinario |
| --- | --- | --- |
| Notificación | Callback; la app decide presentación | El SDK/sistema puede presentar el aviso |
| Datos | Callback para el contenido | Callback sujeto a entrega, prioridades y restricciones |
| Notificación y datos | Callback | Aviso; los datos suelen acompañar la apertura por la acción |

La tabla no es una promesa de entrega instantánea ni de ejecución ilimitada. Permisos, conectividad, ahorro energético, configuración del canal y estado de la instalación afectan al resultado. No uses prioridad alta para toda actualización ordinaria ni para eludir las restricciones.

En el adaptador del proyecto solo se acepta un dato con `tipo=revision`. No se ejecuta contenido remoto como código. `onMessageReceived` delega una revisión a WorkManager; no realiza allí una descarga larga. El contenido visible del aviso sigue siendo un recurso controlado por la app. Para una app real, valida el contrato y accede a datos sensibles mediante su autorización normal.

## Envío desde un backend confiable

El cliente Android **no** debe llevar una clave de servidor para enviarse mensajes. El envío se realiza desde un backend con Admin SDK o HTTP v1, autenticado mediante credenciales administradas en el servidor. El [protocolo HTTP v1](https://firebase.google.com/docs/cloud-messaging/send/v1-api) utiliza autorización de acceso del entorno del backend, no una clave incrustada en un `build.gradle.kts`.

El cuerpo conceptual de un envío de datos dirigido a un token es:

```json
{
  "message": {
    "token": "TOKEN_DE_LA_INSTALACION_REGISTRADA",
    "data": { "tipo": "revision" }
  }
}
```

El texto en mayúsculas es un marcador de documentación, no un token suministrado. Faltan el proyecto y la autorización real del servidor; este JSON por sí solo no envía nada. Protege el endpoint de envío, limita destinatarios y evita que una app pueda ordenar avisos a cualquier token ajeno.

## Verificación que requiere el servicio real

Comprueba registro inicial, renovación, primer plano, segundo plano, apertura, permiso denegado, token inválido y cierre de sesión. Contrasta el identificador del mensaje en el servidor y el resultado observado, sin volcar tokens privados en un informe público.

La compilación del adaptador y las pruebas locales de WorkManager no acreditan FCM. La práctica funciona sin cuenta precisamente para separar esas evidencias; la entrega remota queda pendiente hasta configurar y probar una integración real.
