## Descripción
Level Up Gamer es una aplicación web compuesta por un frontend desarrollado con React y Vite y un backend encargado de la lógica y los servicios de la aplicación.

El inicio de sesión se realiza con Azure AD (Microsoft Entra ID) usando la librería MSAL. Cuando el usuario inicia sesión, el frontend obtiene un token y lo envía en cada petición al backend, el cual valida el token antes de responder.

Este proyecto corresponde a la Evaluación Parcial N°1 de Desarrollo Cloud Native I. La pauta indicaba el sistema Pedidos360 con Angular, pero el profesor nos autorizó trabajar con nuestro sistema Level Up Gamer y usar React para el frontend.

## Requisitos
Node.js (versión 20 o superior).

Tener una cuenta de Duoc asignada a la aplicación Cloud en Azure.

Tener el backend en ejecución para poder ver los productos y usar el panel de administrador.

## Estructura del proyecto
Backend: contiene los microservicios desarrollados en Java con Spring Boot.

Frontend: contiene la aplicación desarrollada en React.

## Azure
Para la autenticación se usa la aplicación registrada en Azure con el nombre Cloud. Esta aplicación es la misma para el frontend y el backend.

Tenant ID: a08a76b3-9088-491e-a89d-b08ee3f42e46

Client ID: 90a79ba8-d9c2-403c-868a-bb2ad06c5b85

Scope: api://90a79ba8-d9c2-403c-868a-bb2ad06c5b85/access_as_user

Roles: ADMIN y CLIENTE

URI de redirección: http://localhost:5173

## FrontEnd
Ubicación de la carpeta:

cd Frontend

Instalación de dependencias:

npm install

Configurar las variables de entorno:

Crear un archivo llamado .env dentro de la carpeta Frontend (se puede copiar el archivo .env.example) con los siguientes datos:

VITE_AZURE_CLIENT_ID=90a79ba8-d9c2-403c-868a-bb2ad06c5b85

VITE_AZURE_TENANT_ID=a08a76b3-9088-491e-a89d-b08ee3f42e46

VITE_API_SCOPE=api://90a79ba8-d9c2-403c-868a-bb2ad06c5b85/access_as_user

VITE_API_BASE_URL=http://localhost:8181/api

El archivo .env no se sube a GitHub. Si se cambia algún dato, hay que detener el frontend y volver a iniciarlo.

Iniciar el frontend

npm run dev

Una vez iniciado, ir a http://localhost:5173

Ejecutar las pruebas

npm test

## Inicio de sesión
Al presionar Iniciar Sesión, la aplicación redirige a la página de Microsoft para ingresar con la cuenta de Duoc. Al volver, la sesión queda iniciada y en el menú aparece el nombre del usuario.

El token se obtiene y se renueva automáticamente, y se envía en cada llamada al backend en el header Authorization.

Los roles del usuario se obtienen desde el token. En la página de perfil se puede ver el nombre, correo, rol y los permisos del token.

Al presionar Cerrar Sesión, se cierra la sesión en la aplicación y en Microsoft.

## Páginas
Inicio, Catálogo, Nosotros, Blog y Reseña: se pueden ver sin iniciar sesión.

Perfil y Carrito: es necesario iniciar sesión.

Administrador: solo pueden entrar los usuarios con rol ADMIN. Desde aquí se pueden agregar, editar y eliminar productos.

Si un usuario sin rol ADMIN intenta entrar al panel de administrador, se muestra el mensaje de acceso denegado.

## Archivos principales del frontend
src/auth/authConfig.js: configuración de MSAL con los datos de Azure.

src/auth/ProtectedRoute.jsx: protege las páginas que necesitan sesión o rol.

src/auth/AuthProvider.jsx: obtiene los datos del usuario y los roles desde el token.

src/api/AxiosConfig.js: agrega el token a cada petición que se hace al backend.

src/api/productosApi.js: contiene las llamadas a los productos.

## BackEnd
Los microservicios validan el token que envía el frontend. Para esto se configura en el application.properties:

spring.security.oauth2.resourceserver.jwt.issuer-uri=https://login.microsoftonline.com/a08a76b3-9088-491e-a89d-b08ee3f42e46/v2.0

spring.security.oauth2.resourceserver.jwt.jwk-set-uri=https://login.microsoftonline.com/a08a76b3-9088-491e-a89d-b08ee3f42e46/discovery/v2.0/keys

spring.security.oauth2.resourceserver.jwt.audiences=90a79ba8-d9c2-403c-868a-bb2ad06c5b85

Si el token no es válido o está vencido, el backend responde con error 401. Si el usuario no tiene el rol necesario, responde con error 403.

El backend debe permitir peticiones desde http://localhost:5173.

## Posibles errores
Si al volver de Microsoft no queda la sesión iniciada, revisar que en la aplicación Cloud esté configurado el scope access_as_user en la opción Exponer una API.

Si aparece que el usuario no tiene rol, revisar que esté asignado en Aplicaciones empresariales, cerrar sesión y volver a entrar.

Si no cargan los productos, revisar que el backend esté en ejecución y que la dirección en el archivo .env sea correcta.
