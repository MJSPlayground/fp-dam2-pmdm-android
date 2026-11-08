# Intents e intercambio de información

## Pedir una acción al sistema

Un **Intent** describe una operación que Android debe resolver. Puede abrir un componente concreto o pedir una acción que otra aplicación ofrezca, como mostrar una página o compartir texto. No es una llamada HTTP ni un mensaje que actualice automáticamente una pantalla Compose.

Un Intent **explícito** identifica el componente de destino, por ejemplo una Activity propia. Un Intent **implícito** expresa una acción y sus datos; Android busca aplicaciones capaces de atenderla. Cuaderno tiene una sola Activity y navega internamente con Navigation Compose. No necesitamos crear otra Activity para cada pantalla solo para utilizar Intents.

Como ejemplo de construcción explícita, `Intent(context, MainActivity::class.java)` identifica nuestra entrada. Ese tipo de destino será útil al preparar la apertura de la app desde una notificación. Construir el objeto no ejecuta por sí mismo la operación.

La [guía de Intents y filtros](https://developer.android.com/guide/components/intents-filters) explica cómo se resuelven y qué declaran los componentes en el manifiesto.

## Abrir un enlace

Para mostrar una página web, un Intent implícito puede combinar `ACTION_VIEW` con una URI HTTPS. Una URI identifica el recurso o la acción de datos; no equivale necesariamente a una ruta de archivo.

Este fragmento pertenece a un controlador de clic de la interfaz. `context` es un contexto disponible desde el contenedor, no un objeto almacenado en el ViewModel:

```kotlin title="Fragmento de una acción de interfaz"
val intent = Intent(
    Intent.ACTION_VIEW,
    Uri.parse("https://developer.android.com/")
)
try {
    context.startActivity(intent)
} catch (noDisponible: ActivityNotFoundException) {
    // La UI informa de que no hay una aplicación disponible para abrir el enlace.
}
```

`Intent` y `ActivityNotFoundException` pertenecen a `android.content`; `Uri`, a `android.net`. En una función componible, `LocalContext.current` se obtiene durante la composición y la acción se ejecuta solo al pulsar. No escribas `startActivity` en el cuerpo de la función: podrías intentar abrir la app externa cada vez que Compose actualice la pantalla.

La existencia de una acción conocida no garantiza que haya un componente disponible en todos los dispositivos. Trata esa situación sin cerrar nuestra app. Para evitar exponer componentes innecesarios, no añadas filtros ni `exported = true` a todas las Activities por costumbre.

## Compartir el texto de una nota

La **Android Sharesheet** presenta los destinos disponibles para compartir. Con texto utilizamos `ACTION_SEND`, el tipo MIME `text/plain` y `EXTRA_TEXT` para el contenido. `createChooser` permite que la persona elija el destino en lugar de que nuestra app imponga un paquete concreto.

```kotlin title="Fragmento de una acción Compartir"
val envio = Intent(Intent.ACTION_SEND).apply {
    type = "text/plain"
    putExtra(Intent.EXTRA_TEXT, "${nota.titulo}\n\n${nota.contenido}")
}
context.startActivity(Intent.createChooser(envio, null))
```

`apply` ejecuta un bloque de configuración sobre el objeto y devuelve ese mismo objeto. Dentro, `type` y `putExtra` pertenecen a `envio`; equivale a configurar esas propiedades por separado. El separador `\n` produce saltos de línea, no los caracteres barra y letra n en el texto final.

El fragmento presupone una nota existente y un contexto de interfaz. La implementación completa debe gestionar la posible ausencia de un destino y conservar la app si se cancela la hoja. Compartir entrega una copia del texto al destino seleccionado; no concede acceso a toda nuestra base de datos ni informa de si otra persona lo ha leído.

La [guía de envío de datos a otras apps](https://developer.android.com/training/sharing/send) describe tipos MIME, extras y Sharesheet. No necesitamos permisos de almacenamiento ni contactos para compartir este texto.

## Compartir imágenes: URI y permiso temporal

Una imagen se comparte normalmente con `ACTION_SEND`, un tipo como `image/jpeg` y una URI en `EXTRA_STREAM`. Pero una ruta privada como `/data/user/.../foto.jpg` no permite que otra app abra el archivo. Tampoco debemos exponer un `file://` arbitrario.

Cuando el contenido ya procede de un proveedor, se utiliza su URI `content://` con los permisos que corresponda. Para un archivo privado propio, **FileProvider** puede ofrecer una URI de contenido según una configuración de rutas limitada. Se concede permiso temporal de lectura al receptor mediante `FLAG_GRANT_READ_URI_PERMISSION` y el mecanismo apropiado de entrega de la URI, sin abrir toda la carpeta privada.

La función de una URI es identificar contenido; el acceso depende de permisos y de la vida del recurso. No guardes como si fuera una ruta local lo que recibes de Photo Picker ni supongas que podrás acceder siempre a cualquier URI después de reiniciar. El tema de multimedia desarrolla selección, captura, almacenamiento y compartición con proyectos completos.

La [configuración oficial de FileProvider](https://developer.android.com/training/secure-file-sharing/setup-sharing) será la referencia para esa integración. Evitaremos crear proveedores y permisos en Cuaderno si no hay archivos que compartir.

## Comprobar si la red activa utiliza Wi-Fi

Para el laboratorio inalámbrico necesitamos distinguir «hay una red activa» de «la red activa utiliza Wi-Fi». Android ofrece `ConnectivityManager` y `NetworkCapabilities` para consultar capacidades de red. Esta comprobación no explora puntos de acceso ni cambia la red del dispositivo.

Declara el permiso normal `ACCESS_NETWORK_STATE` en el manifiesto, además de `INTERNET` para las peticiones. El siguiente fragmento de función consulta el transporte de la red activa:

```kotlin title="Ejemplo de consulta de conectividad"
fun usaWifi(context: Context): Boolean {
    val gestor = context.getSystemService(ConnectivityManager::class.java)
        ?: return false
    val red = gestor.activeNetwork ?: return false
    val capacidades = gestor.getNetworkCapabilities(red) ?: return false
    return capacidades.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
}
```

Necesita `Context` de `android.content` y las clases de `android.net`. Si devuelve `true`, hemos observado transporte Wi-Fi en esa consulta. No demuestra que el servidor esté accesible: puede haber aislamiento entre clientes, un cortafuegos o una red sin salida a Internet. Tampoco es una reserva de conectividad: la red puede cambiar justo después.

Por eso la comprobación es informativa, no una condición que permita eliminar el tratamiento de errores HTTP. Una VPN también puede afectar a lo que se presenta como transporte de la red activa. La petición sigue necesitando gestionar fallo de conexión y tiempo de espera. La [lectura del estado de la red](https://developer.android.com/develop/connectivity/network-ops/reading-network-state) explica capacidades y observación de cambios.

En la pantalla Remotos de la solución, **Comprobar Wi-Fi** realiza esta consulta puntual y presenta «Red activa Wi-Fi» o «La red activa no es Wi-Fi o no está disponible». Lee ese mensaje como resultado de la comprobación de transporte, no como confirmación de una consulta correcta al servidor.

## Comunicar con otro equipo de la misma red

El laboratorio utiliza el mismo formato JSON de publicaciones, servido desde otro ordenador. El teléfono se conecta por Wi-Fi y consulta la dirección privada del servidor. La pantalla Remotos admite esa URL completa en la compilación de depuración, dentro de las restricciones descritas en [HTTP](http.md).

Antes de interpretar un fallo como un error Kotlin, comprueba:

- El servidor está iniciado y escucha en una interfaz alcanzable desde otros equipos, no solo en su propio `localhost`.
- El teléfono y el ordenador están en una red que permite comunicación entre clientes.
- Se utiliza la dirección y el puerto del ordenador servidor, con la ruta exacta de publicaciones.
- El cortafuegos permite el servicio de pruebas en el entorno previsto; no se han desactivado todas sus protecciones.
- La variante de la app permite esa URL de laboratorio y el servidor responde con el JSON esperado.

Una petición al host del ordenador a través de `10.0.2.2` desde el emulador es una prueba útil del cliente, pero no sustituye la prueba de teléfono por Wi-Fi. Registra dispositivo, transporte, petición y resultado observado; si no dispones de teléfono o de una red adecuada, deja esa prueba expresamente pendiente.
