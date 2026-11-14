# Actividades y repaso

## Comprobación autónoma del dispositivo

Un equipo necesita conocer qué funciones puede demostrar en un teléfono del aula. A partir del proyecto inicial, prepara una app que muestre aceleración, giro y sensores ambientales disponibles con sus unidades correctas. Debe permitir probar respuesta háptica, vibración, flash y confirmación biométrica cuando existan, sin bloquear el resto de funciones si falta una capacidad o se deniega un permiso. No guardará lecturas ni las enviará por Internet.

Entrega en tu copia un registro reproducible: modelo/API, funciones presentes, acción aplicada, cifra o efecto observado, tratamiento de ausencia y pruebas físicas pendientes. Debe dejar de escuchar y apagar flash al abandonar. No se exige seguimiento en segundo plano ni cifrado de datos privados. La solución funcional está separada al final de esta página; el inventario dependerá de tu dispositivo real.

## Interpretar y depurar

1. El acelerómetro marca aproximadamente 9,8 con el teléfono quieto. ¿Prueba un fallo de calibración?
2. El giroscopio produce 1,57 rad/s. ¿Aproximadamente cuántos grados por segundo son? ¿Qué faltaría para estimar un ángulo?
3. ¿Por qué no se puede usar el tercer componente de todos los eventos de sensores?
4. Una pantalla deja de verse, pero su listener sigue registrado. Explica consecuencias y dónde debe retirarse.
5. Un teléfono no tiene sensor de temperatura ambiente. ¿Sería correcto mostrar la temperatura de batería bajo la misma etiqueta?
6. `canAuthenticate` tiene éxito. ¿La identidad ya está confirmada? ¿Qué evento lo confirma?
7. El callback biométrico permite mostrar un texto. ¿Eso cifra un archivo? ¿Qué otro mecanismo sería necesario?
8. ¿Por qué TakePicture y controlar una linterna pueden tener necesidades de permisos diferentes?
9. El test JVM pasa, pero el vibrador del emulador no se siente. ¿Qué puedes declarar verificado?
10. Una lectura contiene NaN y se presenta como una medida normal. ¿Qué validación y estado de error ayudarían a evitarlo?

[Solución del proyecto — ZIP](../descargas/proyectos/sensores-solucion.zip).

[Respuestas razonadas](soluciones.md).
