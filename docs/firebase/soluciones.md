# Soluciones de Firebase

El [proyecto solución](../descargas/proyectos/firebase-solucion.zip) conserva `MainActivity` como entrada. `NubeRepository.eliminar` usa la referencia privada del documento y la tarea de borrado; `descargar` recupera el objeto del usuario con un límite de bytes. La Activity observa sesión y documentos y retira registros al dejar de necesitarlos.

1. Authentication gestiona identidad; Firestore, documentos y consultas; Storage, archivos. Una imagen va como objeto y sus metadatos pueden ir en un documento.
2. No. Otro cliente puede omitir ese filtro; las reglas del servidor deben denegar rutas de otras cuentas.
3. El UID lo establece la autenticación. Un texto de formulario puede cambiar o ser falsificado y no demuestra identidad.
4. Crear registra credenciales; acceder confirma una cuenta existente. Cerrar sesión local no elimina cuenta, documentos ni objetos.
5. Puede ser un evento local pendiente o de caché. Si se necesita confirmación del servidor hay que interpretar metadatos y finalización, sin equiparar presencia visual con autorización confirmada.
6. Acumula suscripciones y trabajo duplicado. Se conserva su ListenerRegistration y se llama a remove al abandonar o cambiar de usuario.
7. Add crea un ID nuevo; set escribe en una referencia; update modifica campos de un documento existente; delete elimina ese documento. Subcolecciones y archivos tienen ciclos de vida propios.
8. El tamaño codificado no determina los píxeles decodificados. También hay que limitar dimensiones y elegir un tamaño de trabajo antes de materializar la imagen completa.
9. No: el MIME es una declaración. Las reglas limitan los metadatos y el tamaño; el cliente sigue tratando errores de formato y decodificación.
10. No. Un SDK privilegiado puede omitirlas. Se requieren pruebas mediante clientes anónimos y autenticados con identidades distintas.
11. Es una dirección especial del emulador Android. Un teléfono necesita una configuración de red deliberada y protegida; abrir servicios de prueba sin estudiar acceso y datos no es una solución automática.
12. Proyecto/app registrados, proveedor habilitado, configuración OAuth y firma, credenciales obtenidas por un flujo soportado y reglas. El laboratorio prueba cuentas y servicios locales; no acredita una identidad de Google ni facturación, cuotas o seguridad de una instalación real.

Las pruebas de reglas distinguen acceso permitido de denegación y comprueban tiempo real y archivos. El hecho de que pasen en local no sustituye una revisión de configuración y límites del proyecto que se vaya a utilizar en producción.
