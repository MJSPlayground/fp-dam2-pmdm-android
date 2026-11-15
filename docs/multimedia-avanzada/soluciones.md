# Soluciones razonadas

1. La app controla permiso, disponibilidad y duración de la cámara y presenta su visor. Preview produce la salida visible; ImageCapture solicita la foto; VideoCapture y Recorder administran vídeo. La vista no guarda una fotografía por existir en pantalla.
2. No: es una configuración pendiente. Start confirma el comienzo y Finalize comunica terminación y posible error. El clic y el retorno de un método no confirman un archivo completo.
3. No. Las combinaciones dependen de capacidades. El laboratorio utiliza modos separados y no exige tres casos simultáneos. Deben comprobarse cámara, proveedor, combinación y diagnóstico concreto.
4. Recortar elige región; escalar cambia dimensiones; la proporción relaciona ancho y alto. Un cuadrado se obtiene seleccionando una región apropiada, no comprimiendo cada eje de modo distinto.
5. No. PNG usa compresión sin pérdida y su parámetro de calidad no equivale al de JPEG. Un JPEG ya tiene sus pérdidas; pasarlo después a PNG no recupera detalle. El MIME y la decodificación verifican bytes, no solo nombre.
6. Autoridad y rutas del proveedor, ubicación del archivo dentro de su superficie, URI de contenido, MIME, EXTRA_STREAM, ClipData y permiso temporal. También que se comparta la salida terminada y no un archivo parcial u original equivocado.
7. El volumen solo afecta a una sesión de presentación. La edición debe retirar la pista del elemento que se exporta. El archivo se comprueba con metadatos o un extractor y se reproduce para contrastar el resultado.
8. No: cancelar no garantiza borrar la salida. El propietario elimina únicamente su archivo parcial y evita ofrecerlo como resultado válido. No debe borrar originales ni directorios completos.
9. 5 es el ID de la muestra cargada; cero indica que no se aceptó ese stream de reproducción. Incluso un stream aceptado no prueba que una persona lo haya escuchado; influyen carga terminada, salida y ajustes.
10. `92 3C 64`. El primer byte combina Note On y canal 2, y los otros expresan nota y velocidad. Falta un receptor/sintetizador o instrumento con un timbre, y un camino de envío abierto; los bytes no son una onda PCM.
11. No: puede no existir estimación útil para una entrada local. Los descartes pertenecen al renderizador; la pérdida de paquetes, al transporte. Una CPU lenta puede descartar vídeo sin pérdida de red.
12. Es una página de servicio, no un medio directo autorizado. Un Intent abre el servicio en su app o navegador; IFrame aloja su reproductor oficial con políticas, identidad y restricciones de inserción. No se extraen pistas para sortearlo.
13. La 102 llegó reordenada. Un hueco provisional no demuestra una pérdida definitiva. El timestamp sigue el reloj del códec: hace falta conocer su frecuencia para interpretar tiempo.
14. RTSP controla la sesión, RTP transporta medios temporizados y RTCP comunica informes. TCP puede atravesar determinadas redes con mayor facilidad y retransmite; una pérdida puede retener datos posteriores hasta resolverla, aumentando retardo.
15. Cámara virtual: capturar JPEG y MP4 del emulador, decodificar y leer dimensiones/duración. Transformación: exportar el clip propio a MP4 sin audio y altura 144, comprobar pista/dimensiones y abrir un frame. Física: fotografiar una referencia asimétrica en un teléfono y abrir la copia en otro receptor, registrando modelo, permiso y orientación. No se sustituyen entre sí.

Las soluciones ejecutables de los dos casos autónomos están al final de [consolidación](actividades.md); las respuestas anteriores explican conceptos y límites, sin representar una certificación de hardware.
