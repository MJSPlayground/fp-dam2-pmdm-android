# Soluciones de referencia del repaso

## Elección de tecnología

1. Kotlin nativo facilita la integración de sensores Android y aprovecha los conocimientos del primer equipo. El coste es que una futura versión iOS necesita trabajo adicional. Para la consulta por enlace, una web adaptable evita exigir instalación; sus capacidades dependen del navegador y la conectividad. Otras respuestas son válidas si justifican estas restricciones.
2. El archivo comprimido no representa el tamaño de la imagen decodificada. Con cuatro bytes por píxel, 4000 × 3000 requiere aproximadamente 48 MB solo para los píxeles de una imagen.
3. Probaría la obtención real de ubicación en un espacio adecuado, gestionando permisos y ausencia de señal. El emulador demuestra la respuesta a coordenadas controladas; el teléfono añade proveedor real, precisión y condiciones de recepción. No basta con que aparezca un mapa.

## Estructura y ejecución

4. La extensión `.kt` identifica Kotlin aunque la carpeta se llame `java`. Un texto externalizado se busca en `app/src/main/res/values/strings.xml` y se referencia desde la interfaz.
5. Debe abrir la raíz con `settings.gradle.kts` y esperar a la sincronización. Añadir imports no instala dependencias ni carga la configuración del módulo.
6. Sincronización, compilación e instalación/ejecución, respectivamente. Hay que ejecutar y pulsar el botón; las fases anteriores no comprueban su comportamiento.

## Compatibilidad

7. API 25 queda excluida por el mínimo. API 26 cumple el mínimo y API 37 no queda excluida por `targetSdk`, que no es un máximo. Esto no garantiza el funcionamiento de cada API ni el comportamiento en una versión futura: hay que verificarlo. Una llamada introducida en 33 requiere comprobación de versión o alternativa compatible al ejecutarse en 26.
8. No. Gradle pertenece a la cadena de construcción del ordenador y lo fija el wrapper del proyecto. El sistema Android es el entorno donde se ejecuta la app instalada.
9. Un error puede proceder de sintaxis, dependencias, recursos o configuración. Cambiar el SDK sin leer la causa puede añadir incompatibilidades y no resolver el error original.

## Diagnóstico y cambio

10. Revisaría la instalación del SDK, su ruta en Android Studio y la configuración local del proyecto. No hay motivo para cambiar la pantalla por un fallo de localización del SDK.
11. Comprobaría si el emulador llega a la pantalla de inicio y aparece disponible en el selector; después revisaría si Build termina de generar el APK o falla antes de instalar. Son fases distintas y requieren soluciones distintas.
12. No necesariamente. Una variable de memoria puede perderse al recrear la actividad. Conservar estado durante recreación y persistir datos entre arranques son problemas distintos que se tratarán en el tema de desarrollo.

Para el cambio visual, modifica el valor del recurso de saludo en `strings.xml` y la separación que ya utiliza `Arrangement.spacedBy` en la pantalla. Por ejemplo, una separación de `20.dp` deja más espacio que `16.dp` sin fijar posiciones. El objetivo es comprobar tu resultado en las orientaciones y temas solicitados, no reproducir una frase concreta. La [solución completa de la práctica](practica.md#comparar-con-la-solucion) permite comparar la estructura de la pantalla.
