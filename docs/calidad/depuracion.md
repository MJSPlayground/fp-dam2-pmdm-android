# Depuración, inspectores y Crashlytics

## De una incidencia a una causa comprobable

Empieza por un recorrido reproducible: versión, dispositivo, entrada, acciones, resultado esperado y observado. Después busca la primera causa útil de Logcat o del informe, no el último mensaje derivado. Un breakpoint detiene en una línea y permite inspeccionar datos y pila; no debe quedarse en «aquí parece entrar» sin contrastar el valor que toma cada rama.

En Lista de revisión puedes poner un breakpoint en `cargar`, observar cómo se cancela el `Job` previo y seguir el cambio de `RevisionState`. Si la lista muestra datos antiguos, comprueba qué llamada publicó el estado al final. No añadas registros con títulos personales, tokens o respuestas completas solo para saber que una función se ejecutó.

## Layout Inspector y recomposiciones

Con la app Debug ejecutándose, abre **Running Devices** y activa **Layout Inspector** sobre el proceso. Selecciona un componente para observar jerarquía y medidas; desactiva la inspección profunda cuando quieras interactuar normalmente con la app. La [guía oficial](https://developer.android.com/studio/debug/layout-inspector) describe los controles de la versión actual del IDE.

Para Compose, los contadores de recomposición y omisión ayudan a localizar trabajo repetido. Un contador alto no demuestra lentitud por sí solo: un composable pequeño puede recomponerse rápidamente y un cálculo pesado bloquear con pocas ejecuciones. Reproduce una interacción, mide y comprueba que la optimización cambia el coste sin alterar el resultado.

La [guía de depuración Compose](https://developer.android.com/develop/ui/compose/tooling/debug) conecta estas herramientas con el código. No elimines estado necesario para reducir un número del inspector; mueve cálculos o lecturas cuando exista evidencia de que es la causa.

## Database Inspector con un proyecto que realmente use Room

Lista de revisión no tiene base de datos: su Repository es local en memoria. Para practicar el inspector utiliza la [solución de Cuaderno](../desarrollo/proyecto.md), que sí incluye Room, sin modificar su arquitectura.

Ejecuta Cuaderno Debug en API 26 o superior, crea una nota y abre **View → Tool Windows → App Inspection → Database Inspector**. Selecciona su proceso y la base; abre la tabla y contrasta ID, título y contenido con la pantalla. Después edita desde la app y actualiza la vista del inspector. Una consulta de lectura permite distinguir «la UI no se actualiza» de «el dato nunca se guardó».

El inspector también puede modificar datos; para aprender, utiliza una copia de laboratorio y evita cambios que invaliden las invariantes que la aplicación normalmente aplica. Consulta la [documentación del inspector](https://developer.android.com/studio/inspect/database) para compatibilidad y comportamiento de consultas. Una captura del inspector no demuestra por sí sola que la app sobreviva a una reinstalación o migración.

## Crashlytics: informes de instalaciones externas

Logcat requiere acceso al dispositivo. Crashlytics recoge y agrupa fallos de instalaciones conectadas a un proyecto Firebase. Es una integración externa opcional: necesita cuenta, registro de la aplicación y una decisión sobre datos y recogida. El laboratorio no activa ese servicio ni envía informes.

El procedimiento sobre una **copia destinada a ese proyecto Firebase** es:

1. Registra el `applicationId` correcto; Debug tiene sufijo `.debug` y no es la misma identidad que Release.
2. Obtén la configuración Android del proyecto y colócala en el módulo/variante apropiado. No introduzcas cuentas de servicio, claves privadas o credenciales administrativas en el cliente.
3. Añade los plugins Google Services y Crashlytics, y la biblioteca `firebase-crashlytics` mediante la BOM Firebase compatible fijada en el catálogo de esa integración. Contrasta versiones y requisitos con la [guía oficial Android](https://firebase.google.com/docs/crashlytics/android/get-started); los alias del laboratorio no incluyen esos plugins porque no se ejecuta Firebase.
4. Decide la política de recogida y documenta la información que llega. Si debe ser por consentimiento, empieza con recogida automática desactivada mediante la configuración documentada y actívala únicamente tras esa decisión.
5. En una compilación de pruebas aislada, genera el fallo de prueba que documenta el servicio, reinicia y comprueba que aparece en la consola con la versión correcta. Retira el disparador antes de distribuir.

Una vez instalado y configurado el SDK, este fragmento registra un fallo no fatal esperado:

```kotlin
FirebaseCrashlytics.getInstance().recordException(fallo)
```

`fallo` es la excepción que ya has tratado. Es un fragmento de la integración configurada, no código incluido en la app sin Firebase. No informes cada validación de formulario como una excepción: generaría ruido y costes de investigación. Evita asociar correos, coordenadas o texto de notas a claves y registros de diagnóstico.

Con R8 hay nombres ofuscados: el servicio necesita la información de mapeo de **esa compilación** para interpretar la pila. Configurar el plugin no demuestra que un informe haya llegado ni que el mapeo sea correcto. La comprobación requiere la consola y el proyecto remoto, y permanece pendiente mientras no se realice. La [personalización de informes](https://firebase.google.com/docs/crashlytics/customize-crash-reports?platform=android) explica recogida, claves y registros.
