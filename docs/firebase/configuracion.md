# Configurar Firebase y un laboratorio local

## Qué servicio resuelve cada problema

Authentication administra credenciales e identidad; Firestore guarda documentos consultables; Storage guarda archivos. El identificador de usuario enlaza sus reglas de propiedad, pero son servicios distintos. Una fotografía grande no debe guardarse como una cadena Base64 dentro de cada documento: un documento puede guardar metadatos y la ruta del objeto.

El servidor valida el acceso. Ocultar un botón o filtrar una lista en Compose no impide que otro cliente invoque las APIs. Una app Android se distribuye a dispositivos que no controlamos y su código puede inspeccionarse; no es un lugar para incluir credenciales administrativas.

La [configuración oficial Android](https://firebase.google.com/docs/android/setup) describe registro de la app, servicios y dependencias. El laboratorio fija Firebase BoM **34.7.0**, con los módulos principales `firebase-auth`, `firebase-firestore` y `firebase-storage`. No usa los antiguos artefactos `-ktx`: las extensiones Kotlin se incorporaron a los módulos principales. La versión elegida está en el catálogo y no se presenta como la última disponible.

## Un proyecto real tiene decisiones adicionales

En Firebase Console se crea o selecciona un proyecto, se registra el identificador Android y se habilitan solo los servicios necesarios. El asistente proporciona configuración de cliente; esa configuración no es una cuenta de servicio ni sustituye reglas de acceso. Antes de usarla hay que revisar región, plan, cuotas, políticas de retención, autenticadores y costes. No se habilita facturación como paso automático de una práctica.

El plugin Google Services puede convertir la configuración de una app registrada en recursos Android. No debe inventarse un `google-services.json` que afirme pertenecer a un proyecto ajeno. Las cuentas de servicio, claves privadas y credenciales del Admin SDK permanecen fuera del cliente y del repositorio.

## Un entorno local independiente

El laboratorio se identifica como `demo-pmdm`. Sus opciones se construyen explícitamente y todos sus clientes se conectan a emuladores antes de hacer operaciones. Solo Debug habilita la pantalla funcional; no hay una opción escondida que envíe datos a una cuenta real.

| Servicio | Dirección en el ordenador | Dirección desde el emulador Android |
| --- | --- | --- |
| Authentication | `127.0.0.1:9099` | `10.0.2.2:9099` |
| Firestore | `127.0.0.1:8085` | `10.0.2.2:8085` |
| Storage | `127.0.0.1:9199` | `10.0.2.2:9199` |
| Interfaz de administración local | `127.0.0.1:4005` | Se abre en el navegador del ordenador |

`10.0.2.2` es una dirección especial del emulador Android para alcanzar su host. No funciona como dirección del ordenador desde un teléfono físico. Los servicios escuchan solo en loopback: esta configuración no abre el laboratorio al resto de la red. Una excepción de seguridad de red limitada a Debug y a ese host permite HTTP local; no habilita HTTP externo en una versión distribuida.

## Iniciar y detener los servicios

Cada ZIP incluye una carpeta `emuladores` con configuración, reglas, versiones y pruebas. Para este servicio externo se necesita Node 22 o posterior y Java 25 además del entorno Android. En la terminal del ordenador, entra en esa carpeta, ejecuta `npm ci` y después `npm start`. La primera instalación descarga herramientas; el arranque puede descargar los emuladores oficiales. Mantén esa terminal abierta y detén con Ctrl+C al terminar.

Después abre el proyecto Android y pulsa Run. Si otro programa ocupa un puerto, detén ese proceso propio o cambia de forma coherente la configuración y las direcciones del cliente; no termines servicios ajenos para liberar puertos. No ejecutes `npm test` mientras siga activo `npm start`, porque las pruebas inician su propia sesión temporal.

Sin exportación, detener la sesión elimina sus datos de servidor. El cliente Firestore puede conservar caché local y Authentication una sesión: si reinicias servidores desde cero, cierra sesión en la app y crea otra cuenta local para evitar confundir caché con datos del servidor nuevo. Las [guías de conexión a emuladores](https://firebase.google.com/docs/emulator-suite/connect_and_prototype) explican sus diferencias respecto a producción.
