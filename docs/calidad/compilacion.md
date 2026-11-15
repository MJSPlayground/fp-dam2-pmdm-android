# Variantes, dependencias y R8

## Debug y Release representan productos distintos

Debug facilita depuración, inspección y firma automática de desarrollo. Release debe preparar el comportamiento que recibirán los usuarios. No basta con cambiar el nombre del archivo APK: las opciones de compilación y las fuentes de cada variante pueden ser diferentes.

En `app/build.gradle.kts` del laboratorio, Debug añade `.debug` a `applicationId` y `-debug` al nombre de versión. Así puede instalarse junto a una versión de distribución sin sustituir su identidad. La clase Kotlin sigue perteneciendo al namespace del módulo; el sufijo de instalación no obliga a renombrar todos los paquetes.

Este fragmento ya está implementado dentro de `android`:

```kotlin
buildTypes {
    debug {
        applicationIdSuffix = ".debug"
        versionNameSuffix = "-debug"
    }
    release {
        isMinifyEnabled = true
        isShrinkResources = true
        proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )
    }
}
```

No incluye firma privada. Compilar esa Release produce artefactos sin una firma de distribución configurada; no equivale a instalarlos ni a publicarlos. La [documentación de variantes](https://developer.android.com/build/build-variants) explica tipos de compilación, sabores y source sets. No introduzcas sabores para cada estado de una pantalla: prueba su estado mediante entradas controladas.

## Identidad y número de versión

`applicationId` identifica la instalación. `versionCode` es el número interno que debe avanzar en las nuevas entregas; `versionName` es el nombre legible, como `1.2`. Cambiar solo el nombre visible no demuestra que la nueva entrega tenga un código admisible para su canal de distribución.

La [guía de versiones](https://developer.android.com/studio/publish/versioning) describe esos campos. Mantén un registro de qué commit corresponde a cada artefacto y no reutilices el mismo código para dos entregas diferentes de un canal que exige incremento. La actividad autónoma modela una comprobación conservadora de candidato por paquete, certificado y código.

## Qué hace R8

R8 analiza las referencias de la aplicación para eliminar código no alcanzable, optimizar y reducir nombres. La reducción de recursos elimina recursos que no necesita la aplicación resultante. Estas operaciones pueden cambiar el resultado si una biblioteca usa reflexión o carga recursos por nombres que el análisis no ve.

`proguard-rules.pro` del laboratorio no añade reglas generales porque su código no serializa por reflexión ni requiere conservar clases por nombre. No empieces por `-keep class ** { *; }`: puede impedir la optimización y ocultar el problema. Revisa las reglas que suministra cada biblioteca y añade reglas pequeñas que correspondan a una necesidad comprobada. La [guía de R8](https://developer.android.com/build/shrink-code) detalla el proceso.

Si Debug funciona y Release falla, reproduce el recorrido en Release firmada de pruebas, guarda su informe y contrasta reflexión, serialización, recursos dinámicos y reglas. Conserva `mapping.txt` de la **misma compilación** para interpretar una pila ofuscada; el mapeo de otra versión puede producir nombres incorrectos.

La solución se puede compilar también con `assembleRelease` y `bundleRelease` desde la ventana Gradle del IDE. Eso verifica que R8 y el empaquetado terminan, pero la ejecución Release requiere además una firma de pruebas o distribución apropiada y su instalación. Los artefactos de `build` no se incluyen en los ZIP docentes.

## Dependencias y compilaciones reproducibles

El catálogo de versiones fija plugins y bibliotecas; el wrapper fija Gradle. Una actualización puede cambiar requisitos de JDK, SDK, Kotlin o APIs. Prueba una familia coherente de versiones, lee cambios oficiales y comprueba Debug, tests y Release antes de mezclar actualizaciones.

`testImplementation` mantiene JUnit, MockK y corrutinas de prueba fuera de las dependencias de la aplicación distribuida. `androidTestImplementation` corresponde al APK instrumentado. No añadas MockK a `implementation` para usarlo dentro de una pantalla.

Las cachés de Gradle evitan repetir trabajo válido; no deben contener la única copia de un código fuente o una configuración necesaria. Para mejorar tiempos, mide tareas lentas en Build Analyzer y comprueba sincronización, cambios que invalidan tareas y configuración. Evita borrar todas las cachés como rutina: obliga a descargar y compilar otra vez sin corregir necesariamente la causa. La [optimización de compilaciones](https://developer.android.com/build/optimize-your-build) aporta criterios de medición.
