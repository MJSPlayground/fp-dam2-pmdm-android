# Lecturas — solucion

Ampliación de persistencia y arquitectura. Abre **esta carpeta** en Android Studio mediante Open sobre `settings.gradle.kts`, sincroniza, selecciona **app** y pulsa Run con un emulador API 26 o posterior. SDK 37 y JBR 25 del IDE para Gradle. No abras la carpeta que contiene ambos proyectos como si fuera un solo build.

En una instalación nueva, la pantalla empieza vacía. **Cargar demostración local** añade dos lecturas con autor y etiquetas; no necesita conexión. **Actualizar desde API** consulta cinco publicaciones HTTPS de JSONPlaceholder y las adapta a fichas de lectura. No son libros reales de una API bibliográfica. Marcar y filtrar son decisiones locales, que no se suben al servicio.

## Recorrido

`MainActivity` observa `LecturasViewModel`. El ViewModel combina Room y DataStore y llama a `AlternarLectura` para cambiar el estado. `DatosRepository` reúne `LecturasDao` y `FuenteRemota`. `di/Modulos.kt` conecta Hilt, base y cliente; `LecturasApplication` y el manifiesto preparan el arranque.

Una relación 1:N une autores y lecturas; una N:M une lecturas y etiquetas. `LecturasDatabase.MIGRACION_1_2` añade la marca de lectura sin recrear la base. `app/schemas` contiene el esquema exportado actual; la prueba construye explícitamente el esquema anterior en una base temporal.

## Trabajo de la práctica

La solución resuelve los cinco TODO. Las clases instrumentadas están activas. Separa esta carpeta del inicial para conservar tu implementación.

Ambas etapas tienen el mismo applicationId: instalar otra sustituye la anterior. No borres datos para comprobar una migración. Los archivos privados desaparecen al desinstalar o borrar datos, que son operaciones distintas de reiniciar un proceso.

## Dependencias verificables

Stack común del curso: Kotlin 2.2.21, AGP 9.2.1, KSP 2.3.4, Room 2.8.4 y Compose BOM 2025.12.00. Esta ampliación añade Hilt 2.59.2 y DataStore 1.2.1, fijados en `gradle/libs.versions.toml`. Consulta [Hilt](https://developer.android.com/training/dependency-injection/hilt-android), [procesamiento KSP](https://dagger.dev/dev-guide/ksp.html) y [DataStore 1.2.1](https://developer.android.com/jetpack/androidx/releases/datastore#1.2.1). Hilt y Room usan KSP; no se mezcla con KAPT.

## Pruebas incluidas

- JVM: alternancia conserva el ID y produce el estado opuesto.
- Android: ambas relaciones, etiquetas compartidas y agregados; actualización conserva marcas y caché ante fallo; preferencia observada después de escribir; migración desde SQLite versión 1 con datos.
- UI Compose: carga local, marcado, filtro y recreación de Activity. Esta prueba reinicia la base `lecturas.db` del laboratorio instalado; no ejecutarla sobre datos que quieras conservar.
- La API de las pruebas es controlada. El acceso al servicio externo, la UI y el reinicio real deben comprobarse aparte; consultar el estado global del repositorio para los resultados realmente ejecutados.

Desde esta carpeta, comprobación técnica opcional:

```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:lintDebug --console=plain
.\gradlew.bat :app:connectedDebugAndroidTest --console=plain
```

Código original y wrapper bajo Apache-2.0; LICENSE y avisos incluidos. Las bibliotecas mantienen sus licencias. No se necesitan claves ni cuentas; no introducir datos personales en el servicio público.
