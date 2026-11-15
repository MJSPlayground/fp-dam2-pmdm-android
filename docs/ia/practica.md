# Práctica guiada: consulta con texto e imagen

Construiremos un cliente con carga, cancelación y adjunto opcional. HTTP, preparación de fotografías y servidor están preparados; completarás validación y cancelación. El servidor arranca en modo simulado, explícito en pantalla, para probar el recorrido sin credenciales ni coste. Lee [modelos](modelos.md), [integración](integracion.md) y [multimodal](multimodal.md).

[Proyecto inicial — ZIP](../descargas/proyectos/ia-inicial.zip){ .md-button .md-button--primary }

???+ example "Preparación y recorrido resuelto"

    Inicia `servidor/servidor.py` con Python3.11 según el README y conserva esa terminal abierta. Abre la carpeta con `settings.gradle.kts` en Android Studio, sincroniza y ejecuta **app** en un emulador Android. El servidor escucha en el ordenador; `10.0.2.2` permite alcanzarlo desde el emulador.

    **Observa el código existente.** Dentro de `app/src/main/java/es/mjsplayground/pmdm/ia`, sigue `MainActivity.kt` → `ConsultaViewModel.kt` → `ClienteIa.kt`. La UI emite eventos; el ViewModel conserva el estado; el cliente ejecuta un POST al servidor. El servidor simulado no analiza la foto y no llama a un modelo.

    **Comprueba.** La app arranca, muestra la advertencia de envío y permite preparar una foto. Consultar sigue deshabilitado porque `ia-validacion` devuelve provisionalmente false. El arranque no debe solicitar una clave del proveedor ni abrir una cuenta externa.

??? example "Validar antes de enviar"

    Ya has implementado validación de formularios en Cuaderno. Aquí validaremos una pregunta por su longitud después de recortar espacios; la misma función decide si habilitar el botón y se comprueba otra vez al ejecutar la acción.

    **Ahora te toca — `ia-validacion`.** Completa `consultaValida` en `ClienteIa.kt`: admite de 3 a 2000 caracteres recortados, incluidos ambos límites. No sustituyas la validación del servidor por esa función: el backend conserva sus controles independientes.

    ??? tip "Pista: comprobar el valor que se va a enviar"

        Combina `trim`, longitud y un rango inclusivo. Los espacios de los extremos no deben convertir una pregunta de dos caracteres en una entrada válida. No necesitas una expresión regular para esta condición.

    **Comprueba.** Dos caracteres o solo espacios mantienen el botón deshabilitado; tres y 2000 lo habilitan, 2001 no. Consulta con un texto de prueba: debe aparecer la frase de demostración y la etiqueta de respuesta simulada. El cliente no debe mostrarla como una inferencia real.

??? example "Cancelar usando un patrón ya preparado"

    **Ejemplo resuelto.** Al comenzar otra consulta, `consultar` llama a `tarea?.cancel()` antes de lanzar un trabajo nuevo. La excepción de cancelación se relanza; el bloque de error trata las incidencias del servicio, no el abandono elegido por la persona.

    **Ahora te toca — `ia-cancelacion`.** Completa `cancelar` en `ConsultaViewModel.kt`: cancela la tarea y publica un estado sin carga, con el recurso `R.string.cancelada`. No muestres `error_red` para una cancelación deliberada ni dejes el botón bloqueado hasta que termine el timeout.

    ??? tip "Pista: trabajo y estado son dos cambios"

        Detener la corrutina evita publicar su respuesta, pero la pantalla también necesita abandonar el indicador de carga. Utiliza `copy` y la actualización del StateFlow del mismo modo que en los otros eventos del ViewModel.

    **Comprueba.** Consulta `!demora`, cancela y espera más de dos segundos: no debe aparecer una respuesta tardía ni error de conexión. Consulta después un texto normal: debe funcionar. La cancelación del cliente no garantiza recuperar una cuota ya consumida por un proveedor remoto.

??? example "Adjuntar y retirar con una decisión visible"

    **Observa el código existente.** `prepararImagen` acota, orienta y recodifica la foto. El ViewModel controla el trabajo y su versión; Quitar invalida resultados anteriores. Mientras prepara, no se permite consultar con un adjunto antiguo.

    **Ahora te toca.** Elige una fotografía propia no personal y formula una pregunta sobre un objeto visible. Antes de consultar, explica qué parte del resultado esperas comprobar con el servidor simulado y cuál necesitaría una inferencia real.

    **Comprueba.** Tras preparar, la app indica que hay imagen. Consulta: la frase simulada confirma texto e imagen. Quita la foto y vuelve a consultar: confirma solo texto. Repite sustitución y retirada mientras se prepara una entrada grande; una preparación antigua no debe reaparecer después de Quitar.

??? example "Error, recuperación y registro honesto"

    **Ahora te toca.** Prepara una secuencia con `!error`, una pregunta válida, servidor detenido y otra consulta después de arrancarlo. Registra acción, estado visible y recuperación. No actives el modo real para superar estas comprobaciones.

    **Comprueba.** El fallo muestra un mensaje y deja reintentar; una consulta posterior puede terminar bien. Girar la pantalla durante una consulta no debe crear otra petición por recomposición. La app conserva la indicación de origen simulado o real y recomienda contrastar la respuesta.

    El README explica cómo activar expresamente el adaptador remoto con configuración del servidor. Esa prueba requiere revisión de condiciones, límites y datos permitidos; si no dispones del servicio, conserva el modo simulado y registra que la inferencia remota no se ha ejecutado.

## Solución de referencia

[Solución completa — ZIP](../descargas/proyectos/ia-solucion.zip){ .md-button }

Resuelve los dos TODO y añade pruebas de límites y preparación concurrente. Las pruebas del backend verifican HTTP y el contrato remoto mediante un doble; no se anuncian como generación de un modelo real.
