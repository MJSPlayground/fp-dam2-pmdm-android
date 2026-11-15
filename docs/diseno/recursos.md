# Recursos, idiomas y accesibilidad

## Iconos que pertenecen a la aplicación

Los recursos se referencian por ID y Android selecciona la variante apropiada. Un vector describe formas con un viewport y escala sin almacenar una imagen distinta para cada densidad. En Android Studio, **New → Vector Asset** permite importar un SVG compatible o utilizar los recursos de iconos disponibles, revisando siempre su licencia. No todos los efectos de SVG se convierten directamente a `VectorDrawable`.

El laboratorio incluye tres ilustraciones vectoriales originales. Cada una tiene un tamaño preferido en dp y coordenadas del dibujo en su viewport. Se pueden usar desde `painterResource` para carga local simple o como modelo entero de Coil para compartir el mismo componente que carga una URL.

Un icono adaptativo se define por capas. En `mipmap-anydpi-v26/ic_launcher.xml`, el fondo blanco y el vector frontal permiten que el lanzador aplique su máscara. No dibujes una máscara circular dentro del archivo esperando que todos los lanzadores la respeten igual. Conserva la zona segura, prueba distintas formas y crea una capa monocroma si necesitas iconos temáticos en las versiones que la soportan. La [guía de iconos adaptativos](https://developer.android.com/develop/ui/views/launch/icon_design_adaptive) explica sus límites.

## Tipografía sin copiar archivos sin permiso

`MaterialTheme.typography` organiza estilos semánticos como títulos, cuerpo y etiquetas. Reutilizarlos conserva jerarquía y facilita escalar el texto. La galería utiliza `FontFamily.Serif` del sistema en la barra para mostrar una variación sin distribuir fuentes comerciales.

Si dispones de una fuente TTF u OTF con permiso de redistribución, colócala en `res/font` con nombre válido, por ejemplo `titulos_regular.ttf`. Conserva su licencia. Entonces puedes construir una `FontFamily(Font(R.font.titulos_regular))` y asignarla a estilos de `Typography`. Esa referencia **solo existe después de añadir el archivo**; no aparece como recurso ficticio en el laboratorio. La [documentación de fuentes](https://developer.android.com/develop/ui/compose/text/fonts) incluye pesos, estilos y descarga.

No escales a mano los `sp` para contrarrestar la preferencia de tamaño del sistema. Un diseño que solo cabe reduciendo el texto no ha resuelto la adaptación.

## Traducir recursos y formar mensajes

Los recursos de `values` constituyen el conjunto de reserva. `values-en` aporta textos ingleses; opcionalmente `values-es` puede especializar los españoles si la base está en otro idioma. No hacen falta tres copias idénticas: en este laboratorio la base ya está en español y la alternativa inglesa contiene los textos de la galería.

`stringResource(R.string.revision, revision)` sustituye `%1$d` por el número y Android selecciona la cadena del idioma correspondiente. Evita concatenar fragmentos como «Actualizaciones: » y el dato por separado: el orden puede cambiar en otra lengua. Para cantidades que cambian de singular a plural utiliza `<plurals>` y `pluralStringResource`; para fechas, dinero y números de negocio, utiliza los formateadores regionales, no una cadena con separadores fijados.

El ejemplo ilustrativo siguiente formatea un importe en la región elegida; `locale` y `importe` ya deben existir:

```kotlin
val moneda = NumberFormat.getCurrencyInstance(locale)
val presentado = moneda.format(importe)
```

El formato no decide qué moneda representa el dato: si el producto maneja EUR, configura también la moneda explícitamente. Cambiar a una región no debe convertir un precio sin aplicar un tipo de cambio. La [guía de localización](https://developer.android.com/guide/topics/resources/localization) separa recursos, datos y comportamiento.

Prueba inglés desde el idioma del sistema o los ajustes de idioma por aplicación cuando estén configurados. El proyecto utiliza la elección del sistema. Los títulos del catálogo también son recursos, de modo que no queda una lista española dentro de una interfaz inglesa. Verifica textos largos y no presupongas que una traducción ocupa el mismo ancho.

## Semántica que exprese la acción

TalkBack no interpreta colores como una persona que ve la pantalla. Los componentes Material aportan roles y acciones habituales; `semantics` permite completar el significado cuando hace falta. El botón de favorito debe comunicar qué lámina modifica y si está guardada. `stateDescription` expresa estado; `contentDescription` puede identificar una acción cuando la etiqueta visible no basta.

Una imagen decorativa puede tener descripción nula porque el título ya ofrece su información. Un botón solo con icono necesita un nombre. No escribas «botón» dentro de la descripción para repetir el rol que el componente ya anuncia. La [guía de semántica](https://developer.android.com/develop/ui/compose/accessibility/semantics) incluye combinación de nodos y propiedades.

Mantén objetivos táctiles de al menos 48 dp. No coloques dos controles pequeños tan cerca que sus zonas expandidas se solapen. Ordena la navegación de forma coherente con la lectura, conserva foco al volver del detalle y proporciona un botón alternativo al gesto de refresco. Las pruebas de semántica pueden detectar etiquetas y estado, pero no sustituyen escuchar el recorrido con TalkBack.

## Contraste y movimiento

En texto normal, usa como referencia una relación de contraste de al menos 4,5:1; el texto grande admite 3:1 bajo las condiciones del criterio. Controles y gráficos necesarios también necesitan contraste suficiente. El color no debe ser el único identificador del estado: Favoritos tiene nombre y marca de selección. Los criterios se documentan en [contraste WCAG](https://www.w3.org/WAI/WCAG22/Understanding/contrast-minimum.html).

La paleta usa parejas de Material (`primary/onPrimary`, `surface/onSurface`) en claro y oscuro. Comprueba el color final compuesto: una transparencia puede reducir contraste aunque los colores base fueran adecuados. Las actividades incluyen calcularlo para colores opacos; no certifiques toda una pantalla por comprobar una pareja.

Prueba fuente al 200 %, modo oscuro, navegación por teclado, TalkBack y animaciones desactivadas. Los resultados pendientes deben seguir identificados como pendientes, aunque lint no marque errores.
