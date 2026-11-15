import json
import threading
import unittest
from http.server import HTTPServer
from urllib.request import urlopen
from urllib.error import HTTPError
from servidor import Respuestas

class ServidorTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.servidor = HTTPServer(("127.0.0.1", 0), Respuestas)
        cls.hilo = threading.Thread(target=cls.servidor.serve_forever, daemon=True)
        cls.hilo.start()
        cls.base = "http://127.0.0.1:" + str(cls.servidor.server_port)

    @classmethod
    def tearDownClass(cls):
        cls.servidor.shutdown(); cls.servidor.server_close(); cls.hilo.join()

    def leer(self, ruta):
        with urlopen(self.base + ruta, timeout=3) as respuesta:
            return json.load(respuesta)

    def test_lugar(self):
        self.assertEqual("40.4138", self.leer("/search?q=Museo")[0]["lat"])
    def test_vacio(self):
        self.assertEqual([], self.leer("/search?q=Desconocido"))
    def test_error(self):
        with self.assertRaises(HTTPError) as error: self.leer("/search?q=ERROR")
        self.assertEqual(503, error.exception.code)
    def test_contrato_incompatible(self):
        self.assertNotIsInstance(self.leer("/search?q=ROTO"), list)
    def test_ruta_sintetica(self):
        ruta = self.leer("/route/v1/driving/-3.7038,40.4168;-3.69,40.42")
        self.assertEqual("LineString", ruta["routes"][0]["geometry"]["type"])
    def test_no_encontrado(self):
        with self.assertRaises(HTTPError) as error: self.leer("/inexistente")
        self.assertEqual(404, error.exception.code)

if __name__ == "__main__": unittest.main()
