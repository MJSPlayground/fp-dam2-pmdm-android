# Imágenes, caché y estados de carga

## Cargar sin bloquear el dibujo

Decodificar una imagen grande o descargarla puede tardar. Hacerlo directamente en el cuerpo de un composable repite trabajo y puede bloquear la interfaz. Coil ofrece un cargador de imágenes y un componente `AsyncImage` que coordina petición, tamaño y resultado.

El proyecto fija **Coil 3.3.0**, publicada en su [registro oficial](https://coil-kt.github.io/coil/changelog/#330---july-22-2025), conservando Kotlin y la BOM del curso. No afirma que sea la última versión. Necesita `coil-compose`; para direcciones HTTP añade además `coil-network-okhttp` y permiso `INTERNET`. Coil 3 no incorpora automáticamente todas las redes posibles por incluir el composable. Consulta [inicio y artefactos](https://coil-kt.github.io/coil/getting_started/).

La aplicación utiliza recursos `R.drawable` y una URL propia. Un fragmento de imagen dentro de una tarjeta sería:

```kotlin
AsyncImage(
    model = lamina.imagen,
    contentDescription = null,
    contentScale = ContentScale.Crop,
    modifier = Modifier.fillMaxWidth().height(150.dp)
)
```

`model` identifica lo que se solicita; `Crop` llena el espacio conservando proporción y recortando bordes. `Fit` conserva toda la imagen, pero puede dejar bandas. La altura da una medida al contenedor antes de recibir datos y evita que cada descarga desplace el contenido de forma imprevisible.

`contentDescription = null` es una decisión: las ilustraciones son decorativas y el título identifica la tarjeta. Si una imagen contiene información indispensable que el título no expresa —un gráfico o el estado de una avería— necesita una descripción equivalente. Repetir el mismo título en imagen y texto puede hacer que TalkBack lo anuncie dos veces.

## Carga, éxito y error son estados distintos

En `Foto`, el estado local se recuerda por `modelo`. Al cambiar la imagen solicitada se reinician sus indicadores. `onSuccess` desactiva carga y error; `onError` termina la carga y activa un mensaje. Si el servidor no responde o el recurso no existe, una zona vacía indefinida no informa al usuario de lo ocurrido.

Los callbacks notifican el resultado de esa petición de imagen; no deben guardar una nota ni iniciar otra navegación. El catálogo y sus títulos siguen disponibles aunque una imagen falle. Esta separación permite que la pantalla sea utilizable con red limitada y sin depender de que una miniatura llegue.

La captura remota procede de este mismo repositorio público. No uses fotografías del alumnado ni URL con tokens. Los tres dibujos locales originales permiten verificar el funcionamiento básico sin red. Descargar imágenes de un servicio no concede automáticamente permiso para redistribuirlas.

## Caché con un propietario estable

Un `ImageLoader` administra peticiones y cachés. Reutilizar el cargador compartido evita crear otro por tarjeta o recomposición. Las cachés de memoria y disco pueden ahorrar decodificación y red, pero el resultado depende de la fuente, la configuración y las cabeceras del servidor; «ya la he visto una vez» no garantiza disponibilidad offline permanente.

La [guía de cargadores](https://coil-kt.github.io/coil/image_loaders/) diferencia configuración y ciclo de vida. No incluyas credenciales dentro de una clave de caché pública ni muestres a otra cuenta una imagen privada por reutilizar incorrectamente su identidad. En este laboratorio todo el contenido remoto es público y no hay autenticación.

Cambiar una lista o pulsar Actualizar no invalida por sí solo la caché de Coil. Si el archivo cambia manteniendo la misma URL, el producto debe decidir su política de actualización. El refresco simulado solo incrementa una revisión visible; no promete una nueva descarga de todos los archivos.

## Pull-to-refresh y una alternativa accesible

`PullToRefreshBox` recibe `isRefreshing` y `onRefresh`; no realiza la consulta por sí mismo. Envuelve contenido desplazable y deja que tu estado determine cuándo termina el indicador. Su [guía oficial](https://developer.android.com/develop/ui/compose/components/pull-to-refresh) documenta el gesto y el indicador.

El ejemplo evita iniciar dos refrescos simultáneos y utiliza `finally` para salir de carga. Además del gesto, ofrece el botón **Actualizar**: así no exige que todo el mundo pueda arrastrar el contenido. La operación espera 800 ms e incrementa un contador; es una simulación explícita, útil para probar indicadores con resultados repetibles.

El esqueleto de carga ocupa un bloque acotado y pulsa su opacidad. No reemplaza datos correctos por un parpadeo continuo. Una animación de espera informa de actividad; si conoces un progreso real, utiliza un indicador determinado y no inventes porcentajes. Cuando el movimiento se reduce o desactiva en el sistema, la función debe seguir siendo comprensible por su texto e indicador.
