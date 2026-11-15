"""Servidor propio del laboratorio Ktor. Sin consultas externas ni datos personales."""
from http.server import BaseHTTPRequestHandler, HTTPServer
from urllib.parse import urlparse, parse_qs
import json

class Respuestas(BaseHTTPRequestHandler):
    def do_GET(self):
        url = urlparse(self.path)
        opciones = parse_qs(url.query)
        codigo = 200
        if url.path != "/mensajes":
            codigo, datos = 404, {"error":"Recurso desconocido"}
        elif "error" in opciones:
            codigo, datos = 503, {"error":"Servicio no disponible"}
        elif "roto" in opciones:
            datos = {"mensajes":"incompatible"}
        elif "vacio" in opciones:
            datos = {"mensajes":[],"siguiente":None}
        else:
            datos = {"mensajes":[{"id":1,"texto":"Preparar laboratorio","leido":False},
                {"id":2,"texto":"Revisar conexión","leido":True},
                {"id":3,"texto":"Registrar resultado","leido":False}],"siguiente":None}
        cuerpo=json.dumps(datos, ensure_ascii=False).encode("utf-8")
        self.send_response(codigo); self.send_header("Content-Type","application/json; charset=utf-8")
        self.send_header("Content-Length",str(len(cuerpo))); self.end_headers(); self.wfile.write(cuerpo)
    def log_message(self, formato, *args): pass

if __name__ == "__main__":
    print("Mensajes del laboratorio: http://127.0.0.1:8092/mensajes; Ctrl+C para detener.")
    HTTPServer(("127.0.0.1",8092),Respuestas).serve_forever()
