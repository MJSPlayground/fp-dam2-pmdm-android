# Soluciones razonadas

1. No necesariamente. TYPE_ACCELEROMETER incluye gravedad. La magnitud en reposo suele aproximarse a la aceleración gravitatoria; no es una medida certificada de calibración.
2. Aproximadamente 90 °/s. Para estimar un ángulo habría que integrar respecto al tiempo y gestionar sesgo, ruido y deriva; cambiar de unidad no hace esa integración.
3. Cada tipo tiene un contrato. Luz y presión no entregan el mismo vector que acelerómetro. Selecciona el tipo antes de acceder a índices y consulta su referencia.
4. Consume recursos y puede enviar eventos a un propietario que ya no los necesita. En este laboratorio se retira en onPause y antes de registrar otro tipo; onResume vuelve a adquirirlo.
5. No. Son magnitudes de lugares y significados distintos. Debe mostrarse ausencia de sensor ambiental y continuar usando las otras funciones.
6. No: solo se ha comprobado disponibilidad para la máscara solicitada. El éxito de onAuthenticationSucceeded confirma esa operación local.
7. No. Para relacionar autenticación con protección criptográfica se requieren claves y operaciones de Android Keystore, con el CryptoObject apropiado y una política de reautorización.
8. TakePicture delega captura en otra app y recibe el resultado autorizado. Controlar directamente el flash usa CameraManager en nuestra app y requiere sus permisos/capacidades.
9. Se puede verificar compilación, lógica pura y el recorrido de solicitud que se haya observado. La salida física y las sensaciones requieren hardware real; la ausencia de sensación virtual no prueba un defecto de código.
10. Comprobar finitud antes de operar y distinguir entrada inválida de ausencia de lectura. Las funciones puras del proyecto rechazan NaN e infinito; no deben representarse como medidas válidas.

El [proyecto solución](../descargas/proyectos/sensores-solucion.zip) tiene el mismo punto de entrada MainActivity y las mismas responsabilidades que el inicial. No contiene una lista universal de hardware presente: la aplicación lo consulta en ejecución.
