# Servidor de pruebas de Cuaderno

Servicio HTTP de aula que devuelve dos notas con el mismo formato de `GET /posts` del ejemplo remoto. No necesita cuenta, claves ni dependencias: Python 3.11 o posterior.

Desde esta carpeta, en el ordenador que servirá las notas:

```powershell
py -3.11 servidor.py --host 0.0.0.0 --port 8080
```

En Linux/macOS utiliza `python3 servidor.py --host 0.0.0.0 --port 8080`.

Abre en ese ordenador `http://127.0.0.1:8080/posts`: deben aparecer dos objetos JSON. Para acceder desde otro dispositivo, sustituye `127.0.0.1` por la dirección IPv4 privada del ordenador servidor y conecta ambos a la misma red. El equipo servidor puede conectarse por cable; para demostrar Wi-Fi, el Android debe usar la conexión Wi-Fi y registrar la prueba entre equipos distintos.

Autoriza únicamente el acceso necesario en la red privada del aula si el firewall lo solicita; no abras puertos del router ni uses datos personales. La red del centro puede aislar clientes: consulta su configuración si el teléfono no llega aunque el servicio funcione localmente. Detén el servidor con Ctrl+C al terminar.

## Casos de prueba

- `/posts`: 200 con dos notas UTF-8.
- `/posts?delay=2`: tarda dos segundos para observar carga.
- `/error`: respuesta 503 con explicación JSON.
- `/empty`: 200 con una lista vacía, para distinguir ausencia de elementos de un error.
- `/invalid`: 200 con JSON válido, pero un identificador de texto incompatible con el DTO; debe mostrarse error de conversión.
- Cualquier ruta desconocida: 404.
- Detener el servidor: permite observar error de conexión y reintento en Android.

En un emulador Android estándar, `10.0.2.2` permite llegar al host; esa conexión virtual no demuestra comunicación inalámbrica entre dos dispositivos físicos. Una prueba local al servidor tampoco la demuestra. Registra separadamente las pruebas realizadas y la pendiente con Android real.

El servicio está acotado al aprendizaje en una LAN controlada: carece de autenticación y cifrado. La app debe utilizar HTTPS para servicios externos y restringir la excepción HTTP al build de depuración utilizado en esta práctica.

Código original bajo Apache-2.0, véase LICENSE de la raíz del repositorio.
