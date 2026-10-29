## Descripción
Level Up Gamer es una aplicación web compuesta por un frontend desarrollado con React y Vite y un backend encargado de la lógica y los servicios de la aplicación.

El inicio de sesión se realiza con Azure AD (Microsoft Entra ID) usando la librería MSAL. Cuando el usuario inicia sesión, el frontend obtiene un token y lo envía en cada petición al backend, el cual valida el token antes de responder.

Este proyecto corresponde a la Evaluación Parcial N°1 de Desarrollo Cloud Native I. La pauta indicaba el sistema Pedidos360 con Angular, pero el profesor nos autorizó trabajar con nuestro sistema Level Up Gamer y usar React para el frontend.

## Requisitos
Node.js (versión 20 o superior).

Java 21 (por ejemplo, Eclipse Temurin 21).

MySQL en ejecución (por ejemplo, con XAMPP), con usuario root sin contraseña y una base de datos llamada db_cloud.

Docker Desktop abierto, para ejecutar RabbitMQ.

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

Roles: ADMIN, CLIENTE y SUPERVISOR

URI de redirección: http://localhost:5173

## FrontEnd
Ubicación de la carpeta:

cd Frontend

Instalación de dependencias:

npm install

Configurar las variables de entorno:

Crear un archivo llamado .env dentro de la carpeta Frontend con los siguientes datos:

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

Supervisor: solo pueden entrar los usuarios con rol SUPERVISOR. Desde aquí se buscan usuarios y se les asigna o quita el rol ADMIN o CLIENTE. El backend hace el cambio directamente en Azure usando Microsoft Graph, por lo que necesita la variable de entorno AZURE_CLIENT_SECRET y los permisos de aplicación AppRoleAssignment.ReadWrite.All, Application.Read.All y User.Read.All con consentimiento de administrador. El rol SUPERVISOR solo se asigna desde el portal de Azure.

Si un usuario sin el rol necesario intenta entrar al panel de administrador o de supervisor, se muestra el mensaje de acceso denegado.

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

Iniciar el backend (requiere Java 21, MySQL y RabbitMQ en ejecución):

cd Backend

mvnw.cmd spring-boot:run

El backend queda disponible en http://localhost:8181. Está listo cuando aparece el mensaje "Tomcat started on port 8181".

## RabbitMQ y DLQ
El backend usa RabbitMQ para enviar y recibir mensajes. RabbitMQ se ejecuta con Docker desde la carpeta raíz del proyecto:

docker compose up -d

Panel de administración de RabbitMQ: http://localhost:15672 (usuario guest, contraseña guest).

Funcionamiento:

El Sender envía el mensaje a la cola "hello" y el Receiver lo procesa.

Si el Receiver falla, Spring reintenta el mensaje 2 veces más (3 intentos en total, esperando 1 y 2 segundos entre ellos).

Si sigue fallando, el mensaje se rechaza y RabbitMQ lo envía al exchange "hello.dlx", que lo deja en la cola "hello.dlq" (Dead Letter Queue). Así el mensaje no se pierde y queda guardado para revisarlo.

Las colas y el exchange se definen en config/RabbitMQConfig.java y los reintentos en application.properties.

Endpoints para probar:

GET http://localhost:8181/api/messages/send?message=hola : envía un mensaje que se procesa correctamente.

GET http://localhost:8181/api/messages/send?message=error : envía un mensaje que falla a propósito y termina en la DLQ.

GET http://localhost:8181/api/messages/dlq : muestra cuántos mensajes hay en la DLQ.

POST http://localhost:8181/api/messages/dlq/reprocesar : devuelve los mensajes de la DLQ a la cola "hello" para procesarlos de nuevo. Ejemplo: curl -X POST http://localhost:8181/api/messages/dlq/reprocesar

## Administrador de RabbitMQ
El backend incluye una API REST para administrar RabbitMQ: crear y eliminar colas, exchanges y bindings. La lógica está en service/RabbitAdminService.java, el controlador en controller/RabbitAdminController.java y los datos de entrada se validan con los DTO de dto/RabbitAdminDtos.java.

POST http://localhost:8181/api/rabbit/queues : crea una cola. Cuerpo: {"nombre":"notificaciones"}. Opcionales: "durable" (true por defecto), "deadLetterExchange", "deadLetterRoutingKey" y "ttlMs".

GET http://localhost:8181/api/rabbit/queues/{nombre} : muestra la cantidad de mensajes y consumidores de una cola.

DELETE http://localhost:8181/api/rabbit/queues/{nombre} : elimina una cola.

POST http://localhost:8181/api/rabbit/exchanges : crea un exchange. Cuerpo: {"nombre":"notificaciones.exchange","tipo":"direct"}. Tipos permitidos: direct, topic, fanout y headers.

DELETE http://localhost:8181/api/rabbit/exchanges/{nombre} : elimina un exchange.

POST http://localhost:8181/api/rabbit/bindings : une una cola a un exchange. Cuerpo: {"cola":"notificaciones","exchange":"notificaciones.exchange","routingKey":"nueva"}.

DELETE http://localhost:8181/api/rabbit/bindings : elimina un binding. Usa el mismo cuerpo que al crearlo.

Validaciones: los nombres son obligatorios y solo pueden contener letras, números, punto, guion y guion bajo. No se permiten nombres que empiecen con "amq." (reservados por RabbitMQ) ni eliminar las colas y exchanges que usa el sistema (hello, hello.dlx y hello.dlq). Si los datos no son válidos, la API responde 400 con el detalle de cada campo; si la cola no existe, responde 404; si RabbitMQ rechaza la operación, responde 409.

Ejemplo en Windows:

curl -X POST http://localhost:8181/api/rabbit/queues -H "Content-Type: application/json" -d "{\"nombre\":\"notificaciones\"}"

## Posibles errores
Si al volver de Microsoft no queda la sesión iniciada, revisar que en la aplicación Cloud esté configurado el scope access_as_user en la opción Exponer una API.

Si aparece que el usuario no tiene rol, revisar que esté asignado en Aplicaciones empresariales, cerrar sesión y volver a entrar.

Si no cargan los productos, revisar que el backend esté en ejecución y que la dirección en el archivo .env sea correcta.

Si el backend muestra "Connection refused" hacia localhost:5672, RabbitMQ no está en ejecución: abrir Docker Desktop y ejecutar docker compose up -d.

Si el backend muestra "PRECONDITION_FAILED" para la cola "hello", la cola existe con una configuración anterior sin DLQ: ejecutar docker restart rabbitmq y volver a iniciar el backend.

Si el backend muestra "release version 21 not supported", se está usando una versión de Java anterior a la 21.
