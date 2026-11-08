# Cuaderno — navegacion

Este proyecto es una etapa de la misma aplicación incremental. No abras toda la carpeta
desarrollo como proyecto Gradle: abre esta carpeta **navegacion** mediante **Open** en
Android Studio y espera la sincronización. Usa Android SDK 36 y JDK de Gradle17 o
JBR21 del IDE. Selecciona **app**, un emulador/API26 o posterior, y **Run**.

## Progresión

1. [Inicial](https://github.com/MJSPlayground/fp-dam2-pmdm-android/tree/main/proyectos/desarrollo/inicial): interfaz preparada y TODO por etapa.
2. [Compose](https://github.com/MJSPlayground/fp-dam2-pmdm-android/tree/main/proyectos/desarrollo/compose): formulario, estado y lista en memoria.
3. [Navegación](https://github.com/MJSPlayground/fp-dam2-pmdm-android/tree/main/proyectos/desarrollo/navegacion): lista, alta y detalle con rutas tipadas.
4. [Remotos](https://github.com/MJSPlayground/fp-dam2-pmdm-android/tree/main/proyectos/desarrollo/remotos): ViewModel, StateFlow, HTTP y repositorio en memoria.
5. [Solución formativa](https://github.com/MJSPlayground/fp-dam2-pmdm-android/tree/main/proyectos/desarrollo/solucion): añade persistencia con Room.

Cada etapa tiene el mismo applicationId: instalar otra sustituye la anterior.
Las notas de esta etapa viven en memoria y se pierden al reiniciar el proceso.
Los textos están en strings.xml; el tema sigue el sistema claro/oscuro.

Esta etapa conserva la lista mientras se navega entre destinos de la composición.
Recrear la Activity o cerrar el proceso elimina estas notas en memoria; si un destino
ya no encuentra su identificador, informa de ello y permite volver. La solución
posterior introduce ViewModel y Room para resolver esas pérdidas de datos.

## Comprobación técnica

Desde esta carpeta:
```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:lintDebug --console=plain
```

La configuración está fijada en gradle/libs.versions.toml.
Consulta el estado del repositorio para las pruebas realmente ejecutadas.

Código y Gradle Wrapper bajo Apache-2.0; LICENSE incluido y avisos del wrapper en
sus scripts. No se incluyen claves ni servicios de pago.
