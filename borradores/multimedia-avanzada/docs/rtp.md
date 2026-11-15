# RTP, control y monitorización

## Tres trabajos que conviene separar

Una transmisión necesita preparar la sesión, transportar medios y observar su calidad. **RTSP** controla una sesión de reproducción; **RTP** transporta unidades de audio o vídeo con información temporal; **RTCP** comunica informes de los participantes. No son tres extensiones de archivo ni términos equivalentes.

Un archivo MP4 por HTTPS puede reproducirse progresivamente sin utilizar RTP. Un directo HLS utiliza segmentos y manifiestos HTTP. Un sistema interactivo WebRTC combina mecanismos adicionales de negociación, conectividad, seguridad y adaptación; no consiste simplemente en cambiar un puerto UDP.

## Qué información aporta RTP

Una cabecera RTP identifica una fuente y permite reconstruir el orden y el tiempo relativo de las muestras. El **número de secuencia** ayuda a detectar saltos o reordenaciones. El **timestamp** sigue el reloj del medio, que depende de su codificación; no son milisegundos universales. El identificador **SSRC** distingue la fuente de sincronización.

Imagina recibir secuencias 100,101,103 y después102. El salto observado no demostraba todavía una pérdida definitiva: había reordenación. Para interpretar el tiempo de vídeo se necesita conocer la frecuencia de reloj declarada, por ejemplo un reloj de 90 kHz; un incremento de 9000 representa 0,1 s en ese reloj. Cambiar la hora del teléfono no cambia el significado de esa unidad.

RTP no garantiza entrega, cifrado ni un plazo máximo. Esas propiedades dependen del transporte y del diseño completo. El [estándar RTP/RTCP](https://www.rfc-editor.org/rfc/rfc3550) define cabeceras e informes; aquí nos interesa interpretar evidencias, no implementar un protocolo desde cero.

## Describir y controlar una sesión

El cliente RTSP obtiene una descripción de sesión —SDP— con pistas, códecs y parámetros necesarios. Después negocia transporte y solicita reproducción. Operaciones como DESCRIBE, SETUP, PLAY y TEARDOWN tienen responsabilidades diferentes: conocer las pistas, preparar su transporte, comenzar y cerrar la sesión.

Las versiones del protocolo y el subconjunto admitido por cada SDK importan. No se presume soporte de cualquier servidor porque acepte una cadena `rtsp://`. La [guía de RTSP de Media3](https://developer.android.com/media/media3/exoplayer/rtsp) especifica formatos y transportes, y el [estándar RTSP](https://www.rfc-editor.org/rfc/rfc7826) permite consultar su modelo de control.

En la combinación fijada se estudia H.264 y audio MPEG-4 compatible. El servidor debe describir correctamente la configuración del códec; enviar paquetes con un nombre de pista convincente no basta para que el decodificador tenga sus parámetros iniciales.

## UDP frente a TCP intercalado

RTP sobre UDP permite que lleguen unidades posteriores aunque una anterior se pierda, pero necesita gestionar pérdidas y variación de llegada. NAT y firewall pueden impedir recibir sus puertos. RTSP puede transportar RTP intercalado sobre su conexión TCP, con mejor accesibilidad en determinadas redes y el coste de que una retransmisión retrase datos posteriores.

El cliente del laboratorio elige explícitamente RTP sobre RTSP/TCP:

```kotlin title="Fragmento de StreamActivity para una entrada RTSP"
val fuente = RtspMediaSource.Factory()
    .setForceUseRtpTcp(true)
    .createMediaSource(MediaItem.fromUri(url))
player.setMediaSource(fuente)
```

No representa una conexión cifrada ni elimina todo retardo. El motor y la vista se liberan al abandonar la pantalla. Para estudiar UDP habría que cambiar esa decisión y comprobar conectividad y puertos; no se anuncia que se han probado ambos transportes por funcionar uno.

## Qué se puede medir

| Evidencia | Qué aporta | Qué no permite concluir por sí sola |
| --- | --- | --- |
| Secuencia recibida | Orden, huecos y reordenación | Motivo exacto de cada hueco |
| Informes RTCP | Estadísticas de recepción y temporización | Calidad visual percibida completa |
| Jitter | Variación de llegada respecto al reloj | Retardo total cámara→pantalla |
| Estimación de ancho de banda | Orientación sobre capacidad observada | Bitrate garantizado ni ausencia de congestión |
| Frames descartados | Trabajo que el renderizador no presentó | Número de paquetes perdidos en red |
| Primera imagen renderizada | El flujo alcanzó decodificación/presentación | Estabilidad de toda la sesión |

Para medir retardo de extremo a extremo necesitas una referencia observable y un método de comparación temporal. Contar cuánto tarda `prepare()` en devolver no mide ese recorrido. Los informes y el tráfico deben analizarse en una sesión propia y autorizada, sin publicar contenido o direcciones personales.

## Emisor local reproducible

El README técnico de Mesa de edición describe un servidor MediaMTX 1.21.3 y FFmpeg publicando el vídeo propio incluido en bucle. La configuración de prueba limita RTSP a loopback, desactiva otros protocolos y usa el puerto 8558. El emulador lo alcanza mediante `rtsp://10.0.2.2:8558/aula`.

Una prueba instrumentada puede recibir esa fuente como argumento y espera el evento real de primera imagen. El servidor debe registrar la sesión de lectura y el cliente recibir frames. Esto demuestra una transmisión local RTP intercalada sobre TCP, con patrón sintético; no recepción de una cámara física, pérdida de paquetes UDP ni comportamiento de una red pública.

Si no se arranca el emisor, la app debe presentar el error y permitir volver. Las pruebas normales utilizan la muestra local, para que ejecutar el proyecto no dependa de disponer siempre de un servidor.

## Comparar soluciones según el problema

HLS/DASH facilitan distribución segmentada y caché HTTP para muchas personas, con políticas de latencia propias. RTSP/RTP es frecuente en cámaras y sistemas controlados. WebRTC se orienta a comunicación interactiva y añade negociación y conectividad específicas. La elección depende de latencia, audiencia, infraestructura y control, no de que una alternativa se llame «tiempo real».

El [sitio oficial de WebRTC](https://webrtc.org/getting-started/overview) y las guías de Media3 ayudan a contrastar sus responsabilidades. Ninguna de estas opciones se convierte automáticamente en una videollamada completa o un servidor de emisión por añadir un módulo Gradle.
