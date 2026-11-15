import base64
import importlib.util
import io
import json
import threading
import unittest
import urllib.error
import urllib.request
from pathlib import Path
from unittest.mock import patch

spec = importlib.util.spec_from_file_location('ia_servidor', Path(__file__).with_name('servidor.py'))
mod = importlib.util.module_from_spec(spec)
spec.loader.exec_module(mod)


class ContratoTest(unittest.TestCase):
    def test_simulada_declara_su_origen(self):
        self.assertTrue(mod.generar({'pregunta':'Consulta de prueba'})['simulada'])
        imagen = base64.b64encode(b'\xff\xd8\xff\xd9').decode()
        self.assertIn('imagen', mod.generar({'pregunta':'Describe', 'imagen':imagen})['respuesta'])

    def test_limites_y_formato(self):
        for datos in ({}, {'pregunta':'a'}, {'pregunta':'a'*2001}, {'pregunta':'Válida','imagen':'???'}, {'pregunta':'Válida','imagen':base64.b64encode(b'no-jpeg').decode()}):
            with self.assertRaises(ValueError): mod.validar(datos)

    def test_contrato_remoto_sin_llamada_real(self):
        respuesta = io.BytesIO(json.dumps({'candidates':[{'finishReason':'STOP','content':{'parts':[{'text':'Salida de prueba'}]}}]}).encode())
        with patch.dict('os.environ', {'GEMINI_API_KEY':'valor-solo-de-prueba','GEMINI_MODEL':'modelo-de-prueba'}), patch.object(mod.urllib.request, 'urlopen', return_value=respuesta) as abrir:
            salida = mod.generar({'pregunta':'Consulta de prueba'}, True)
            self.assertFalse(salida['simulada'])
            peticion = abrir.call_args.args[0]
            self.assertNotIn('valor-solo-de-prueba',peticion.full_url)
            self.assertEqual(json.loads(peticion.data)['generationConfig']['maxOutputTokens'],512)

    def test_http_real_loopback(self):
        servidor = mod.Servidor(('127.0.0.1',0),mod.Handler)
        hilo = threading.Thread(target=servidor.serve_forever,daemon=True);hilo.start()
        base=f'http://127.0.0.1:{servidor.server_port}'
        try:
            peticion=urllib.request.Request(base+'/generar',data=json.dumps({'pregunta':'Prueba local'}).encode(),headers={'Content-Type':'application/json'})
            with urllib.request.urlopen(peticion) as respuesta:
                self.assertTrue(json.load(respuesta)['simulada'])
            for ruta, cuerpo, codigo in [('/desconocido',{'pregunta':'Texto'},404),('/generar',{'pregunta':'x'},400),('/generar',{'pregunta':'!error'},503)]:
                with self.assertRaises(urllib.error.HTTPError) as fallo:
                    urllib.request.urlopen(urllib.request.Request(base+ruta,data=json.dumps(cuerpo).encode()))
                self.assertEqual(fallo.exception.code,codigo)
        finally:
            servidor.shutdown();servidor.server_close()


if __name__=='__main__':unittest.main()
