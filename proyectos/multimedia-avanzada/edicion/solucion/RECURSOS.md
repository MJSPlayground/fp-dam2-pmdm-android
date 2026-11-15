# Recursos propios del laboratorio

`patron.png`: PNG sintético800×600 con cuadrantes verde/naranja; creado programáticamente, sin fotografías. `muestra.mp4`: patrón lavfi testsrc2 de FFmpeg con seno440Hz,320×240,12fps,2s, H.264 baseline y AAC. No contiene material de terceros. `tono.wav`: audio PCM original del laboratorio multimedia del mismo repositorio.

Generación del vídeo, FFmpeg 9.0.2-essentials_build-www.gyan.dev:

```text
ffmpeg -f lavfi -i testsrc2=size=320x240:rate=12 -f lavfi -i sine=frequency=440:sample_rate=44100 -t 2 -c:v libx264 -pix_fmt yuv420p -profile:v baseline -c:a aac -shortest -movflags +faststart muestra.mp4
```

Recursos originales distribuidos bajo Apache-2.0, como el código del ejemplo. FFmpeg se usa solo como herramienta de generación y no se incorpora como biblioteca de la app.
