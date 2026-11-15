# BroadcastReceiver y eventos del sistema

## Recibir un evento no abre una ejecución ilimitada

Un broadcast comunica que ocurrió algo: un cambio del sistema o un evento de una aplicación. `BroadcastReceiver.onReceive` recibe el `Intent`, interpreta su acción y debe terminar pronto. No es una pantalla ni un propietario adecuado para una descarga larga.

La [guía de broadcasts](https://developer.android.com/develop/background-work/background-tasks/broadcasts) distingue registro mediante contexto y declaración en manifiesto. Las restricciones dependen del evento, la versión y el estado del proceso. Declarar cualquier acción en XML no garantiza recibirla en todas las versiones.

El laboratorio utiliza un evento **propio y explícito**: el botón construye un Intent dirigido a `RevisionReceiver`, que está declarado con `exported="false"`. Otra aplicación no puede utilizarlo como una API pública para programar avisos. No se hace pasar por un evento de batería, arranque o red que no se haya producido.

## Validar y delegar

Este fragmento muestra el contrato de la recepción dentro de `onReceive`:

```kotlin
if (intent.action == ACCION) {
    programar(context.applicationContext)
}
```

`ACCION` es la constante de la aplicación y `programar` encola la solicitud de WorkManager. La recepción valida qué evento entiende y delega; no bloquea el hilo ni conserva la referencia al receiver para actuar mucho después de que termine el callback.

Un Intent explícito identifica componente, pero conviene validar igualmente su acción y los datos que esperas. Si el producto necesitase recibir eventos de otras aplicaciones, habría que diseñar autorización, exportación y validación de extras. No basta con cambiar `exported` a `true` para facilitar una prueba.

## Registro durante una pantalla activa

Un receiver registrado por contexto puede observar ciertos eventos mientras su propietario vive. Por ejemplo, una función de pantalla puede registrar un observador y retirarlo con `DisposableEffect`, igual que se retiraban listeners en la ampliación de asincronía. La simetría impide acumular receptores al recomponer.

Fragmento conceptual dentro de ese efecto, con `receiver` y `filtro` ya preparados:

```kotlin
ContextCompat.registerReceiver(
    contexto, receiver, filtro, ContextCompat.RECEIVER_NOT_EXPORTED
)
onDispose { contexto.unregisterReceiver(receiver) }
```

El flag expresa el alcance esperado para el tipo de evento. Algunos broadcasts de otros componentes del sistema requieren estudiar exportación y permisos específicos; no apliques una regla única sin comprobar quién envía. Tampoco presupongas que el receiver de una pantalla seguirá activo después de abandonarla.

## Restricciones modernas

Desde Android 8, la mayoría de broadcasts implícitos no pueden recibirse simplemente declarando un receiver en el manifiesto de una app que apunta a esa versión o posterior. Existen excepciones documentadas para acciones concretas. Las versiones actuales también pueden diferir entregas para procesos en caché y limitar comportamientos de fondo.

Recibir un broadcast no concede automáticamente permiso para iniciar un servicio en primer plano desde segundo plano. Debes cumplir las reglas del tipo de servicio y el estado de la aplicación. Un evento de arranque tampoco es un permiso general para iniciar reproducción sin una acción de la persona.

Para observar conectividad de forma adecuada utiliza las APIs de conectividad y callbacks que correspondan; no reconstruyas toda la lógica de red a partir de un broadcast antiguo. Para trabajo diferible, recibe el evento mínimo y deja la planificación a WorkManager.

## Comprobación proporcionada

En la práctica, el botón Enviar evento propio debe terminar produciendo la misma solicitud única que Programar revisión. Una acción distinta no debe encolarla. Cancelar y volver a enviar permite comprobar el reemplazo sin generar muchas tareas independientes.

Esa prueba acredita un evento propio. No demuestra recepción tras reiniciar el dispositivo, cambios de red reales ni comportamiento de todas las capas de ahorro del fabricante. Registra cada escenario por separado y conserva como pendiente lo que no se haya reproducido.
