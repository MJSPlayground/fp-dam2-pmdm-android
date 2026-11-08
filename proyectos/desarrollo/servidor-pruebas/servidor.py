"""Servicio de aula compatible con GET /posts de Cuaderno (solo biblioteca estándar)."""
from argparse import ArgumentParser
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
import json
from urllib.parse import parse_qs, urlparse
import time

NOTAS = [
    {"userId": 1, "id": 1, "title": "Preparar la excursión", "body": "Revisar agua, mochila y previsión del tiempo."},
    {"userId": 1, "id": 2, "title": "Comprobar la red local", "body": "Esta nota ha llegado desde el servidor de pruebas."},
]


class Handler(BaseHTTPRequestHandler):
    def do_GET(self):
        address = urlparse(self.path)
        query = parse_qs(address.query)
        try:
            delay = min(5.0, max(0.0, float(query.get("delay", ["0"])[0])))
        except ValueError:
            self.respond(400, {"error": "delay debe ser un número entre 0 y 5"})
            return
        time.sleep(delay)
        if address.path == "/posts":
            self.respond(200, NOTAS)
        elif address.path == "/empty":
            self.respond(200, [])
        elif address.path == "/invalid":
            self.respond(200, [{"id": "no-es-un-entero", "title": "Formato incompatible", "body": "Prueba de conversión"}])
        elif address.path == "/error":
            self.respond(503, {"error": "Servicio temporalmente no disponible"})
        else:
            self.respond(404, {"error": "No existe este recurso; utiliza /posts"})

    def respond(self, status, payload):
        body = json.dumps(payload, ensure_ascii=False).encode("utf-8")
        self.send_response(status)
        self.send_header("Content-Type", "application/json; charset=utf-8")
        self.send_header("Content-Length", str(len(body)))
        self.send_header("Cache-Control", "no-store")
        self.end_headers()
        try:
            self.wfile.write(body)
        except (BrokenPipeError, ConnectionResetError):
            pass  # El cliente puede abandonar la pantalla o cancelar la petición.


def main():
    parser = ArgumentParser(description=__doc__)
    parser.add_argument("--host", default="127.0.0.1", help="Para otro dispositivo de tu LAN: 0.0.0.0")
    parser.add_argument("--port", type=int, default=8080)
    args = parser.parse_args()
    server = ThreadingHTTPServer((args.host, args.port), Handler)
    print(f"Servidor en {args.host}:{server.server_port}; /posts, /posts?delay=2, /empty, /invalid y /error. Ctrl+C termina.", flush=True)
    try:
        server.serve_forever()
    except KeyboardInterrupt:
        pass
    finally:
        server.server_close()


if __name__ == "__main__":
    main()
