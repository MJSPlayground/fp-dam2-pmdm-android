"""Prueba observable del contrato de red que consume el proyecto Cuaderno."""
import importlib.util
import json
from pathlib import Path
from threading import Thread
from urllib.error import HTTPError
from urllib.request import urlopen
from http.server import ThreadingHTTPServer
import unittest

SOURCE = Path(__file__).resolve().parent.parent / "proyectos/desarrollo/servidor-pruebas/servidor.py"
spec = importlib.util.spec_from_file_location("lan", SOURCE)
lan = importlib.util.module_from_spec(spec)
spec.loader.exec_module(lan)


class ContractTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.server = ThreadingHTTPServer(("127.0.0.1", 0), lan.Handler)
        cls.thread = Thread(target=cls.server.serve_forever, daemon=True)
        cls.thread.start()
        cls.base = f"http://127.0.0.1:{cls.server.server_port}"

    @classmethod
    def tearDownClass(cls):
        cls.server.shutdown()
        cls.server.server_close()
        cls.thread.join(timeout=5)

    def test_posts_decodable_with_android_dto_fields(self):
        with urlopen(self.base + "/posts", timeout=5) as response:
            self.assertEqual(response.status, 200)
            self.assertIn("charset=utf-8", response.headers["Content-Type"])
            notes = json.load(response)
        self.assertEqual(len(notes), 2)
        self.assertEqual(len({note["id"] for note in notes}), 2)
        for note in notes:
            self.assertIsInstance(note["id"], int)
            self.assertIsInstance(note["title"], str)
            self.assertIsInstance(note["body"], str)
            self.assertTrue(note["title"].strip())

    def test_failure_routes_preserve_http_status(self):
        for path, status in (("/error", 503), ("/missing", 404), ("/posts?delay=bad", 400)):
            with self.subTest(path=path):
                with self.assertRaises(HTTPError) as error:
                    urlopen(self.base + path, timeout=5)
                self.assertEqual(error.exception.code, status)
                self.assertIn("error", json.load(error.exception))

    def test_empty_and_incompatible_are_successful_http_responses(self):
        with urlopen(self.base + "/empty", timeout=5) as response:
            self.assertEqual(response.status, 200)
            self.assertEqual(json.load(response), [])
        with urlopen(self.base + "/invalid", timeout=5) as response:
            self.assertEqual(response.status, 200)
            payload = json.load(response)
            self.assertIsInstance(payload[0]["id"], str)


if __name__ == "__main__":
    unittest.main()
