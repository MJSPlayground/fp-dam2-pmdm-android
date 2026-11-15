# Elegir dónde se ejecuta el modelo

## Un modelo generativo no es una consulta de base de datos

Una API REST puede devolver un registro concreto; un modelo generativo produce una respuesta a partir de instrucciones y contexto. El resultado puede variar, omitir información o incluir afirmaciones falsas. Mostrarlo con una interfaz cuidada no convierte su contenido en un hecho comprobado.

El contexto puede ser texto, imagen u otras modalidades. Un modelo multimodal puede relacionar una pregunta con una fotografía; eso no garantiza que identifique todos sus detalles ni que pueda medir dimensiones reales sin una referencia. Antes de añadirlo, define qué ayuda ofrece, cómo revisará la persona su salida y qué decisiones no debe tomar automáticamente.

## Nube y ejecución local

| Decisión | Modelo remoto | Modelo en el dispositivo |
| --- | --- | --- |
| Ejecución | En infraestructura del proveedor | En hardware compatible del usuario |
| Red | Normalmente necesaria | Puede trabajar sin enviar la entrada a la nube tras preparar recursos |
| Recursos | Cuotas, latencia y coste del servicio | Memoria, almacenamiento, batería y capacidad del equipo |
| Datos | Se transmiten según contrato y política del servicio | Pueden permanecer locales si el flujo completo está diseñado así |
| Disponibilidad | Depende del servicio, región y cuenta | Depende del modelo, dispositivo, versión y recursos descargados |

Privacidad no se deduce solo de la palabra «local». Una app puede ejecutar un modelo local y aun así enviar telemetría con la entrada. Hay que revisar todo el recorrido. Tampoco es razonable prometer un modelo grande en todos los teléfonos del mínimo API26.

## AICore y Gemini Nano

Android dispone de capacidades de IA en el dispositivo mediante AICore y las APIs GenAI de ML Kit en dispositivos compatibles. AICore administra recursos del modelo y su ejecución; no es una biblioteca que haga compatibles automáticamente todos los emuladores o teléfonos antiguos.

La [guía de Gemini Nano](https://developer.android.com/ai/gemini-nano) presenta las capacidades disponibles y enlaza sus APIs. Antes de diseñar una función hay que consultar la lista de dispositivos y tareas soportadas, comprobar disponibilidad y distinguir recurso descargable, preparación en curso y capacidad no disponible. La app debe ofrecer un recorrido útil cuando no pueda ejecutar esa función.

Una app puede necesitar primero preparar o descargar recursos y después solicitar generación. No anuncies «sin conexión» antes de comprobar ambos estados. Una respuesta de un doble de pruebas demuestra el flujo de UI, no que Nano haya ejecutado una inferencia en ese dispositivo.

El laboratorio de este tema implementa un cliente de servidor. No contiene una falsa implementación Nano que devuelva una cadena fija. La selección de una API local concreta exige comprobar el hardware disponible y sus condiciones; se puede comparar con el mismo comportamiento de carga, error y revisión humana ya aprendido.

## Coste y límites de un modelo remoto

Los proveedores pueden cobrar por entrada, salida, imagen, modalidad o recursos asociados. Una pregunta breve con una foto puede consumir más recursos que otra de texto; reintentar continuamente multiplica solicitudes. No fijes precios en el código ni presupongas una cuota gratuita permanente.

El backend del laboratorio limita entrada, concurrencia y salida. En un producto real hacen falta además cuotas por identidad, autenticación, métricas sin contenido personal y un procedimiento ante abuso o indisponibilidad. El cliente debe permitir cancelar la espera y reintentar deliberadamente; no lanzar peticiones cada vez que Compose recompone.

Los modelos cambian de versión y pueden retirarse. Por eso el adaptador remoto recibe el identificador en una variable de servidor y exige seleccionarlo expresamente. Verifica sus modalidades, estado y contrato antes de activarlo; el ejemplo no presume que un nombre antiguo siga disponible.
