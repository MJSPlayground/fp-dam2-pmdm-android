# Cuaderno — inicial

Este proyecto es una etapa de la misma aplicación incremental. No abras toda la carpeta
desarrollo como proyecto Gradle: abre esta carpeta **inicial** mediante **Open** en
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

## Trabajo del alumno

En MainActivity sigue los TODO compose-estado, compose-titulo, compose-contenido,
compose-validacion y compose-lista. Antes de completarlos, los campos están preparados
pero no aceptan cambios y Guardar permanece deshabilitado. El paso de lista sustituye
la raíz con scroll por LazyColumn: no anides una lista vertical dentro de otro scroll.

Los ejercicios Kotlin independientes están en
`app/src/main/java/es/mjsplayground/pmdm/desarrollo/cuaderno/ejercicios/KotlinEjercicios.kt`.
Sus pruebas se entregan desactivadas con @Ignore: actívalas cuando completes los TODO.
No se incluyen aquí implementaciones de Room, Retrofit ni soluciones de esas etapas.

## Comprobación técnica

Desde esta carpeta:
```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:lintDebug --console=plain
```

La configuración está fijada en gradle/libs.versions.toml.
Consulta el estado del repositorio para las pruebas realmente ejecutadas.

Código y Gradle Wrapper bajo Apache-2.0; LICENSE incluido y avisos del wrapper en
sus scripts. No se incluyen claves ni servicios de pago.
