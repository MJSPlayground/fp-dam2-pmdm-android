# Repaso de multimedia avanzada

1. ¿Qué responsabilidad nueva asume nuestra app al integrar CameraX en lugar de delegar en TakePicture? ¿Qué piezas del visor y la captura son distintas?
2. `prepareRecording` devuelve un objeto. ¿El vídeo ya está grabando? ¿Qué evento permite anunciar que el archivo terminó?
3. Un móvil admite Preview+ImageCapture y Preview+VideoCapture, pero falla con los tres a la vez. ¿Es suficiente para afirmar que la biblioteca no funciona?
4. Una foto se muestra cuadrada después de forzar su ancho y alto, pero las caras se deforman. Distingue recorte, escalado y proporción.
5. Guardar PNG con calidad100, ¿equivale a una fotografíaJPEG sin pérdida? ¿Por qué la extensión no demuestra el formato?
6. Una app receptora no abre un vídeo privado. Enumera contratos que revisarías antes de pedir acceso general a archivos.
7. ¿Por qué bajar a cero el volumen del reproductor no elimina la pista de audio del MP4 exportado?
8. Transformer se cancela y el archivo todavía existe. ¿Es necesariamente un fallo de la biblioteca? ¿Qué debe hacer el propietario con su salida parcial?
9. `SoundPool.load` devuelve5 y `play` devuelve0. ¿Qué significan esos dos valores y qué puedes afirmar sobre salida audible?
10. Convierte a hexadecimal los bytes de una nota MIDI con canal 2, nota 60 y velocidad 100. ¿Qué componente falta para producir un timbre audible?
11. Un archivo local muestra estimación de red cero. ¿Se ha medido una red de capacidad cero? ¿Son frames descartados y paquetes perdidos la misma métrica?
12. Una dirección de una página de YouTube se entrega a ExoPlayer. ¿Por qué no debe tratarse como una URL directa de vídeo? Compara enlace e IFrame.
13. Llegan secuencias RTP 100,101,103,102. ¿Qué diferencia hay entre detectar un hueco y confirmar una pérdida? ¿El timestamp expresa siempre milisegundos?
14. Explica qué administran RTSP, RTP y RTCP. ¿Por qué utilizarTCP puede facilitar la conectividad y a la vez retrasar contenido posterior a una pérdida?
15. Diseña una evidencia de cámara virtual, otra de transformación real de archivo y otra que necesite dispositivo físico. No uses «compila» como resultado funcional.

[Soluciones razonadas](soluciones.md).
