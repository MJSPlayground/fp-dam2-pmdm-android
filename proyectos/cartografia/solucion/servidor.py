"""Servidor local de respuestas sintéticas. No consulta proveedores cartográficos."""
from http.server import BaseHTTPRequestHandler, HTTPServer
from urllib.parse import urlparse, parse_qs
import json

class Respuestas(BaseHTTPRequestHandler):
    def do_GET(self):
        ruta = urlparse(self.path)
        consulta = parse_qs(ruta.query).get("q", [""])[0]
        if ruta.path == "/search":
            if consulta == "ERROR":
                self.enviar(503, {"error": "Servicio de prueba no disponible"}); return
            if consulta == "ROTO":
                self.enviar(200, {"contrato": "incompatible"}); return
            lugares = [{"lat": "40.4138", "lon": "-3.6921", "display_name": "Museo del laboratorio"}] if consulta == "Museo" else []
            self.enviar(200, lugares)
        elif ruta.path.startswith("/route/v1/driving/"):
            # Geometría propia y sintética: no representa una ruta viable por calles.
            self.enviar(200, {"code": "Ok", "routes": [{"geometry": {"type": "LineString", "coordinates": [[-3.7038,40.4168],[-3.7000,40.4188],[-3.6900,40.4200]]}}]})
        else:
            self.enviar(404, {"error": "Ruta de prueba desconocida"})

    def enviar(self, codigo, datos):
        cuerpo = json.dumps(datos, ensure_ascii=False).encode("utf-8")
        self.send_response(codigo)
        self.send_header("Content-Type", "application/json; charset=utf-8")
        self.send_header("Content-Length", str(len(cuerpo)))
        self.end_headers()
        self.wfile.write(cuerpo)

    def log_message(self, formato, *args):
        pass  # No publicar consultas ni coordenadas en registros.

if __name__ == "__main__":
    print("Servidor sintético en http://127.0.0.1:8091; Ctrl+C para detener.")
    HTTPServer(("127.0.0.1", 8091), Respuestas).serve_forever()
