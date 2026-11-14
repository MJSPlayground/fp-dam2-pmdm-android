# Repaso de multimedia y funciones del dispositivo

Responde razonando el recorrido de los datos y el resultado visible. No hace falta memorizar todas las firmas de las APIs.

1. Una app pide acceso a todas las fotos para seleccionar una imagen. ¿Qué alternativa reduce ese acceso y qué recibe la app?
2. `TakePicture` devuelve `true`, pero el programa intenta leer el booleano como si fuera una foto. ¿Qué dato faltaba conservar y cuándo?
3. Una foto de 6000 × 4000 ocupa pocos MB en JPEG. ¿Por qué puede agotar memoria al decodificar? ¿En qué momento reducirías sus dimensiones?
4. La foto se ve derecha en la galería y tumbada en la app. ¿Qué metadato investigarías? ¿Por qué no siempre basta con girar 90 grados?
5. Una entrada de 2400 × 1200 se limita a 1024 y después se gira 90 grados. Predice dimensiones de trabajo y de exportación sin deformar.
6. El archivo se llama `resultado.jpg`, pero sus bytes son PNG. ¿Qué operación se ha omitido? ¿Cómo detectarías el fallo sin fiarte del nombre?
7. ¿Qué debe decidir la app al convertir una imagen transparente a JPEG? ¿Convertir después a PNG recupera el detalle perdido?
8. Una app receptora no puede abrir un archivo privado compartido. ¿Qué revisarías además de comprobar que el archivo existe?
9. Media3 indica READY y `isPlaying == false`. ¿Es necesariamente un error? Diferencia pausa, detención y liberación.
10. El indicador dice «Reproduciendo» inmediatamente después del clic, aunque el medio falla. ¿Qué fuente de información debería usar?
11. Un mapa carga calles, pero no aparece la posición. Propón tres causas posibles y una prueba que distinga error de cartografía de error de ubicación.
12. Un punto aparece en otra región al pasarlo de `LatLng` a GeoJSON. ¿Qué contrato puede haberse confundido?
13. Se concede `POST_NOTIFICATIONS`, pero no se ve un aviso. ¿Qué ajustes adicionales influyen y qué no demuestra la compilación?
14. Tras salir de una pantalla sigue el audio y una lectura de ubicación entrega un resultado a una vista destruida. ¿Qué responsabilidad común se ha olvidado?
15. Redacta una comprobación que se pueda repetir en emulador y otra que necesite hardware físico. Evita «comprobar que funciona».

[Soluciones razonadas](soluciones-repaso.md).
