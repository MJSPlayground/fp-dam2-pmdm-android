# Depuración, comprobaciones y dispositivo real

## Identificar la fase que falla

Una app puede fallar antes de instalarse, al abrir una pantalla o al realizar una operación. Esas situaciones requieren evidencias distintas. Cambiar dependencias, borrar datos y modificar la UI a la vez dificulta saber qué ha resuelto el problema.

| Fase | Evidencia principal | Primera pregunta |
| --- | --- | --- |
| Sincronización | Mensaje de Gradle Sync | ¿El IDE puede cargar scripts y dependencias? |
| Compilación | Error de Build con archivo y línea | ¿La fuente y los recursos cumplen sus contratos? |
| Instalación | Mensaje del dispositivo/ADB | ¿El dispositivo está conectado y admite esa compilación? |
| Arranque o interacción | Excepción en Logcat y pasos | ¿Qué operación concreta provocó el fallo? |
| Comportamiento incorrecto sin cierre | Datos de entrada, estado y resultado | ¿Dónde cambió por primera vez un valor que no debía? |

Empieza por la primera causa relevante del registro, no por la última línea que dice que la tarea falló. En Room puede haber un error de consulta que impide generar una implementación; corregir el código generado no resolvería la causa. En recursos, una etiqueta XML rota puede acabar manifestándose como un `R` sin resolver en Kotlin.

## Leer Logcat

Logcat permite consultar mensajes del sistema y del proceso de la aplicación. En Android Studio, selecciona el dispositivo correcto y filtra el proceso o paquete de Cuaderno. Reproduce la acción una sola vez para identificar el intervalo relevante.

Una excepción contiene tipo, mensaje y pila de llamadas. Las líneas de bibliotecas ayudan a entender el camino, pero busca también la primera referencia a una clase propia. Si hay `Caused by`, revisa la causa interna: puede explicar lo que una excepción externa presenta de forma genérica.

No publiques registros con notas personales, URLs privadas, tokens o identificadores de usuarios. Para depurar una consulta basta normalmente con la acción, el tipo de fallo y el código HTTP cuando existe. La [guía de Logcat](https://developer.android.com/studio/debug/logcat) explica filtros y lectura de mensajes en el IDE.

## Seguir un valor con el depurador

Un **breakpoint** detiene el proceso en una línea para inspeccionar variables y recorrer llamadas. Colócalo en un lugar que corresponda al problema: si un título válido se rechaza, empieza en la validación o al recibir la acción Guardar; si el dato recibido tiene otra forma, inspecciona la conversión o el DTO.

Desde Android Studio ejecuta **Debug** sobre el dispositivo y realiza la acción. Comprueba el valor antes y después de `trim`, el identificador de la nota y el resultado de la regla de validación. Avanzar sobre una línea ejecuta una operación; entrar en ella permite seguir la función llamada. No necesitas entrar en cada función interna de Compose para averiguar si el título es vacío.

En corrutinas, una espera puede suspender y continuar después. Detener todos los hilos durante mucho tiempo también puede afectar tiempos de espera de red; un fallo que solo aparece tras mantener un breakpoint debe interpretarse con ese contexto. La [guía de depuración](https://developer.android.com/studio/debug) explica las herramientas disponibles.

## Comprobar el recorrido, no solo cada botón

Un listado puede dibujarse correctamente y, aun así, la aplicación perder notas al editar o acumular pantallas al volver. Prueba secuencias completas con resultados distinguibles:

1. Crear una nota con título y contenido reconocibles.
2. Volver al listado y abrirla por su identificador.
3. Modificar su contenido y guardar: debe cambiar la misma nota, no aparecer una segunda.
4. Volver, cerrar la app y abrirla: el dato confirmado debe conservarse.
5. Cancelar una eliminación: la nota debe continuar.
6. Confirmar la eliminación: esa nota desaparece y las demás permanecen.

Utiliza dos notas con el mismo título y distinto contenido para comprobar que se opera por identidad. Si solo utilizas títulos únicos, una implementación incorrecta que busque por texto podría pasar inadvertida.

En el formulario comprueba texto vacío, espacios, longitudes límite y un título demasiado largo. Revisa también el teclado: Guardar debe seguir alcanzable. Al fallar la escritura no deben perderse los valores que el usuario ha introducido.

## Probar carga, error y recuperación

La consulta remota necesita algo más que el caso satisfactorio. Un servidor controlado permite devolver lista vacía, responder con demora o producir códigos de error sin depender de que un servicio público falle justo cuando quieres observarlo.

Durante una demora deben verse el progreso y una interfaz que sigue respondiendo. Tras un error debe desaparecer el progreso y existir una acción de reintento. Al corregir la URL o restablecer el servidor, una nueva consulta debe recuperar contenido sin reiniciar toda la app.

Comprueba un HTTP 404 y un 503, un cuerpo de formato incompatible y una dirección inaccesible. Anota qué tipo de fallo reconoce la aplicación. No simules todas las situaciones devolviendo la misma lista vacía: eso ocultaría si se distinguen respuestas válidas y errores.

La disponibilidad de Wi-Fi tampoco es una garantía de conexión con el servidor. Repite la prueba con el servicio detenido para comprobar cómo termina el cliente. No desconectes ni alteres una red del centro compartida para provocar un fallo si puedes detener tu propio servicio de laboratorio.

## Instalar en un dispositivo Android real

Activa las opciones de desarrollador y la depuración USB del teléfono siguiendo la [guía oficial de ejecución en dispositivo](https://developer.android.com/studio/run/device). En un dispositivo prestado, solicita al propietario la configuración necesaria y respeta su uso. Conecta un cable de datos, desbloquea el teléfono y acepta la autorización del ordenador cuando aparezca.

Selecciona el teléfono en Android Studio y pulsa Run. La compilación de depuración debe instalarse y abrirse en el dispositivo, no únicamente compilar en el ordenador. En Windows puede ser necesario el controlador USB correspondiente. Si no aparece el aviso de autorización, distingue un cable que solo carga de un problema de controlador.

Comprueba en el teléfono el formulario, teclado, navegación, persistencia y Sharesheet. Después realiza la comunicación Wi-Fi con otro ordenador descrita en [Intents e intercambio](intents.md). Son pruebas diferentes: una instalación USB demuestra despliegue local, pero no una consulta inalámbrica al equipo del aula.

Al terminar, evita dejar un ordenador ajeno autorizado permanentemente. No desinstales una aplicación que contenga datos importantes de otra persona para que quepa el ejemplo. Las notas del laboratorio deben poder reconocerse y su eliminación debe ser una acción consciente.

## Pruebas automáticas esenciales

Las pruebas complementan la revisión manual. Una **prueba unitaria** comprueba lógica aislada, como límites de validación o transformación de publicaciones, sin necesitar dibujar la app. Debe utilizar casos que distingan una solución correcta de una que casi siempre devuelve el mismo resultado.

Una **prueba instrumentada** se ejecuta en un dispositivo o emulador. Una prueba de interfaz Compose puede localizar un campo, escribir, pulsar y comprobar un texto. Resulta útil para un flujo como crear y editar una nota, pero no certifica por sí sola contraste, legibilidad, precisión de hardware ni todo el comportamiento de otro dispositivo.

`assembleDebug` genera la compilación de depuración; `lintDebug` realiza análisis estático; las tareas de pruebas ejecutan los tests que corresponda. Superar una de ellas no implica haber superado las demás. Tampoco un informe con cero pruebas ejecutadas demuestra que el comportamiento esté comprobado.

No es necesario introducir una infraestructura compleja de pruebas para estudiar este tema. La ampliación de calidad desarrolla dobles, pruebas de ViewModel y pruebas UI más profundas. Aquí debes comprender qué evidencia aporta cada comprobación y leer su resultado real.

## Documentar una aplicación de forma útil

El README del proyecto debe permitir a otra persona abrirlo sin adivinar pasos esenciales. Incluye qué hace la app, requisitos de SDK y versiones, cómo abrirla en Android Studio, el punto de entrada, dependencias de servicios y configuración del laboratorio cuando se utilice.

Separa **resultado esperado** de **pruebas realizadas**. Un registro breve puede indicar fecha, variante, dispositivo/API, pasos, resultado y límites. Por ejemplo, «CRUD verificado en emulador API 36; consulta a otro ordenador por Wi-Fi pendiente por falta de teléfono» comunica más que «todo probado».

No registres como correcta una función porque compila o porque el código se parece al ejemplo. Si una comprobación necesita dispositivo físico, servicio externo o una cuenta que no has utilizado, deja ese punto pendiente. La documentación debe permitir reproducir y continuar el trabajo sin convertir expectativas en resultados inventados.
