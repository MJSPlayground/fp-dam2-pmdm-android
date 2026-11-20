# Imagen, privacidad y verificación

## Una fotografía es parte de la entrada

Una consulta multimodal combina una instrucción con una imagen. «Describe la foto» y «localiza el texto del cartel y explica qué no se puede leer» piden tareas diferentes. El modelo no conoce automáticamente el contexto de la persona ni puede identificar con certeza algo que no aparece de forma suficiente en los píxeles.

El laboratorio utiliza Photo Picker para que la persona elija contenido concreto. En la imagen API 37 del aula, selecciona la miniatura y confirma con **Done**. Elegirlo no inicia una consulta: primero se prepara una copia y después se pulsa Consultar. Esto permite retirar el adjunto antes de enviar. Cancelar el selector conserva la decisión anterior y no debe presentarse como una respuesta de IA.

## Preparar lo necesario y retirar metadatos

La app decodifica con muestreo para limitar el lado de trabajo a 1024 píxeles, corrige la orientación EXIF y dibuja sobre un fondo opaco. Después recodifica JPEG sin trasladar los metadatos originales. El resultado se limita a 512 KiB y se codifica en Base64 para el contrato JSON.

Este proceso reutiliza las ideas del [editor de imágenes](../multimedia/transformaciones.md). Reducir puede eliminar detalles pequeños, por lo que una respuesta que no lee un texto minúsculo no demuestra necesariamente un fallo de transporte. No basta con borrar EXIF para anonimizar una imagen: una cara, un nombre o un documento siguen formando parte de sus píxeles.

Base64 representa bytes como texto; no oculta ni cifra la fotografía. El HTTP local del laboratorio está limitado al host del emulador. Una comunicación real hacia un servicio necesita HTTPS y controles de acceso apropiados. La [guía de entrada de imágenes del proveedor](https://ai.google.dev/gemini-api/docs/image-understanding) describe formatos y modalidades; las condiciones y límites pueden cambiar según modelo.

## Evitar que una preparación antigua vuelva a adjuntarse

La lectura de una fotografía puede tardar más que otra. Si la persona elige una segunda imagen, la primera no debe sobrescribirla al terminar tarde. Si pulsa Quitar, un callback anterior no debe restaurar el archivo eliminado.

El ViewModel conserva un trabajo de preparación y una versión. Al reemplazar o quitar incrementa esa versión, cancela el trabajo previo y descarta resultados que ya no corresponden a la selección vigente. Mientras prepara, Consultar permanece deshabilitado. La pantalla anuncia el estado y permite quitar o sustituir: no envía silenciosamente una fotografía anterior.

No guardamos el JPEG Base64 en un Bundle. El ViewModel puede conservarlo durante una recreación de pantalla, pero no se promete restauración tras la muerte del proceso ni historial permanente. La entrada es temporal y no se registra en el backend del laboratorio.

## Consentimiento, coste y revisión

Antes de enviar una foto a un proveedor, informa de su destino y permite retirarla. Utiliza imágenes propias no personales para las prácticas. Un servidor local configurado para reenviar no convierte una inferencia remota en procesamiento en el dispositivo.

Las preguntas deben delimitar tarea y formato esperado sin exigir que el modelo invente lo que no puede observar. Por ejemplo, pedir una lista de objetos visibles y una indicación de dudas permite revisar la salida; pedir una identificación inequívoca de una persona no es una tarea apropiada para este laboratorio.

La respuesta debe contrastarse. En texto, comprueba una afirmación con una fuente fiable. En imagen, compara cada elemento con los píxeles y señala detalles ausentes. Una respuesta segura en su redacción puede ser errónea. No utilices el número de tokens, la latencia o un tono convincente como medida de exactitud.

## Qué demuestra cada prueba

El modo simulado confirma que llegaron texto y, si corresponde, bytes de imagen. Los tests de contrato verifican campos, límites y tratamiento de errores; los de preparación comprueban sustitución y cancelación. Ninguna de esas pruebas evalúa la comprensión visual de un modelo.

Para evaluar un proveedor real hace falta una muestra permitida, preguntas conocidas, resultados revisados y registro de modelo, configuración y fecha. Esa prueba puede consumir cuota y transmitir datos. Si no se realiza, se documenta como pendiente; no se inventan respuestas de un modelo para completar una captura.
