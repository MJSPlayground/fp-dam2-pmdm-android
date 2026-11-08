# Cuaderno — solucion

Este proyecto es una etapa de la misma aplicación incremental. No abras toda la carpeta
desarrollo como proyecto Gradle: abre esta carpeta **solucion** mediante **Open** en
Android Studio y espera la sincronización. Usa Android SDK 36 y JDK de Gradle17 o
JBR21 del IDE. Selecciona **app**, un emulador/API26 o posterior, y **Run**.

## Progresión

1. [Inicial](https://github.com/MJSPlayground/fp-dam2-pmdm-android/tree/main/proyectos/desarrollo/inicial): interfaz preparada y TODO por etapa.
2. [Compose](https://github.com/MJSPlayground/fp-dam2-pmdm-android/tree/main/proyectos/desarrollo/compose): formulario, estado y lista en memoria.
3. [Navegación](https://github.com/MJSPlayground/fp-dam2-pmdm-android/tree/main/proyectos/desarrollo/navegacion): lista, alta y detalle con rutas tipadas.
4. [Remotos](https://github.com/MJSPlayground/fp-dam2-pmdm-android/tree/main/proyectos/desarrollo/remotos): ViewModel, StateFlow, HTTP y repositorio en memoria.
5. [Solución formativa](https://github.com/MJSPlayground/fp-dam2-pmdm-android/tree/main/proyectos/desarrollo/solucion): añade persistencia con Room.

Cada etapa tiene el mismo applicationId: instalar otra sustituye la anterior.
Los datos de Room sobreviven al cierre. No es una sincronización: importar crea una copia local editable.
Los textos están en strings.xml; el tema sigue el sistema claro/oscuro.

## Recorrido y responsabilidades

MainActivity obtiene un único Contenedor desde CuadernoApplication. El contenedor
crea Room, Retrofit y NotaRepository. CuadernoViewModel recibe el repositorio por
constructor: conserva CuadernoUiState y procesa los eventos. La interfaz observa
StateFlow con collectAsStateWithLifecycle; los composables no ejecutan consultas SQL.
DetalleViewModel recupera el identificador de la ruta con SavedStateHandle.toRoute.

Crea una nota con un título de 3 a 80 caracteres y contenido no vacío. Abre su tarjeta
para modificarla, compartir el texto o eliminarla con confirmación. Los espacios
exteriores se recortan al guardar. Cancelar el diálogo conserva la nota.

En **Notas remotas**, pulsa **Cargar** para consultar JSONPlaceholder. **Importar copia**
guarda una copia editable con identificador local nuevo; no modifica el servidor.
Los títulos remotos de más de 80 caracteres se recortan para adaptarse al formulario.
Importar de nuevo crea otra copia: este ejemplo no implementa sincronización.

## Laboratorio de red

El campo acepta la URL completa del recurso: por ejemplo
`https://jsonplaceholder.typicode.com/posts`. El servicio de pruebas del repositorio
puede usarse con `http://10.0.2.2:8080/posts` en el emulador y
`http://IP-PRIVADA-DEL-EQUIPO:8080/posts` en un teléfono de la misma red.
Utiliza `/posts?delay=2` para observar carga y `/error` para una respuesta503.
Apagar el servidor permite observar error de conexión y **Reintentar**.

**Comprobar Wi-Fi** consulta puntualmente el transporte de la red activa. No acredita
que el servidor sea accesible y una VPN puede alterar el transporte informado.
La comunicación inalámbrica con otro equipo debe probarse en una red real.
El alias10.0.2.2 del emulador no constituye esa prueba con un teléfono físico.

Solo debug permite HTTP, y el repositorio lo restringe a localhost y direcciones
IPv4 privadas/loopback. Release exige HTTPS tanto en el manifiesto como en el validador.
No uses datos personales en el laboratorio HTTP. Consulta el README de
[servidor-pruebas](https://github.com/MJSPlayground/fp-dam2-pmdm-android/tree/main/proyectos/desarrollo/servidor-pruebas)
para iniciar el servicio y configurar la escucha/firewall.

## Pruebas incluidas

- JVM: límites de validación, conversión de JSON remoto a nota, política de URL,
  diferencia entre errores de formulario y URL, ejercicios Kotlin y respuestas
  Retrofit200/503/JSON incompatible contra MockWebServer local.
- Android: CRUD de Room en memoria; formulario inválido, recreación de Activity,
  alta/edición y cancelación/confirmación de borrado; error de URL y reintento.

La prueba de interfaz utiliza la base de datos de desarrollo del emulador y vacía
sus notas al empezar. Ejecútala en un emulador de pruebas, no sobre datos que necesites.
MockWebServer es una dependencia exclusiva de test; no se distribuye en la aplicación.

## Comprobación técnica

Desde esta carpeta:
```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:lintDebug --console=plain
.\gradlew.bat :app:connectedDebugAndroidTest --console=plain
```

La configuración está fijada en gradle/libs.versions.toml.
Consulta el estado del repositorio para las pruebas realmente ejecutadas.

Código y Gradle Wrapper bajo Apache-2.0; LICENSE incluido y avisos del wrapper en
sus scripts. No se incluyen claves ni servicios de pago.
