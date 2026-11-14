# Cuentas y autenticación

## Identidad frente a autorización

Autenticar confirma una identidad; autorizar decide qué puede hacer. Tras iniciar sesión, Firebase entrega un usuario con `uid`. Usaremos ese identificador en rutas privadas y las reglas verificarán que coincida con `request.auth.uid`. No se compara una contraseña dentro de un documento Firestore ni se envía el UID elegido libremente por el formulario para decidir quién puede leer.

En producción, el proveedor correo/contraseña debe habilitarse en Authentication. Conviene definir verificación del correo, política de contraseña, recuperación y tratamiento de abuso. El [flujo oficial de cuentas por contraseña](https://firebase.google.com/docs/auth/android/password-auth) documenta creación y acceso. Las contraseñas no se guardan en preferencias, notas, capturas ni logs de la aplicación.

## Crear y acceder son operaciones distintas

El SDK devuelve una tarea asíncrona. Este fragmento supone un cliente `auth` configurado y valores introducidos por la persona:

```kotlin
auth.createUserWithEmailAndPassword(correo, clave)
    .addOnCompleteListener { tarea ->
        // Representar éxito o fallo; no registrar la contraseña.
    }
```

Crear una cuenta existente puede fallar; iniciar sesión utiliza `signInWithEmailAndPassword`. La validación local evita envíos vacíos, pero el servicio mantiene su propia política. El mínimo de seis caracteres usado por la demo no debe presentarse como una recomendación general de fortaleza de contraseña.

Un fallo puede significar datos incorrectos, cuenta no disponible, limitación de solicitudes o servicio inaccesible. No todos los errores deben revelar si una dirección está registrada. La interfaz muestra un mensaje recuperable, limpia el estado de carga y permite corregir; no anuncia éxito al pulsar el botón antes de recibir respuesta.

## Seguir la sesión

`FirebaseAuth.AuthStateListener` comunica cambios de usuario. En el proyecto se asocia en `onStart` y se retira en `onStop`. Si hay usuario, la app empieza a observar su colección; al cambiar sesión, retira el listener de documentos anterior y vacía la lista y la imagen.

`signOut()` cierra la sesión local, pero no elimina la cuenta ni todos sus datos remotos. Tampoco una variable `conectado = false` invalida por sí misma la autorización del SDK. La pantalla debe reflejar el cliente de autenticación real y dejar de presentar datos del usuario anterior.

## Proveedores de identidad

Un proveedor externo, como Google, evita crear otra contraseña en nuestra app. Su flujo obtiene una credencial mediante una interfaz soportada y la entrega a Firebase. El siguiente fragmento muestra solo el intercambio final; `idToken` debe proceder del flujo de identidad autorizado, no de una cadena inventada:

```kotlin
val credencial = GoogleAuthProvider.getCredential(idToken, null)
auth.signInWithCredential(credencial)
```

`GoogleAuthProvider` pertenece a `com.google.firebase.auth`. En Android, la [guía de acceso con Google](https://firebase.google.com/docs/auth/android/google-signin) utiliza Credential Manager y explica registro OAuth, identificadores y huellas de firma. Esas configuraciones dependen de una app y un proyecto reales: el ZIP local no finge que Google ha autenticado al alumno ni contiene credenciales para hacerlo.

El correo mostrado y el proveedor no sustituyen el UID para reglas. Vincular proveedores a una misma cuenta requiere un flujo explícito para evitar crear identidades duplicadas. Cerrar una cuenta, eliminar sus documentos y retirar sus archivos son operaciones diferentes que deben diseñarse juntas cuando exista una necesidad real.
