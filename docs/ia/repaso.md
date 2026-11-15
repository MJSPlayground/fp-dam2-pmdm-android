# Consolidación y repaso

## Una consulta que puede revisarse

Una asociación necesita preparar descripciones de imágenes propias antes de revisarlas manualmente. Quiere escribir una pregunta, adjuntar o quitar una foto y obtener una respuesta con indicación de origen. Debe distinguir espera, cancelación, fallo y resultado; la persona debe poder corregir y reintentar sin reiniciar la app. No se enviará nada por el hecho de abrir la pantalla o elegir una imagen.

Parte del proyecto inicial y completa ese comportamiento. Comprueba límites, retirada de imagen durante preparación, sustitución por otra, cancelación de una respuesta lenta y recuperación tras un error. Usa el servidor simulado para el circuito y registra la inferencia remota como pendiente si no está disponible. No necesitas crear una cuenta ni introducir credenciales en Android.

## Preguntas de interpretación

1. ¿En qué se diferencia una respuesta generada de un registro recuperado de una base de datos?
2. ¿Ejecutar un modelo en el teléfono garantiza que toda la aplicación mantiene los datos locales?
3. ¿Por qué AICore no hace compatible cualquier teléfono API26 con Gemini Nano?
4. ¿Guardar una clave en BuildConfig o aplicar R8 la protege dentro del APK?
5. ¿Qué ventajas aporta un backend y qué controles siguen faltando en el servidor local para un producto expuesto a Internet?
6. ¿Por qué Base64 no cifra la imagen y por qué quitar EXIF no elimina una cara de sus píxeles?
7. Una preparación antigua termina después de elegir otra foto. ¿Qué comportamiento debe evitarse y cómo comprobarlo sin un modelo real?
8. ¿Cancelar una corrutina cancela necesariamente una inferencia recibida por el proveedor o devuelve su coste?
9. ¿Qué demuestra que `simulada` sea true? ¿Permite evaluar exactitud visual?
10. El proveedor no devuelve candidatos o bloquea la respuesta. ¿Sería correcto inventar una salida para que la pantalla nunca quede vacía?
11. ¿Qué registrarías para comparar resultados de un modelo sin divulgar preguntas o fotografías personales?
12. ¿Por qué una tarea de generación no debe ejecutarse directamente en el cuerpo de un composable?

[Solución del proyecto — ZIP](../descargas/proyectos/ia-solucion.zip).

[Respuestas razonadas](soluciones.md).
