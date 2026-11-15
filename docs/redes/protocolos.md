# Redes y protocolos móviles

## El transporte y el servicio responden preguntas distintas

Un teléfono puede tener Wi-Fi conectado sin alcanzar un servidor, o disponer de red móvil mientras un recurso HTTP devuelve un error. **Transporte** describe por dónde viajan los datos; **protocolo de aplicación**, qué mensajes se intercambian y cómo se interpretan.

La acción **Consultar transporte de red** del laboratorio lee `ConnectivityManager`, la red activa y sus `NetworkCapabilities`. Distingue Wi-Fi, móvil, otro transporte y ausencia de red. El permiso normal `ACCESS_NETWORK_STATE` permite esa consulta. No realiza una petición para decidir si un servidor concreto funciona.

Un portal cautivo, un cortafuegos, un nombre que no se resuelve o una aplicación servidora detenida pueden impedir la comunicación con un transporte activo. El estado de red sirve para orientar el diagnóstico; la petición y su respuesta son la comprobación de ese servicio. La [guía de operaciones de red](https://developer.android.com/develop/connectivity/network-ops/connecting) explica también permiso de Internet y trabajo asíncrono.

## Elegir un canal según el intercambio

| Mecanismo | Qué aporta | Qué debes definir o comprobar |
| --- | --- | --- |
| HTTP/HTTPS | Recursos, estados y respuestas; puede funcionar en red local o Internet | URL, contrato, autenticación y errores |
| TCP | Flujo de bytes ordenado sobre una conexión | Fronteras de mensajes y protocolo de aplicación |
| UDP | Datagramas, sin garantía de entrega u orden | Pérdidas, repetición, tamaño y secuencia cuando importen |
| Bluetooth clásico RFCOMM | Flujo de bytes entre dispositivos próximos compatibles | Servicio, conexión, trama y cierre |
| BLE/GATT | Servicios y características para datos pequeños y estados | UUID, propiedades, operaciones asíncronas y permisos |

UDP no se vuelve fiable por estar cerca del otro equipo. BLE no es una versión pequeña de HTTP: una característica no equivale a una ruta REST y sus operaciones no admiten el mismo tamaño o ritmo. RFCOMM no delimita tus mensajes aunque una escritura coincida casualmente con una lectura en una prueba.

El entorno móvil puede perder conectividad entre una comprobación y el envío. Conserva estados de carga, error, reintento y cancelación. No reintentes indefinidamente: una dirección equivocada no se corrige consumiendo batería.

## Comunicación con otro equipo

Consultar una API pública demuestra HTTP, pero no necesariamente comunicación con un dispositivo próximo. Para un laboratorio de red local, identifica quién sirve, desde qué dispositivo se solicita y qué resultado permite comprobar el intercambio. En el emulador, `10.0.2.2` alcanza el host; en un teléfono esa dirección no representa el ordenador del aula.

La ampliación incluye un servidor HTTP propio y un protocolo Bluetooth mínimo. Para una prueba física, utiliza equipos tuyos o autorizados, documenta los papeles de cada uno y registra un mensaje público como HOLA. No hace falta utilizar nombres reales, mensajes personales ni subir direcciones de hardware al informe público.

## Disponibilidad de hardware y consentimiento

Una app puede admitir Bluetooth sin exigirlo para instalarse. El manifiesto del laboratorio declara características opcionales y comprueba adaptador y estado antes de operar. Un dispositivo sin anunciante BLE puede actuar de central; otros no tendrán la capacidad necesaria.

Ninguna exploración o conexión comienza al abrir la app. Los botones identifican la operación y sus límites. Los permisos se solicitan según esa acción; abrir HTTP no pide acceso a dispositivos próximos. El usuario puede denegar y continuar con el resto de la aplicación.

El emulador comprueba pantalla, estados y HTTP; no sustituye dos radios físicas, emparejamiento, anuncios BLE o transferencia real. Un resultado sin dispositivos puede deberse a soporte, estado, permisos o filtro, y no demuestra que no exista ningún equipo cercano.
