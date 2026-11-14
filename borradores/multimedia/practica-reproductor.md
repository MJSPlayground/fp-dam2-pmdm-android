# Práctica guiada: reproductor y animación

Construiremos controles y una animación ligados al estado real de Media3. Creación, carga y liberación están preparadas; completarás eventos, pausa, detención e indicador. Se incluye audio propio para empezar sin conexión. Lee [reproducción](reproduccion.md) y [animaciones](animaciones.md).

[Proyecto inicial — ZIP](../descargas/proyectos/multimedia-reproductor-inicial.zip){ .md-button .md-button--primary }

???+ example "Preparación y propietario del reproductor"

    Abre la carpeta con `settings.gradle.kts` en Android Studio y ejecuta **app**. Pulsa Reproducir: comienza el audio local, pero la interfaz aún no refleja correctamente `isPlaying`. Los TODO delimitan ese desfase deliberado y las operaciones pendientes.

    **Observa el código existente.** En `app/src/main/java/es/mjsplayground/pmdm/multimedia/reproductor`, abre `MainActivity.kt` y `ControlReproductor.kt`. La Activity crea el controlador en `onStart` y lo libera en `onStop`. Una recomposición no fabrica otro ExoPlayer. `cargar` asigna un `MediaItem` y prepara; `play` solicita reproducción y permite reiniciar después del final.

    **Comprueba.** Pulsa Inicio mientras suena: debe detenerse al abandonar la app. Vuelve y reproduce: debe existir una instancia utilizable. La liberación no depende de pulsar Detener.

??? example "Eventos: el motor cuenta lo que ocurre"

    **Ejemplo resuelto.** El listener copia `player.playbackState` a `fase` en `onEvents`. Distingue IDLE, BUFFERING, READY y ENDED; READY puede significar preparado y pausado. `isPlaying` informa de avance real. `onPlayerError` ya representa el fallo.

    **Ahora te toca.** En `player-eventos`, completa `reproduciendo` con la propiedad del motor siguiendo el patrón de `fase`. No lo asignes a verdadero desde el botón: el medio podría no abrirse.

    ??? tip "Pista: de dónde llega la información"

        Lee la propiedad del parámetro `player` que entrega el callback, en lugar de volver a copiar el valor anterior de `estado`.

    **Comprueba.** Al reproducir debe cambiar el texto y habilitarse Pausar. Al terminar debe verse Finalizado y habilitarse Reproducir. Si no cambia, comprueba el registro del listener y que la pantalla lee `actual.estado`.

??? example "Pausar y detener: operaciones equivalentes"

    **Observa el código existente.** `play()` comunica la acción al motor y prepara de nuevo si estaba detenido. El controlador evita repetir decisiones dentro de cada botón.

    **Ahora te toca.** Completa `player-pausa` y `player-detener`. Pausar conserva posición; detener deja de reproducir y sitúa el próximo inicio en cero. Las operaciones están explicadas en [reproducción](reproduccion.md#reproducir-pausar-detener-y-liberar). No liberes la instancia al detener: la pantalla debe reutilizarla.

    ??? tip "Pista: posición y estado son distintos"

        `Player.pause` y `Player.stop` tienen contratos distintos. `seekTo` elige posición. Usa un vídeo propio más largo que el tono para observar la diferencia.

    **Comprueba.** Reproduce, pausa y espera: el contenido queda inmóvil. Reanuda: continúa. Detén y reproduce: empieza desde cero. Repite después del final. Cambiar solo el texto sin actuar sobre el motor no supera la prueba.

??? example "Animación y vídeo: integrar"

    **Ahora te toca.** En `player-animacion`, liga `AnimatedVisibility` al estado de reproducción. Conserva el texto como alternativa al movimiento. La técnica se explica en [animaciones](animaciones.md).

    **Observa el código existente.** El selector entrega una URI; `AndroidView` conecta `PlayerView` a `actual.player`. Vista y motor tienen propietarios diferentes. Cancelar conserva el medio anterior.

    **Comprueba.** El indicador aparece al reproducir y desaparece al pausar o terminar. Pausa durante la transición: debe acabar oculto. Selecciona un vídeo propio y comprueba imagen, proporción y controles. Cancela otra selección: debe mantenerse el medio anterior.

??? example "Errores y registro final"

    **Ahora te toca.** En una copia de pruebas, pasa a `control.cargar` una URI de contenido inexistente utilizando el depurador. Identifica el callback del fallo y la acción que permite recuperarse. Conserva el recurso válido de arranque en tu entrega.

    **Comprueba.** Debe verse Error, no Finalizado. Carga de nuevo el audio: el fallo se limpia y puede reproducirse. Sal y vuelve varias veces: no deben acumularse instancias ni continuar audio de fondo. Gira y comprueba que no se cierra inesperadamente.

    Registra API, medio y estados observados; distingue salida audible y visual de pruebas automatizadas. Este laboratorio reinicia al volver a primer plano; no restaura posición ni reproduce en segundo plano.

## Solución de referencia

[Solución formativa completa — ZIP](../descargas/proyectos/multimedia-reproductor-solucion.zip){ .md-button }

Resuelve los cuatro TODO en los mismos archivos. Su README explica la procedencia del tono y las pruebas. Los ejercicios autónomos están en [consolidación](consolidacion.md).
