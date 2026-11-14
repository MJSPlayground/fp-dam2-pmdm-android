"""Recurso docente original: tono sinusoidal440Hz,3s,PCM16mono16000Hz.

Generación determinista con Python estándar. Sin muestras de terceros.
Código y recurso resultante bajo Apache-2.0 (LICENSE de cada proyecto).
"""
from pathlib import Path
import math
import struct
import wave

root = Path(__file__).resolve().parent
rate, duration = 16000, 3
samples = bytearray()
for index in range(rate * duration):
    t = index / rate
    envelope = min(1, t / 0.05, (duration - t) / 0.05)
    samples.extend(struct.pack("<h", int(0.15 * 32767 * envelope * math.sin(2 * math.pi * 440 * t))))
for stage in ("inicial", "solucion"):
    target = root / stage / "app/src/main/res/raw/tono.wav"
    target.parent.mkdir(parents=True, exist_ok=True)
    with wave.open(str(target), "wb") as output:
        output.setnchannels(1)
        output.setsampwidth(2)
        output.setframerate(rate)
        output.writeframes(samples)
    print(target)
