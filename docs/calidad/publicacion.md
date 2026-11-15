# Firma, distribución y mantenimiento

## Qué permite instalar una aplicación

Un APK contiene una aplicación instalable y necesita firma. La firma relaciona la entrega con un certificado; no es una contraseña escrita en la pantalla ni el nombre de versión. Una actualización debe conservar la identidad compatible del paquete y su firma según el esquema de distribución utilizado.

Un AAB es un formato de publicación: contiene código y recursos para generar los APK adecuados. No se instala como un APK con el botón de un explorador de archivos. Google Play puede generar y firmar APKs desde el bundle mediante Play App Signing. La [guía de App Bundles](https://developer.android.com/guide/app-bundle) desarrolla esta diferencia.

| Artefacto | Uso habitual | Lo que falta comprobar |
| --- | --- | --- |
| APK Debug | Desarrollo en emulador o dispositivo propio | No representa por sí solo la distribución Release |
| APK Release firmado | Instalación de esa entrega compatible | Recorrido real, actualización y conservación de datos |
| AAB firmado para subida | Entrega a un servicio de distribución compatible | Procesamiento del servicio y APKs que recibirá cada dispositivo |

El laboratorio prepara Debug y artefactos Release sin firma privada. No crea una cuenta de publicación, no genera una release pública ni modifica una app de Play.

## Crear una firma de forma controlada

En Android Studio, **Build → Generate Signed App Bundle or APK** permite elegir formato, keystore, alias y variante. El recorrido sirve para una app propia cuyo propietario ha decidido preparar una distribución. Guarda el keystore fuera del repositorio y de las descargas, con copia de seguridad protegida; conserva contraseñas y acceso en un gestor apropiado.

La clave de depuración que usa el IDE es para desarrollar, no el plan de custodia de una app publicada. No pongas contraseñas en un `build.gradle.kts` compartido ni subas el archivo aunque `.gitignore` permita evitar nuevos rastreos: si ya se versionó, ignorarlo después no borra su historial.

Con Play App Signing se distinguen la **clave de firma de la app**, utilizada para los APK distribuidos, y la **clave de subida**, utilizada para autenticar bundles enviados. Confundir ambas puede llevar a registrar una huella incorrecta en un proveedor o pensar que basta con cualquier keystore nuevo para actualizar. La [guía oficial de firmado](https://developer.android.com/studio/publish/app-signing) describe las opciones y recuperación.

La actividad de versiones usa una huella como identificador del certificado. Una huella pública no es una clave privada; el ejercicio no almacena material de firma real. El algoritmo didáctico no pretende reproducir todos los esquemas de rotación o actualización de una plataforma.

## Probar antes de una distribución general

Play Console organiza pruebas y producción mediante canales. Un canal interno permite validar una entrega con un grupo controlado; otros canales amplían los destinatarios y condiciones. Antes de una entrega real consulta los [procedimientos y requisitos de la consola](https://support.google.com/googleplay/android-developer/answer/9845334): las condiciones de cuentas, pruebas y revisión pueden cambiar.

Un recorrido de publicación incluye identidad y versión, ficha, clasificación, privacidad y seguridad de datos, público destinatario, permisos y políticas aplicables. Completar el formulario no sustituye comprobar que lo declarado corresponde a lo que hace el SDK y la aplicación. Un servicio externo de fallos o IA también puede afectar a esos datos.

En un entorno de pruebas, verifica la descarga del canal, arranque, navegación, almacenamiento, permisos y errores. Comprueba **actualizar desde la entrega anterior**, con datos ya creados: reinstalar desde cero no detecta una migración que pierde notas. Verifica también el dispositivo mínimo y las configuraciones que el bundle genera para los destinos soportados.

Las pruebas locales, una firma válida y la aceptación del bundle son evidencias distintas. No marques «publicación comprobada» si solo generaste un archivo. En estos materiales no se ha ejecutado una publicación en Play ni una instalación Release firmada.

## Mantener después de publicar

Cada incidencia debería identificar versión, entorno y acciones reproducibles, eliminando datos personales. Relaciona el informe con su commit y mapeo. Corrige, reproduce el fallo original y ejecuta las pruebas afectadas; después verifica las rutas que podría haber roto la corrección.

Mantén una estrategia de datos: migraciones, copias admitidas y restauración. Detener un despliegue no hace que desaparezcan las versiones ya instaladas. Volver a distribuir código anterior con un código de versión menor no es una estrategia universal de reversión; puede requerir una nueva entrega compatible y conservar cambios de esquema.

Planifica actualizaciones de dependencias y SDK con pruebas proporcionadas. No mezcles una corrección urgente con un rediseño sin relación. El registro de una entrega debe decir qué cambió, qué se probó realmente, qué queda pendiente y cómo identificar el artefacto, sin almacenar secretos ni datos del alumnado.
