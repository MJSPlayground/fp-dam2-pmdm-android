# Soluciones de diseño

## Actividades autónomas

[Proyecto solución — ZIP](../descargas/proyectos/diseno-solucion.zip){ .md-button }

Abre la solución en Android Studio y ejecuta **app** para la galería. Las funciones autónomas están en `RetosDiseno.kt`; `GaleriaTest`, dentro de **test**, permite ejecutarlas mediante Run sin dispositivo ni red.

`ordenarSeleccion` conserva la primera aparición de cada ID y ordena por dos criterios: pertenecer a favoritos en primer lugar e ID después. `contraste` transforma los canales sRGB a valores lineales, calcula luminancias relativas y coloca la mayor en el numerador. Comprueba extremos, simetría y una pareja de la paleta. El cálculo recibe colores opacos: no simula un motor de composición con transparencias.

Estas funciones no están en el proyecto inicial y no se necesitan para completar los TODO guiados.

## Repaso

1. Fixed fija el número de columnas; Adaptive calcula cuántas caben a partir de un mínimo. Un `span` de `maxLineSpan` permite que el encabezado atraviese todas las columnas.
2. La clave representa la identidad de un elemento aunque cambie su posición. Si se usa posición, al filtrar o reordenar el estado puede asociarse a otro elemento. Las claves deben ser únicas.
3. Corresponde Compact: una columna y barra inferior. Importa el espacio actual de ventana, no la etiqueta «tableta». La información de bisagra/postura exige otra consideración si afecta al área útil.
4. Coordina el gesto y el indicador con el estado que recibe. La app debe ejecutar la operación, evitar duplicados y terminar carga también ante errores/cancelación. El botón permite iniciar la misma acción sin depender del gesto.
5. La operación solo incrementa una revisión tras una demora local; no invalida las cachés. Coil puede reutilizar memoria o disco y la fuente puede conservar su URL. El producto debe definir qué significa actualizar una imagen y cómo comprobarlo.
6. Si el título ya identifica una ilustración decorativa, repetirlo en la imagen puede duplicar el anuncio. Si el contenido visual aporta información esencial, necesita un equivalente accesible. La decisión depende del significado, no de utilizar Coil.
7. Falta el ID: `foto-ID` identifica el par miniatura/detalle correcto. El ámbito de visibilidad permite relacionar las representaciones entrante y saliente durante la animación.
8. Puede representar mal la rama saliente, porque durante la transición conviven distintos valores de destino. La lambda entrega el estado que corresponde a ese contenido animado.
9. `remember` conserva durante la composición. `rememberSaveable` puede restaurar datos compatibles ante recreaciones y restauración de estado, con límites; no sustituye almacenamiento de negocio en disco ni garantiza persistencia tras cualquier terminación.
10. El mensaje y el orden de sus argumentos pertenecen a recursos traducibles. Separadores, fechas y formato monetario dependen del locale y del contrato del dato. Un cambio de región no equivale a convertir una moneda en otra.
11. El nombre identifica la acción y su objeto, por ejemplo «Guardar favorito: Bosque»; el estado informa «Sin guardar» o «Guardado en favoritos». El rol del botón ya viene del componente; no hace falta repetir la palabra botón.
12. Lint detecta determinados patrones estáticos. Hay que ejecutar anchos, idiomas, temas, tamaño de fuente, foco/teclado y TalkBack; observar refresco, errores y las dos direcciones de la transición. Un test JVM o una captura fija no reemplaza ese recorrido.
