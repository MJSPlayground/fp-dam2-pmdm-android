# Animaciones asociadas al estado

## Mostrar un cambio, no simular una operación

Una animación puede ayudar a reconocer que la reproducción está activa, que aparece un panel o que ha terminado una operación. Su entrada debe ser un estado real. Si animamos una etiqueta al pulsar Reproducir aunque el medio haya fallado, la interfaz comunica un éxito que no existe.

Ya conocemos el flujo de eventos: el reproductor notifica `onIsPlayingChanged`; el controlador actualiza un valor observable; Compose representa ese valor. La animación se sitúa al final de ese recorrido, como una forma de mostrarlo.

```text
Acción del usuario → Player → evento isPlaying
                                  ↓
                         Estado de interfaz
                                  ↓
                        Visibilidad animada
```

No necesitamos un temporizador que cambie un booleano por su cuenta para aparentar actividad. Tampoco un bucle que actualice el estado desde el cuerpo de una función componible: ese trabajo no describe la pantalla y puede repetirse sin control.

## AnimatedVisibility

`AnimatedVisibility` añade o retira un contenido mediante una transición de entrada o salida. Recibe un booleano `visible`; cuando cambia, Compose realiza la transición correspondiente. Durante la salida el contenido permanece el tiempo necesario para completar su animación y después abandona la composición.

Este componente completo recibe el estado y un mensaje ya obtenido de los recursos por su contenedor. No crea otro reproductor ni posee un booleano independiente:

```kotlin title="Componente ilustrativo de un indicador"
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun IndicadorReproduccion(reproduciendo: Boolean, mensaje: String) {
    AnimatedVisibility(visible = reproduciendo) {
        Text(text = mensaje)
    }
}
```

Cuando `reproduciendo` pasa a verdadero, aparece el mensaje; cuando vuelve a falso, sale. El contenedor puede utilizar la cadena «Reproduciendo» del recurso del proyecto. La animación no debe borrar un mensaje de error que la persona aún necesita leer.

La [guía de animaciones de composición](https://developer.android.com/develop/ui/compose/animation/composables-modifiers) explica visibilidad, tamaño y propiedades. Empezamos con una transición pequeña ligada a un valor que ya existe en la app.

## Elegir una transición proporcionada

Las transiciones pueden combinar desvanecimiento y cambio de tamaño. Por ejemplo, `fadeIn()` y `fadeOut()` hacen que el cambio sea gradual sin desplazar mucho contenido. Este fragmento modifica la entrada y salida del componente anterior:

```kotlin title="Fragmento de una transición de visibilidad"
AnimatedVisibility(
    visible = reproduciendo,
    enter = fadeIn(),
    exit = fadeOut()
) {
    Text(text = mensaje)
}
```

`fadeIn` y `fadeOut` pertenecen a `androidx.compose.animation`. El valor que controla `visible` sigue siendo el mismo. Si añadimos una expansión, debemos comprobar cómo desplaza los controles cercanos: una acción importante no debería moverse inesperadamente justo cuando se intenta pulsarla.

No es necesario combinar varias transformaciones para demostrar una animación. Una transición discreta permite observar su relación con el evento y limita el ruido visual. El comportamiento funcional debe entenderse también si la duración de animaciones del sistema se reduce o se desactiva.

## Propiedades animadas

Para cambiar una propiedad concreta, Compose ofrece funciones como `animateFloatAsState`. Reciben un valor objetivo y producen valores intermedios. Su finalidad no es medir cuánto ha avanzado una operación real.

Por ejemplo, podríamos variar una opacidad entre dos estados. El siguiente fragmento ilustra el cálculo; `alpha` se utiliza después en `Modifier.alpha(alpha)` sobre el componente que se quiera destacar:

```kotlin title="Fragmento de una propiedad visual"
val alpha by animateFloatAsState(
    targetValue = if (reproduciendo) 1f else 0.65f,
    label = "Opacidad del indicador"
)
```

Requiere `androidx.compose.animation.core.animateFloatAsState`, `androidx.compose.runtime.getValue` y, al aplicarlo, `androidx.compose.ui.draw.alpha`. La etiqueta ayuda a identificar la animación en herramientas de inspección. Los valores representan opacidad; no son un porcentaje de reproducción ni un indicador de búfer.

El laboratorio utiliza la animación de visibilidad para mantener un caso pequeño y verificable. Las transiciones entre pantallas, efectos complejos y optimización se desarrollarán en sus ampliaciones.

## Estado observable y accesibilidad

Un indicador animado debe tener un equivalente comprensible en texto o semántica. No uses únicamente una luz verde intermitente para comunicar que hay audio, ni una oscilación como único aviso de error. Las personas que no ven el movimiento o lo reducen deben poder conocer el estado.

Mantén textos como reproducción, pausa y error asociados a la fuente real. Evita parpadeos y movimientos continuos sin necesidad; un control que cambia de posición o tamaño puede dificultar la interacción y provocar molestias.

En el código del reproductor, comprueba que detener y fallar dejan `isPlaying` en un estado que retira el indicador. Preparar la fuente no debe mostrarlo como reproduciendo si todavía no avanza. También hay que probar volver de Photo Picker y abandonar la Activity, no solo alternar un botón con datos simulados.

## Cómo comprobar una animación

Una captura estática demuestra una apariencia en un instante. Para comprobar la transición hay que ejecutar el cambio de estado y observar entrada, salida y comportamiento de los controles. Registra qué acción se realizó y qué evento del reproductor se utilizó.

En PDF, la función de la animación se explica mediante su estado inicial y final y el recorrido descrito en texto. Una grabación puede complementar la demostración, pero no debe ser la única forma de entender cuándo aparece el indicador ni sustituir la comprobación del evento que lo gobierna.
