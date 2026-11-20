# Notas en Firebase local — solución

Abre esta carpeta en Android Studio, sincroniza y ejecuta **app** en emulador
API26 o superior, con SDK 37 y el JBR compatible. Solo Debug habilita el laboratorio.

Primero inicia Emulator Suite en la subcarpeta `emuladores`. Necesita Node22 y Java 25:

```powershell
npm ci
npm start
```

No requiere login Firebase ni proyecto real. La UI está en http://127.0.0.1:4005
y los servicios escuchan solo en loopback. Android usa 10.0.2.2: Auth9099,
Firestore8085 y Storage9199. No es configuración para teléfono físico ni producción.
Utiliza cuentas locales de prueba; detener sin exportar elimina los datos del servidor.

Solución de los dos TODO: elimina la nota seleccionada y descarga el archivo privado
con límite de bytes. Incluye reglas de propiedad y límites de tamaño y MIME.

Pruebas técnicas: `gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:lintDebug`.
En emuladores, `npm test` verifica reglas, usuarios, tiempo real, archivos y Auth.
No ejecutes `npm start` y `npm test` a la vez.

Código Apache-2.0; LICENSE y avisos del Wrapper incluidos.
