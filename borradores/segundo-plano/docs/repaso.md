# Actividades y repaso de segundo plano

## Actividad autónoma: una política de reintentos

Una aplicación debe decidir qué hacer al terminar un intento de sincronización. Si se completó, termina con éxito. Si falló de forma temporal y todavía no ha agotado su máximo de reintentos, pide otro intento. En cualquier otro fallo, termina sin seguir repitiendo.

El primer intento se identifica con cero. Con un máximo de dos reintentos, los fallos temporales de los intentos cero y uno pueden pedir repetición; el del intento dos ya termina. Un máximo cero no permite reintentar. Los índices y máximos negativos son entradas inválidas y deben rechazarse.

Implementa una decisión independiente de Android y prepara casos para éxito, fallo permanente, temporal antes del límite, temporal en el límite y máximo cero. No realices una espera ni contactes con una red para probar la política: la planificación queda fuera de esta decisión. No cambies silenciosamente un fallo permanente por uno temporal para hacer pasar un test.

[Solución de referencia — ZIP](../descargas/proyectos/segundo-plano-solucion.zip){ .md-button }

## Elegir el mecanismo

Justifica qué mecanismo utilizarías en cada caso y qué condiciones tendrías que comprobar:

- Mostrar un aviso al guardar una nota desde la pantalla.
- Reintentar una sincronización cuando exista red sin exigir un instante exacto.
- Escuchar audio que la persona ha iniciado aunque cambie de aplicación.
- Reaccionar brevemente a un evento documentado del sistema.
- Recibir un mensaje que un backend dirige a una instalación autorizada.

## Repaso razonado

1. ¿Crear una notificación mantiene un proceso ejecutándose? ¿Qué diferencia hay entre avisar y planificar?
2. ¿Qué determina la importancia en API 26 o posterior y qué papel conserva `setPriority`? ¿Puedes cambiar libremente la decisión de un canal ya existente?
3. Si el permiso está concedido, ¿puede seguir sin verse el aviso? Distingue ajustes generales, canal y entrega.
4. ¿Por qué reutilizar el ID 42 evita llenar la bandeja de avisos? ¿Un estilo expandible cambia la hora de ejecución?
5. ¿Qué promete `setInitialDelay(10 segundos)` y qué no? ¿Para qué sirve una restricción de red?
6. ¿Cuándo corresponde `retry` y cuándo `failure`? ¿Qué peligro tiene reintentar una operación no idempotente?
7. ¿Qué cambia entre KEEP y REPLACE para trabajo único? ¿Cancelar garantiza detener inmediatamente una operación bloqueante?
8. Una prueba construye un Worker con `TestListenableWorkerBuilder` y termina correctamente. ¿Qué sigue sin demostrar sobre WorkManager?
9. ¿Por qué el receiver del laboratorio no acredita un cambio de batería ni recepción después de un reinicio? ¿Qué debe hacer `onReceive` con una operación larga?
10. ¿Una notificación con datos FCM llega siempre a `onMessageReceived` de la misma forma en primer y segundo plano?
11. ¿Por qué el token no debe utilizarse como autenticación del usuario? ¿Qué ocurre al renovar o cambiar de cuenta?
12. ¿Dónde reside la autorización para enviar por HTTP v1? ¿Qué problema habría al incluir una cuenta de servicio en el APK?
13. ¿Qué objeto posee el reproductor en el laboratorio? ¿Qué libera el efecto de la pantalla al salir?
14. ¿Por qué una sesión de audio no es una solución para ejecutar tareas generales en fondo? ¿Qué comprobaciones de hardware no sustituye `isPlaying`?

[Respuestas](soluciones.md)
