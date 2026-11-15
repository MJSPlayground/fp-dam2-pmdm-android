# Integrar contenido de YouTube

## El recurso pertenece a un servicio con su reproductor

Una URL de una página de YouTube no es un archivo MP4 que pueda entregarse directamente a ExoPlayer o Transformer. El servicio administra reproducción, identidad, restricciones y disponibilidad. Extraer pistas o intentar saltarse su reproductor no forma parte de este tema.

Hay dos integraciones razonables según la necesidad: abrir el enlace mediante la aplicación o navegador, o alojar el reproductor oficial embebido cuando de verdad sea necesario mantenerlo en una pantalla propia. No hay que integrar una biblioteca antigua de Android únicamente por encontrar un tutorial que aún compile.

## Abrir un contenido mediante un Intent

Para una referencia formativa, un Intent HTTPS conserva la experiencia del servicio y permite que Android elija el receptor. Este método de ejemplo puede añadirse a `MainActivity` del editor, que ya tiene los imports de Intent, Uri y ActivityNotFoundException:

```kotlin title="Método de referencia dentro de MainActivity"
private fun abrirYouTube(id: String) {
    require(Regex("[A-Za-z0-9_-]{11}").matches(id))
    val uri = Uri.Builder().scheme("https").authority("www.youtube.com")
        .path("watch").appendQueryParameter("v", id).build()
    try {
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    } catch (_: ActivityNotFoundException) {
        mensaje = getString(R.string.sin_destino)
    }
}
```

`id` es el identificador del vídeo, no una URL completa ni una clave API. La comprobación evita construir una ruta arbitraria, pero no acredita que el vídeo esté disponible o se pueda ver en cualquier país. El usuario sigue necesitando conexión y cumplir las condiciones del servicio. El ejemplo no descarga ni transforma contenido.

## El reproductor embebido utiliza el API IFrame

La [referencia oficial IFrame](https://developers.google.com/youtube/iframe_api_reference) describe un reproductor HTML que se controla mediante JavaScript y eventos. Su API puede informar de preparado, reproducción, pausa y error. Es un contrato de esa página web, no un objeto Player de Media3.

Una integración con WebView necesita una página controlada, origen y parámetros apropiados, tamaño suficiente para los controles, navegación segura y el ciclo de vida correspondiente. Debe conservar las funciones y presentación exigidas por el servicio. No se inserta código HTML recibido de una entrada libre ni se permite que una WebView de medios navegue sin control por cualquier URL.

Las restricciones de reproducción automática, inicio de sesión, edad, país o permisos de inserción del propietario pueden impedir un vídeo. Un código de error se trata como estado del servicio, no como razón para intentar acceder a sus pistas por otro método.

## Identidad de la app y privacidad

La documentación vigente de IFrame describe la integración con **Android WebView Media Integrity**: los reproductores embebidos pueden verificar identidad de la app y dispositivo mediante metadatos y atestación. Esa dependencia del entorno tiene que revisarse antes de elegir WebView, especialmente en emuladores y dispositivos sin servicios compatibles.

La configuración y los datos que intercambia el reproductor pertenecen a ese servicio. Antes de introducirlo en un producto hay que revisar políticas, privacidad y consentimiento que correspondan. Para simplemente enlazar un recurso docente, abrir el enlace evita mantener un reproductor embebido dentro de nuestra app.

## Qué se incluye y cómo se verifica

Los laboratorios del tema trabajan con archivos propios, no incluyen vídeos de terceros ni un reproductor de YouTube oculto. El método anterior es una referencia de integración externa con contexto de código, y el API IFrame se explica para poder decidir cuándo usarlo.

Si se incorpora a una app real, verifica apertura con aplicación instalada y con navegador, cancelación y regreso, contenido no disponible y restricciones de inserción cuando se elija ese camino. No se da por probada esa comunicación con el servicio por compilar el editor local.
