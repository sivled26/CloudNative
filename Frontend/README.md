# Level-Up Gamer – Frontend (React + MSAL)

Frontend de la tienda Level-Up Gamer para la Evaluación Parcial N°1 de **DSY1107 Desarrollo Cloud Native I**.
Inicia sesión con **Azure AD (Microsoft Entra ID)** usando **MSAL** y envía el JWT en cada llamada al
**API Gateway**, que reenvía al BFF y a los microservicios.

- React 19 + Vite + React Router
- `@azure/msal-browser` + `@azure/msal-react`
- Axios con interceptor que adjunta `Authorization: Bearer <access token>`
- Jest + Testing Library

## Cómo funciona la autenticación

| Pieza | Archivo | Equivalente en Angular |
| --- | --- | --- |
| Configuración MSAL (clientId, tenant, scopes) | `src/auth/authConfig.js` | `MsalModule.forRoot(...)` |
| Instancia MSAL y cuenta activa | `src/auth/msalInstance.js` | `MsalService` |
| Proveedor en la raíz | `src/main.jsx` (`<MsalProvider>`) | `MsalModule` + `MsalRedirectComponent` |
| Datos del usuario (nombre, correo, **roles**, **scopes**) | `src/auth/AuthProvider.jsx`, `useAuth()` | `MsalService.instance.getActiveAccount()` |
| Protección de rutas y roles | `src/auth/ProtectedRoute.jsx` | `MsalGuard` + guard de roles |
| Token en cada request | `src/api/AxiosConfig.js` | `MsalInterceptor` |

Flujo: el usuario pulsa **Iniciar Sesión** → `loginRedirect` a Microsoft → vuelve con ID token y access token →
el interceptor pide el token con `acquireTokenSilent` (lo renueva solo) y lo envía al API Gateway →
si la renovación silenciosa falla, se usa `acquireTokenRedirect`.

Rutas:

| Ruta | Acceso |
| --- | --- |
| `/`, `/catalogo`, `/aboutus`, `/blog`, `/resena`, `/login`, `/registro` | Pública |
| `/perfil`, `/carrito` | Requiere sesión |
| `/admin` | Requiere rol `ADMIN` |

Los roles se leen del claim `roles` (ID token y access token) y los scopes del claim `scp` del access token.
En `/perfil` se muestran rol, scopes y vencimiento del token, útil para la demo.

## Configurar Azure AD

1. **Azure Portal → Microsoft Entra ID → App registrations → New registration**
   - Nombre: `levelup-frontend`
   - Redirect URI: tipo **Single-page application (SPA)** → `http://localhost:5173`
2. En la misma app, **Expose an API** → *Add a scope* → `access_as_user`.
   El scope completo queda como `api://<client-id>/access_as_user`.
   (Si tu compañero creó una app registration aparte para la API, usa el scope de esa app.)
3. **App roles → Create app role** (tipo *Users/Groups*): `ADMIN` y `CLIENTE`.
   Si la API es otra app registration, crea los mismos roles también ahí.
4. **Enterprise applications → levelup-frontend → Users and groups**: asigna usuarios de prueba con cada rol.
5. **API permissions → Add a permission → My APIs** → el scope `access_as_user` → *Grant admin consent*.

## Ejecutar

```bash
npm install
cp .env.example .env   # completar con los datos de Azure y la URL del API Gateway
npm run dev            # http://localhost:5173
```

Variables (`.env`, no se sube a GitHub):

| Variable | Ejemplo |
| --- | --- |
| `VITE_AZURE_CLIENT_ID` | Application (client) ID de la app SPA |
| `VITE_AZURE_TENANT_ID` | Directory (tenant) ID |
| `VITE_API_SCOPE` | `api://<client-id-api>/access_as_user` |
| `VITE_API_BASE_URL` | `https://<id>.execute-api.<region>.amazonaws.com/api` |

## Scripts

| Comando | Qué hace |
| --- | --- |
| `npm run dev` | Servidor de desarrollo |
| `npm run build` | Build de producción en `dist/` |
| `npm test` | Pruebas con Jest |
| `npm run lint` | ESLint |

## Endpoints que consume el frontend

Todos a través de `VITE_API_BASE_URL` (`src/api/productosApi.js`):

| Método | Ruta | Rol |
| --- | --- | --- |
| GET | `/productos` | Público |
| POST | `/productos` | `ADMIN` |
| PUT | `/productos/{id}` | `ADMIN` |
| DELETE | `/productos/{id}` | `ADMIN` |

Si el backend responde 401 o 403, la vista muestra el mensaje correspondiente
("Tu sesión expiró…", "No tienes permisos…").
