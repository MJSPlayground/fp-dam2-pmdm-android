# Comprender un proyecto Android

## Del archivo al programa instalado

Una aplicación Android combina código, recursos y configuración. Android Studio permite verlos mediante la vista simplificada **Android** o mediante **Project**, que refleja mejor los directorios reales. Si una ruta de la explicación no coincide con la vista simplificada, cambia a Project antes de concluir que falta un archivo.

```text
mi-aplicacion/
├── settings.gradle.kts
├── build.gradle.kts
├── gradle/libs.versions.toml
├── gradlew / gradlew.bat
├── gradle/wrapper/
└── app/
    ├── build.gradle.kts
    └── src/main/
        ├── AndroidManifest.xml
        ├── java/es/mjsplayground/pmdm/ecosistema/MainActivity.kt
        └── res/values/strings.xml
```

`app` es el módulo que producirá la aplicación. Un proyecto puede tener más módulos, pero no necesitamos separarlo para una pantalla sencilla. El código Kotlin puede estar dentro de una carpeta llamada `java`; el nombre de esa carpeta no cambia el lenguaje de los archivos `.kt`.

Los recursos de `res` incluyen textos, imágenes, colores y configuraciones. `strings.xml` permite mantener textos fuera del código y preparar traducciones. El manifiesto declara componentes y características de la aplicación; no es el archivo donde dibujamos una pantalla Compose.

## Qué hacen Gradle y el plugin Android

**Gradle** organiza tareas y dependencias. El **Android Gradle Plugin (AGP)** añade las tareas propias de Android: procesar recursos, compilar, empaquetar e instalar. El plugin de Kotlin permite compilar Kotlin; el de Compose prepara la compilación de sus funciones de interfaz.

`settings.gradle.kts` identifica los módulos y repositorios de dependencias. El script de la raíz declara plugins compartidos. `app/build.gradle.kts` configura el módulo, sus niveles SDK y las bibliotecas que usa. El sufijo `.kts` indica Kotlin DSL, un lenguaje de configuración basado en Kotlin; no es código que se ejecuta dentro del teléfono.

El catálogo `gradle/libs.versions.toml` asocia nombres legibles con versiones y coordenadas de bibliotecas. Cuando un script usa `libs.androidx.activity.compose`, esa referencia se resuelve a una entrada del catálogo. No copies una versión distinta en cada práctica: consulta la configuración común.

El **Gradle Wrapper** (`gradlew`, `gradlew.bat` y `gradle/wrapper`) descarga y utiliza la distribución de Gradle fijada por el proyecto. Así dos equipos no dependen de instalaciones globales distintas. Su primera ejecución necesita conexión.

El recorrido simplificado es:

```text
Código Kotlin + recursos + manifiesto
                  ↓
       Gradle y plugins de compilación
                  ↓
             APK de depuración
                  ↓
      Instalación y ejecución en Android
```

**Sincronizar** carga la configuración y resuelve el modelo del proyecto en el IDE. **Compilar** comprueba y transforma las fuentes. **Ejecutar** añade instalación y arranque. Una sincronización correcta no demuestra que la app compile; una compilación correcta no demuestra que un botón funcione.

## MainActivity y la composición raíz

En Java ya has visto métodos que el entorno llama al producirse un evento. En Android, el sistema llama a `onCreate` cuando crea una actividad. Una **Activity** es un componente que sirve de punto de entrada a una experiencia de interfaz. Para nuestras primeras prácticas bastará una actividad.

Este fragmento muestra la estructura, sin los imports ni el tema del proyecto completo:

```kotlin title="MainActivity.kt — estructura ilustrativa"
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Text("Hola, Android")
        }
    }
}
```

`class` declara la clase y `:` expresa herencia. `override fun` indica que reemplazamos una función heredada. `super.onCreate` conserva el trabajo de la clase base. El parámetro `savedInstanceState` puede contener información previa; el signo `?` indica que también puede no existir. Aprenderemos estos elementos de Kotlin en el siguiente tema.

`setContent` establece la interfaz Compose de la actividad. Dentro se llaman funciones que describen qué mostrar, como `Text`. La primera función de pantalla que llamamos desde ahí es la **composición raíz** de nuestra interfaz. Compose puede volver a ejecutar funciones para reflejar cambios de estado; no debemos pensar en ellas como instrucciones que se ejecutan una única vez.

En el proyecto completo hay además un tema Material 3 y un `Scaffold`, que proporciona una estructura de pantalla y espacios seguros para el contenido. No elimines su relleno para pegar el texto al borde: las barras del sistema pueden ocultarlo.

La referencia de [actividades y ciclo de vida](https://developer.android.com/guide/components/activities/activity-lifecycle) explica el contrato que el sistema establece con estos componentes.

## El sistema controla la vida de la aplicación

Al abrir otra app, nuestra actividad puede dejar de estar visible. Al volver, puede continuar o crearse de nuevo. Girar el dispositivo también puede recrearla. Si Android necesita memoria, puede finalizar el proceso sin conservar sus variables.

Por tanto, hay tres cosas diferentes: que la pantalla se dibuje de nuevo, que se recree una Activity y que se reinicie el proceso. No vamos a resolver todavía la conservación de datos; por ahora observa que un valor que cambia en memoria no equivale a un dato guardado. Más adelante distinguiremos estado de interfaz y persistencia.

## El manifiesto y la pantalla de entrada

En `AndroidManifest.xml`, busca la declaración de `MainActivity` y el filtro con `MAIN` y `LAUNCHER`. Indica al sistema qué actividad puede abrirse desde el lanzador. Una actividad con un filtro de entrada debe declarar correctamente si se exporta.

No añadas permisos por anticipado. Nuestra primera pantalla no necesita Internet, ubicación ni acceso a fotos. Al utilizar una capacidad nueva aprenderemos qué permiso corresponde, dónde se declara y cuándo se solicita.
