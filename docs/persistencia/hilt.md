# Inyección de dependencias con Hilt

## Recibir colaboradores en lugar de fabricarlos

En Cuaderno un contenedor construía base, cliente y repositorio. Esa construcción manual sigue siendo válida para aplicaciones pequeñas. Cuando varias clases comparten dependencias, Hilt puede generar la conexión y comprobar durante compilación que existe una forma de construir cada objeto.

**Inversión de control** significa que una clase no dirige la creación de todos sus colaboradores. **Inyección por constructor** significa recibirlos como argumentos. Hilt automatiza parte de ese trabajo; no decide las reglas de negocio ni vuelve innecesarios los constructores.

El proyecto conserva Kotlin 2.2.21, AGP 9.2.1, KSP 2.3.4 y Room 2.8.4 de la configuración común. Añade Hilt 2.59.2 mediante el plugin `com.google.dagger.hilt.android`, biblioteca `hilt-android` y procesador `hilt-compiler` con KSP. No mezcla KAPT y KSP para generar tipos que deban colaborar. Consulta la [configuración oficial](https://dagger.dev/hilt/gradle-setup.html) y las [limitaciones del procesamiento KSP](https://dagger.dev/dev-guide/ksp.html).

Estas versiones se fijan en `gradle/libs.versions.toml` de ambos proyectos. La versión de plugin, biblioteca y procesador de Hilt coincide. Una versión nueva de una dependencia no implica que se pueda cambiar una sola entrada sin comprobar toda la combinación.

## Application y puntos de entrada

`LecturasApplication` lleva `@HiltAndroidApp`. Esto prepara el contenedor de la aplicación y su código generado. El manifiesto debe declarar `android:name=".LecturasApplication"`: crear una clase anotada sin hacer que Android la utilice no conecta el arranque.

`MainActivity` lleva `@AndroidEntryPoint`. Ese punto de entrada permite obtener dependencias para componentes Android admitidos. No se coloca esta anotación sobre todos los composables ni convierte cualquier clase en una Activity.

La Activity obtiene su ViewModel con `by viewModels()`. Como el propietario es un punto de entrada Hilt y el ViewModel está registrado, la factoría generada puede construirlo. En esta aplicación no hace falta añadir una segunda biblioteca de navegación para obtener ese objeto.

## Constructores con Inject

Una clase Kotlin propia puede indicar cómo construirla mediante `@Inject constructor`. En `FuenteRemota` el argumento es `LecturasApi`. Hilt debe saber primero cómo obtener esa API; la anotación no inventa un servidor ni una instancia de Retrofit.

```kotlin title="Fragmento de clase construible por Hilt"
class FuenteRemota @Inject constructor(private val api: LecturasApi)
```

`Inject` procede de `javax.inject`. Si una dependencia no puede resolverse, la compilación muestra el camino hasta el consumidor. Crear otro objeto manualmente para silenciar el error puede originar dos bases o dos repositorios en lugar de reparar la conexión.

`@Singleton` conserva una instancia dentro del componente de aplicación. No hace persistente su memoria después de terminar el proceso: para eso siguen existiendo Room y DataStore. Tampoco es un singleton universal entre todas las instalaciones o procesos posibles.

## Provides: objetos de bibliotecas

No podemos modificar el constructor de `RoomDatabase` generado ni el de una interfaz Retrofit para añadir `@Inject`. El módulo `Recursos` usa funciones `@Provides`: construye la base mediante `Room.databaseBuilder` y la API mediante `Retrofit.Builder`.

```kotlin title="Fragmento del módulo de recursos"
@Provides @Singleton
fun base(@ApplicationContext contexto: Context): LecturasDatabase =
    Room.databaseBuilder(contexto, LecturasDatabase::class.java, "lecturas.db")
        .addMigrations(LecturasDatabase.MIGRACION_1_2).build()
```

`@ApplicationContext` distingue el contexto que debe vivir con la aplicación. El método pertenece a un objeto anotado `@Module` y `@InstallIn(SingletonComponent::class)`. La anotación de instalación indica qué componente recibe sus declaraciones; sin ella Hilt no utiliza el módulo como si estuviera registrado.

En el archivo completo, `Provides` y `Module` son de `dagger`; las anotaciones de instalación y componentes pertenecen a `dagger.hilt`. No construimos una base dentro de cada pantalla ni registramos varias para el mismo contrato sin distinguirlas.

## Binds: elegir una implementación

El dominio pide `LecturasRepository`, que es una interfaz. El módulo abstracto `Enlaces` utiliza `@Binds` para indicar que debe entregarse `DatosRepository`, cuya construcción ya conoce Hilt por su constructor.

```kotlin title="Método del módulo Enlaces"
@Binds
abstract fun repository(implementacion: DatosRepository): LecturasRepository
```

`@Binds` conecta un tipo con otro compatible; `@Provides` ejecuta código de construcción. No se escribe un cuerpo en este método abstracto. El módulo lleva también `@Module` y `@InstallIn`.

Si hubiera dos implementaciones para el mismo tipo, necesitaríamos elegir mediante calificadores u otra decisión explícita. No añadimos esa infraestructura al laboratorio porque solo tenemos una elección real de producción.

## ViewModels y pruebas

`LecturasViewModel` declara `@HiltViewModel` y un constructor `@Inject`. Su duración pertenece al propietario del ViewModel, no al singleton de la base. La [integración de Hilt con ViewModel](https://developer.android.com/training/dependency-injection/hilt-jetpack) explica esa distinción.

Las pruebas locales construyen el caso de uso directamente con un repositorio de prueba. Las pruebas de Room construyen una base temporal y un repositorio real con una API controlada. No necesitan arrancar Hilt para todo: probar la lógica con colaboradores pequeños también comprueba que la inyección por constructor es útil.

Al ejecutar la app sí se comprueba el grafo generado y el arranque Android. Una compilación correcta acredita generación y tipos; no sustituye las pruebas de UI, migraciones ni conservación de datos.
