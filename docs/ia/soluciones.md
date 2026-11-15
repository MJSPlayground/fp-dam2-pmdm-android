# Soluciones de IA

La [solución del cliente](../descargas/proyectos/ia-solucion.zip) valida pregunta recortada y cancela el trabajo con un estado explícito. Conserva la preparación segura del adjunto y el contrato HTTP. El servidor incluido usa simulación por defecto; no contiene respuestas inventadas presentadas como inferencia remota.

1. La generación construye una salida que puede contener errores; una consulta de base de datos recupera datos existentes según su contrato. Ninguna interfaz sustituye verificar la fuente y el significado de sus resultados.
2. No. Telemetría, sincronización o peticiones de la app podrían enviar información aunque la inferencia sea local. Se revisa todo el flujo.
3. Depende de hardware, versión, recursos y APIs soportadas. Hay que comprobar disponibilidad y preparación; un doble de pruebas no acredita ejecución en AICore.
4. No. El APK puede inspeccionarse. Las credenciales del proveedor se mantienen en un entorno de servidor o se utiliza un servicio de cliente con mecanismos de protección apropiados.
5. Separa credenciales y permite adaptar límites y proveedor. Para producción faltan autenticación, cuotas por identidad, operación mantenida, protección y retención de datos, entre otros requisitos de ese producto.
6. Base64 solo representa bytes como texto. Recodificar retira metadatos del original, pero un rostro o un documento siguen visibles y pueden identificar a alguien.
7. La entrada anterior no debe reemplazar la selección nueva ni volver tras Quitar. Una prueba puede controlar el orden de dos lecturas demoradas y verificar qué adjunto termina seleccionado, sin inferencia.
8. No. El trabajo de red bloqueante puede continuar hasta su timeout y el servidor puede haber recibido ya la solicitud. Cancelar la espera del cliente no es una garantía de cancelación o devolución de cuota del proveedor.
9. Indica una salida de demostración del circuito. Comprueba contrato y estados, sin comprender la imagen ni evaluar el modelo.
10. No. Se comunica resultado no disponible y una acción posible. La app no debe sustituirlo por una respuesta que parezca generada correctamente.
11. Modelo, configuración, fecha, tipo de entrada permitida, resultado esperado y criterios de revisión. Los datos personales no se añaden al repositorio ni se hacen públicos en un informe.
12. Puede repetirse al recomponer y multiplicar envíos y coste. Se inicia por un evento o efecto deliberado con propietario y cancelación definidos; el ViewModel expone el resultado como estado.
