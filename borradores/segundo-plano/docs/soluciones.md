# Soluciones de segundo plano

## Actividad de política

[Proyecto solución — ZIP](../descargas/proyectos/segundo-plano-solucion.zip){ .md-button }

`Politica.kt` ofrece una decisión mediante un enum y una función pura. `PoliticaTest` comprueba los casos del enunciado y el rechazo de entradas negativas. Ejecuta esa clase dentro de **test** con Run en Android Studio, sin emulador ni servicios. La representación elegida es una referencia; otras son válidas si conservan el mismo comportamiento observable.

La función no programa un Worker ni garantiza idempotencia de una sincronización real. Esas responsabilidades se comprueban en otras capas. El proyecto inicial no contiene esa solución autónoma.

## Elección de mecanismos

Guardar desde una pantalla puede producir una notificación local inmediata si existe un motivo útil y autorización. Una sincronización diferible puede ser WorkManager, con restricciones y política de repetición. La escucha iniciada por la persona corresponde a MediaSessionService y su tipo de servicio de reproducción. Un evento del sistema puede recibirse mediante un receiver apropiado a esa acción, versión y propietario; su trabajo largo se delega. Un mensaje del backend utiliza FCM con registro autenticado de tokens, sin poner autorización administrativa en el cliente.

## Repaso

1. No. Publicar un aviso y ejecutar trabajo son acciones distintas. El sistema puede mantener visible el aviso cuando el proceso que lo publicó ya no existe.
2. El canal aporta la importancia y la persona puede modificar sus ajustes. `setPriority` es relevante para versiones anteriores y no reemplaza ese canal. No se deben crear canales nuevos para eludir una decisión.
3. Sí. Puede estar desactivada la app o el canal, intervenir la configuración de presentación o no haberse entregado un mensaje remoto. Conceder permiso no demuestra toda la cadena.
4. El mismo ID actualiza la entrada existente. Un estilo cambia presentación, no programación ni propiedad del proceso.
5. Es un límite inferior; el sistema y las restricciones pueden retrasar más. La conectividad condiciona si puede empezar o continuar, pero no demuestra una respuesta válida del servidor.
6. Retry es para un fallo temporal recuperable bajo una política acotada; failure termina un caso que no debe repetirse sin cambios. Repetir una operación no idempotente puede duplicar efectos externos.
7. KEEP conserva una cadena existente apropiada; REPLACE cancela y sustituye. La cancelación es una petición y el trabajo debe cooperar; una llamada bloqueante puede tardar en responder.
8. No acredita la planificación real, demora mínima, condiciones de red, reinicio ni restricciones de energía. El test ha ejecutado una unidad con parámetros controlados.
9. Es un Intent propio y explícito, no un evento de hardware ni arranque. `onReceive` valida y delega pronto; no debe mantener allí una operación lenta ni una referencia de pantalla.
10. No. El tipo de mensaje y el estado de la app alteran la ruta: en segundo plano un aviso con datos puede presentarse por el SDK y entregar los datos al abrir, sin el mismo callback de primer plano.
11. Identifica una instalación para entrega, no autorización de negocio. Hay que actualizar asociaciones, retirar tokens inválidos y revisar el vínculo al cerrar sesión para no enviar avisos de una cuenta a otra.
12. En un backend confiable con credenciales administradas y acceso autorizado. Incluir una cuenta de servicio permite extraer privilegios administrativos del cliente y no protege el envío por usuario.
13. El servicio posee ExoPlayer y MediaSession. La pantalla libera su conexión/controller; no destruye la sesión que debe continuar durante la escucha.
14. El servicio debe corresponder a una actividad perceptible y autorizada, con restricciones propias. `isPlaying` comprueba estado del player, pero no audición, salida física, auriculares, foco de otros dispositivos ni todas las condiciones de energía.
