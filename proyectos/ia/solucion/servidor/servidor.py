"""Backend de laboratorio. Simulado por defecto; --real habilita Gemini expresamente.

Nunca imprime cuerpos ni credenciales. Escucha solo loopback. No desplegar sin
agregar autenticación, control de cuota y política de datos propia.
"""
import argparse
import base64
import binascii
import json
import os
import re
import socket
import threading
import time
import urllib.error
import urllib.request
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer

MAX_BODY = 750_000
MAX_IMAGE = 512 * 1024


def validar(datos):
    if not isinstance(datos, dict) or not isinstance(datos.get('pregunta'), str):
        raise ValueError('Pregunta necesaria')
    pregunta = datos['pregunta'].strip()
    if not 3 <= len(pregunta) <= 2000:
        raise ValueError('Longitud de pregunta')
    imagen = datos.get('imagen')
    if imagen is not None:
        if not isinstance(imagen, str):
            raise ValueError('Imagen incompatible')
        try:
            contenido = base64.b64decode(imagen, validate=True)
        except (ValueError, binascii.Error):
            raise ValueError('Imagen incompatible') from None
        if not 0 < len(contenido) <= MAX_IMAGE or not contenido.startswith(b'\xff\xd8'):
            raise ValueError('Se admite JPEG acotado')
    return pregunta, imagen


def generar(datos, real=False):
    pregunta, imagen = validar(datos)
    if not real:
        if pregunta == '!error':
            raise RuntimeError('Fallo simulado')
        if pregunta == '!demora':
            time.sleep(2)
        return {'respuesta': 'Demostración local: se recibió ' + ('texto e imagen.' if imagen else 'una consulta de texto.'), 'simulada': True}
    # No se lee ni utiliza ninguna clave hasta que el proceso se inicie con --real.
    clave = os.environ.get('GEMINI_API_KEY', '')
    modelo = os.environ.get('GEMINI_MODEL', '')
    if not clave or not re.fullmatch(r'[a-zA-Z0-9.\-]{1,80}', modelo):
        raise RuntimeError('Falta configuración del servidor')
    partes = [{'text': pregunta}]
    if imagen:
        partes.append({'inlineData': {'mimeType': 'image/jpeg', 'data': imagen}})
    cuerpo = {'contents': [{'role': 'user', 'parts': partes}], 'generationConfig': {'maxOutputTokens': 512}}
    peticion = urllib.request.Request(
        'https://generativelanguage.googleapis.com/v1beta/models/' + modelo + ':generateContent',
        data=json.dumps(cuerpo).encode('utf-8'),
        headers={'Content-Type': 'application/json', 'x-goog-api-key': clave}, method='POST',
    )
    with urllib.request.urlopen(peticion, timeout=20) as respuesta:
        raw = respuesta.read(1024 * 1024 + 1)
    if len(raw) > 1024 * 1024:
        raise RuntimeError('Respuesta excesiva')
    recibido = json.loads(raw)
    candidatos = recibido.get('candidates') or []
    if not candidatos or candidatos[0].get('finishReason') not in ('STOP', 'MAX_TOKENS'):
        raise ValueError('Sin respuesta presentable')
    texto = ''.join(p.get('text', '') for p in candidatos[0].get('content', {}).get('parts', []))
    if not texto.strip():
        raise ValueError('Respuesta vacía')
    return {'respuesta': texto[:16000], 'simulada': False,
            'incompleta': candidatos[0].get('finishReason') == 'MAX_TOKENS' or len(texto) > 16000}


class Servidor(ThreadingHTTPServer):
    real = False
    cupo = threading.BoundedSemaphore(2)


class Handler(BaseHTTPRequestHandler):
    def log_message(self, *_):
        pass

    def enviar(self, codigo, datos):
        contenido = json.dumps(datos, ensure_ascii=False).encode('utf-8')
        self.send_response(codigo)
        self.send_header('Content-Type', 'application/json; charset=utf-8')
        self.send_header('Content-Length', str(len(contenido)))
        self.end_headers()
        try:
            self.wfile.write(contenido)
        except (BrokenPipeError, ConnectionResetError):
            pass

    def do_POST(self):
        if self.path != '/generar':
            self.enviar(404, {'error': 'Recurso desconocido'}); return
        if not self.server.cupo.acquire(blocking=False):
            self.enviar(429, {'error': 'Servidor ocupado'}); return
        try:
            longitud = int(self.headers.get('Content-Length', '0'))
            if not 0 < longitud <= MAX_BODY:
                self.enviar(413, {'error': 'Entrada excesiva o vacía'}); return
            self.connection.settimeout(5)
            datos = json.loads(self.rfile.read(longitud))
            self.enviar(200, generar(datos, self.server.real))
        except (ValueError, UnicodeError):
            self.enviar(400, {'error': 'Entrada o respuesta no válida'})
        except urllib.error.HTTPError as error:
            self.enviar(429 if error.code == 429 else 502, {'error': 'Proveedor no disponible'})
        except (socket.timeout, TimeoutError):
            self.enviar(504, {'error': 'Tiempo agotado'})
        except Exception:
            self.enviar(503, {'error': 'Operación no disponible'})
        finally:
            self.server.cupo.release()


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--real', action='store_true')
    args = parser.parse_args()
    servidor = Servidor(('127.0.0.1', 8095), Handler)
    servidor.real = args.real
    print('IA local en 127.0.0.1:8095; modo ' + ('REAL: puede consumir cuota del proveedor' if args.real else 'SIMULADO: no hace inferencia ni llamadas externas'), flush=True)
    try:
        servidor.serve_forever()
    except KeyboardInterrupt:
        pass
    finally:
        servidor.server_close()
