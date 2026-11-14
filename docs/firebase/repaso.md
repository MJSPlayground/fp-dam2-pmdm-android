# Consolidación y repaso

## Archivo privado de notas

Un grupo de lectura necesita un archivo personal: cada participante inicia sesión, añade y elimina sus notas y conserva una imagen propia. Las notas deben actualizarse cuando cambian en el servidor y la aplicación debe ocultarlas al cerrar sesión. Ninguna cuenta puede leer o escribir los recursos de otra. Una imagen excesiva o incompatible debe rechazarse y una descarga fallida debe permitir reintentar.

Parte del proyecto inicial y completa ese comportamiento sin cambiar las reglas por acceso público. Comprueba dos cuentas, un fallo de autenticación, una entrada vacía, un archivo excesivo, un cambio externo observado y cierre de sesión. El registro debe distinguir resultados contra Emulator Suite y servicios reales; no se exige activar estos últimos. La solución está separada al final.

## Preguntas razonadas

1. ¿En qué se diferencian Authentication, Firestore y Storage? ¿Dónde guardarías una fotografía y dónde sus metadatos?
2. La app oculta notas de otras cuentas mediante un filtro Kotlin. ¿Eso protege el servidor?
3. ¿Por qué se utiliza UID y no el texto libre del correo para determinar propiedad?
4. ¿Crear una cuenta e iniciar sesión son la misma operación? ¿Qué sucede con sus datos al cerrar sesión?
5. Un listener muestra una nota nueva antes de confirmarla el servidor. ¿Qué podría estar ocurriendo?
6. ¿Por qué registrar `addSnapshotListener` en cada recomposición provoca problemas? ¿Cómo se retira?
7. ¿Qué diferencia hay entre `add`, `set`, `update` y `delete`? ¿Borrar un padre elimina todos sus archivos y subcolecciones?
8. ¿Por qué limitar un archivo comprimido a 1 MiB no basta para evitar problemas de memoria al dibujarlo?
9. Un objeto declara MIME `image/png`, pero sus bytes no se decodifican. ¿Las reglas de metadatos garantizaban que fuera un PNG válido?
10. ¿Una prueba realizada con Admin SDK confirma que las reglas del cliente deniegan a un usuario ajeno?
11. ¿Por qué el teléfono físico no alcanza el ordenador con `10.0.2.2`? ¿Se debe abrir el emulador Firebase a toda la red para resolverlo sin más?
12. ¿Qué configuración falta antes de usar Google como proveedor real y qué demuestra únicamente el laboratorio local?

[Solución del proyecto — ZIP](../descargas/proyectos/firebase-solucion.zip).

[Respuestas razonadas](soluciones.md).
