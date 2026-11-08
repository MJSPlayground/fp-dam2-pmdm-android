# Repaso de Kotlin y desarrollo Android

Utiliza tu Cuaderno y las páginas de teoría para razonar los casos. Los ejercicios de código no requieren bibliotecas adicionales a las del tema.

## Interpretar Kotlin

1. Explica el resultado de este fragmento para `null`, `""` y `"Ana"`. ¿Qué comprueba y qué no comprueba el operador Elvis?

    ```kotlin
    fun saludo(nombre: String?): String = "Hola, ${nombre ?: "visitante"}"
    ```

2. Una lista contiene `Tarea("Leer")` y `Tarea("Enviar", true)`. Compara `tareas.map { it.terminada }` con `tareas.filter { !it.terminada }`. Indica el tipo y el contenido de cada salida.
3. ¿Puede cambiar el contenido de una colección mutable declarada con `val`? Distingue reasignar la referencia de modificar el objeto.
4. Una función `suspend` tarda dos segundos en recibir datos. ¿Significa que crea un hilo nuevo o que cualquier cálculo dentro se ejecuta automáticamente en un hilo de trabajo?

## Estado e interfaz

5. Este contador no actualiza correctamente la pantalla. Localiza el problema y explica qué relación falta con Compose:

    ```kotlin
    @Composable
    fun Contador() {
        var pulsaciones = 0
        Button(onClick = { pulsaciones++ }) {
            Text("Pulsaciones: $pulsaciones")
        }
    }
    ```

6. El texto de un formulario usa `remember`, la lista usa Room y una preferencia aún no se almacena. Describe qué puede perderse al recrear la actividad y qué debe seguir ahí al cerrar el proceso y abrir la app.
7. Un composable recibe `texto` y `onTextoChange`. ¿Por qué ese diseño facilita reutilizarlo y comprobarlo? ¿Quién decide cuál es el valor actual?
8. Un `Modifier` aplica fondo y relleno en distinto orden. Explica por qué las dos versiones pueden ocupar el mismo espacio exterior y colorear áreas diferentes.
9. En una lista, una fila utiliza estado local recordado y su posición cambia después de eliminar otra nota. ¿Qué problema evitan las claves estables? ¿Por qué el índice no es siempre una buena clave?

## Navegación y responsabilidades

10. ¿Por qué pasamos `Detalle(id: Long)` en lugar de una nota completa? ¿Qué debe mostrar la pantalla si ese identificador ya no existe?
11. Tras guardar una nota nueva, el usuario pulsa Atrás y vuelve a encontrar el formulario enviado. Describe una política de pila que evite repetir involuntariamente el alta.
12. Dibuja el recorrido desde pulsar Guardar hasta ver la nota en la lista: componente de pantalla, ViewModel, repositorio, DAO, Room y Flow. Indica dónde validas y dónde se guarda realmente.
13. Una función composable llama directamente al DAO cada vez que se recompone. Explica los riesgos y propone dónde iniciar la operación.
14. ¿Por qué `collectAsStateWithLifecycle` es preferible a iniciar una recolección sin considerar si la pantalla está activa? ¿Conservará por sí sola los datos entre reinicios del proceso?

## Red y persistencia

15. Distingue tres fallos: falta el permiso de Internet, el servidor responde 404 y el servidor no es accesible. ¿Dónde buscarías información para cada caso? ¿Qué mensaje accionable mostrarías al usuario?
16. El servidor devuelve 200 con JSON incompatible con el DTO. ¿Debe la app mostrar una lista vacía como si la consulta hubiera tenido éxito? Justifica tu respuesta.
17. Importas una publicación y la editas localmente. ¿Ha cambiado también en el servidor? ¿En qué parte del código buscarías una operación de subida si existiera?
18. El DAO devuelve `Flow<List<Nota>>`. Explica qué ocurre en la pantalla después de un INSERT y por qué no necesitas refrescar manualmente una copia independiente de la lista.
19. Un UPDATE afecta a cero filas. Propón una causa distinta de «Room no funciona» y una forma de comprobarla.
20. ¿Por qué una base de datos necesita una migración cuando cambia su esquema? ¿Es aceptable borrar automáticamente las notas para ocultar ese problema?

## Intents y comprobaciones

21. Distingue un Intent explícito y uno implícito. ¿Por qué compartir texto mediante Sharesheet no exige elegir de antemano una aplicación concreta?
22. Un emulador obtiene notas desde `10.0.2.2`. ¿Demuestra comunicación Wi-Fi con otro equipo? Describe una evidencia suficiente y qué debes registrar si no tienes teléfono.
23. La app abre el formulario y guarda bien, pero el teclado oculta el botón en un teléfono pequeño. ¿Se puede considerar terminada porque pasa la compilación? ¿Qué prueba visual falta?
24. Una prueba manual encuentra que el texto se pierde al girar durante la edición. Indica pasos de reproducción precisos y qué archivos revisarías antes de cambiar la base de datos.

## Programación breve

Resuelve las cuatro funciones de [Kotlin desde Java](practica-kotlin.md) y ejecuta sus pruebas. Después escribe una función que reciba una lista de títulos y una consulta, recorte la consulta y devuelva los títulos que la contienen sin distinguir mayúsculas. Una consulta vacía debe conservar todos y el orden original. No cambies la entrada.

El archivo `ejercicios/FiltroTitulos.kt` del proyecto inicial contiene el TODO. Comprueba al menos `listOf("Leer Kotlin", "Comprar pan", "leer Android")` con `" LEER "`, cadena vacía y `"red"`. Retira el `@Ignore` de `FiltroTitulosTest` para ejecutar sus comprobaciones. Explica por qué `map` no es la operación apropiada para excluir elementos.

## Comprobación integrada

En Cuaderno crea dos notas, edita una, elimina otra con cancelación previa, importa un elemento remoto y comparte su texto. Reinicia la app y verifica la persistencia. Repite la carga con servidor apagado y recupera la lista con Reintentar. Registra dispositivo/API, acciones y resultados. Las condiciones concretas para la prueba Wi-Fi y el teléfono real están en [comprobaciones](comprobaciones.md).

[Consultar soluciones del repaso](soluciones-repaso.md).
