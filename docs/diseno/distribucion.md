# Listas, cuadrículas y adaptación

## Elegir el recorrido del contenido

Una lista vertical funciona bien cuando cada elemento necesita toda la anchura. Una galería permite comparar miniaturas y puede aprovechar varias columnas. `LazyRow` presenta elementos horizontalmente, por ejemplo categorías; `LazyVerticalGrid` organiza una cuadrícula vertical y compone los elementos que necesita mostrar.

En el laboratorio, las categorías son una fila desplazable y las imágenes una cuadrícula. La fila aparece como un elemento que ocupa todas las columnas, no como otra lista vertical ilimitada dentro del scroll. Consulta [listas y cuadrículas](https://developer.android.com/develop/ui/compose/lists) para sus contratos de medida y composición.

El siguiente fragmento presupone una colección de láminas y una función `Tarjeta`:

```kotlin
LazyVerticalGrid(columns = GridCells.Fixed(2)) {
    item(span = { GridItemSpan(maxLineSpan) }) {
        Text("Categorías")
    }
    items(laminas, key = { it.id }) { lamina ->
        Tarjeta(lamina)
    }
}
```

`GridCells.Fixed(2)` reparte el ancho en dos columnas. `GridCells.Adaptive(minSize = 160.dp)` calcula cuántas caben manteniendo un mínimo. No confundas este mínimo con un ancho máximo: si queda espacio sobrante, las columnas lo reparten. El título del ejemplo se extiende mediante `maxLineSpan` y no consume solamente una celda.

Las claves deben representar identidad estable y ser únicas. Usar la posición hace que al filtrar o reordenar el estado local de una fila pueda asociarse a otra. Una clave por ID conserva mejor la continuidad, pero no guarda por sí sola todo el estado en disco. Evita objetos mutables que cambian silenciosamente mientras Compose conserva su referencia.

## Ancho de ventana, no etiqueta del dispositivo

Una tableta puede tener tu app en una ventana estrecha y un teléfono puede mostrar una ventana horizontal ancha. Adapta al espacio actual, no a nombres de modelos ni a píxeles físicos. Los `dp` expresan tamaño independiente de densidad y `sp` se emplea para el texto escalable.

El proyecto utiliza `material3-window-size-class`, alineado con la BOM del curso, y `calculateWindowSizeClass(activity)`. Su API de tres anchos requiere opt-in en esta combinación:

| Clase de ancho del laboratorio | Rango en dp | Decisión de la galería |
| --- | --- | --- |
| Compact | Menos de 600 | Una columna y barra inferior |
| Medium | Desde 600 hasta antes de 840 | Dos columnas y menú lateral |
| Expanded | Desde 840 | Tres columnas y menú lateral |

Esta clasificación se calcula en la composición raíz y se entrega a la pantalla. Así puedes probar decisiones de distribución sin que cada tarjeta consulte la Activity. Las APIs adaptativas más recientes ofrecen otras capacidades; el ejemplo conserva deliberadamente la combinación fijada y no mezcla dos sistemas equivalentes. La [guía de clases de ventana](https://developer.android.com/develop/ui/compose/layouts/adaptive/use-window-size-classes) explica el modelo actual.

No basta con aumentar columnas. Comprueba altura útil, fuente grande, detalle y teclado. Si la ventana tiene una bisagra que oculta contenido, una clase de ancho no describe por sí sola esa oclusión: una aplicación para plegables debe considerar la postura y la información de la ventana. El laboratorio verifica distribución por ancho; no presenta una simulación de bisagra como prueba de hardware plegable.

## Navegación visible y menú lateral

`NavigationBar` con `NavigationBarItem` permite seleccionar destinos principales en ventanas compactas. Su estado `selected` expresa dónde estás; el evento cambia el destino. El icono puede ser decorativo si la etiqueta ya identifica la acción. Una barra no debe ejecutar una operación destructiva como si fuera un destino.

En ventanas mayores el ejemplo usa `ModalNavigationDrawer`, `ModalDrawerSheet` y `NavigationDrawerItem`. Abrir y cerrar el drawer es suspendido, por lo que un evento utiliza `rememberCoroutineScope`. La [guía de drawer](https://developer.android.com/develop/ui/compose/components/drawer) muestra esta relación entre estado y gesto.

No se crean dos catálogos independientes: barra y drawer cambian el mismo filtro de destino. Solo se muestra la modalidad elegida para ese ancho. Al cambiar tamaño se conservan favoritos y categoría, y el detalle permite volver mediante una acción visible y Atrás del sistema. El producto real podría optar por rail o drawer permanente; aquí el objetivo es practicar dos componentes sin imponer una navegación nueva al itinerario principal.
