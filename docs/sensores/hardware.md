# Biometría y funciones del dispositivo

## Confirmar presencia local

La biometría del sistema permite pedir una reautorización local. La app no recibe una fotografía de la cara ni una plantilla de huella: obtiene un resultado de autenticación. No equivale a crear una cuenta en un servidor ni identifica por sí sola al usuario remoto.

El laboratorio fija `androidx.biometric:biometric:1.1.0`, versión estable de la biblioteca. `BiometricPrompt` necesita un propietario compatible: aquí `FragmentActivity`, que también permite alojar Compose con `setContent`. No confundas esta biblioteca con la clase homónima de plataforma. La [guía oficial](https://developer.android.com/identity/sign-in/biometric-auth) y las [versiones publicadas](https://developer.android.com/jetpack/androidx/releases/biometric) explican las alternativas y combinaciones admitidas.

## Disponibilidad antes del diálogo

`BiometricManager.canAuthenticate` recibe el nivel de autenticador requerido. El laboratorio utiliza `BIOMETRIC_STRONG` y permite cancelar mediante un botón negativo. Si el hardware no existe, no está disponible o no tiene una credencial biométrica configurada, la pantalla debe explicarlo sin intentar autenticar como si ya estuviera listo.

```kotlin
val permitido = BiometricManager.Authenticators.BIOMETRIC_STRONG
val resultado = BiometricManager.from(context).canAuthenticate(permitido)
```

El fragmento comprueba capacidades; no confirma identidad. Para ello se crea `BiometricPrompt` con executor principal y callback, se prepara `PromptInfo` y se llama a `authenticate`. Los imports y el recorrido completo están en la Activity del proyecto. La combinación con credencial del dispositivo tiene reglas según versión; no basta con añadir un botón negativo junto a cualquier máscara de autenticadores.

En el callback distinguimos tres respuestas: éxito permite la demostración; no coincidencia informa y permite otro intento; error incluye cancelación o bloqueo y finaliza esa operación. Android administra el diálogo, los intentos y las políticas del autenticador. No construyas tu propio formulario para recoger huellas.

## Una demostración visual no cifra archivos

La app muestra un texto tras autenticar y lo oculta al abandonar la pantalla. Solo ilustra el recorrido de reautorización. Una variable booleana no protege una base de datos ni un archivo extraído del dispositivo. Para proteger operaciones criptográficas hay que relacionar el prompt con claves de Android Keystore y un `CryptoObject`, definir cuándo se requiere reautenticación y qué ocurre al cambiar las credenciales.

Tampoco se debe guardar una autorización indefinida en preferencias para saltarse futuras comprobaciones. En el laboratorio no hay secretos ni datos privados; usa esa misma separación al explicar qué demuestra la prueba. El emulador puede simular una huella configurada, pero no verifica un lector físico ni la resistencia del autenticador.

## Háptica y vibración

La respuesta háptica acompaña una interacción, por ejemplo una pulsación prolongada. `View.performHapticFeedback` expresa esa intención y respeta la configuración del sistema; no requiere permiso VIBRATE para esa llamada. Compose permite acceder a la vista mediante `LocalView` o utilizar sus APIs de feedback según el efecto disponible.

Una vibración temporal explícita utiliza un `Vibrator` y `VibrationEffect`. En API26 o superior, `createOneShot` recibe duración en milisegundos y amplitud; `DEFAULT_AMPLITUDE` deja la intensidad apropiada al dispositivo. La aplicación declara el permiso normal `VIBRATE`, sin diálogo contextual. Comprueba `hasVibrator` antes de anunciar capacidad.

```kotlin
vibrador.vibrate(VibrationEffect.createOneShot(
    80, VibrationEffect.DEFAULT_AMPLITUDE
))
```

El fragmento supone un vibrador disponible y permiso declarado. Solicitar la operación no demuestra que la persona la haya percibido; influyen hardware y ajustes. No uses vibración como única señal de éxito o error. La [referencia oficial de háptica](https://developer.android.com/develop/ui/views/haptics/haptics-apis) ayuda a elegir entre intención de interfaz y control del actuador.

## Linterna, cámara y permisos

`CameraManager` permite consultar cámaras y comprobar `FLASH_INFO_AVAILABLE`. Un dispositivo con cámara puede carecer de flash; una cámara en uso puede impedir controlar su linterna. El laboratorio busca una con flash, pide CAMERA al pulsar la acción y trata denegación y excepciones. Declarar la característica con `required=false` evita excluir dispositivos sin ella.

`setTorchMode(id, encendida)` solicita el cambio. La [referencia CameraManager](https://developer.android.com/reference/android/hardware/camera2/CameraManager) describe disponibilidad, errores y callbacks. La app apaga la linterna al perder la pantalla. No confíes solo en un botón de apagar: la persona puede ir a Inicio o abrir otra app.

Esto es distinto de delegar una captura a una cámara externa con `TakePicture`, que en el laboratorio de imágenes no declara CAMERA. Un permiso se justifica por la API que usa tu aplicación, no por que aparezca la palabra cámara en un enunciado.

## Consumo y privacidad

Escucha solo mientras exista una necesidad visible. No mantengas sensores activos para una pantalla abandonada ni publiques lecturas o datos biométricos en registros. Evita frecuencias altas por defecto y no transformes una demostración local en seguimiento de fondo. Permisos, capacidad y resultado son tres comprobaciones distintas; ninguna sustituye a las otras.
