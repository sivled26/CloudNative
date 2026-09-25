import axios from 'axios';
import { InteractionRequiredAuthError } from '@azure/msal-browser';
import { msalInstance, getCurrentAccount } from '../auth/msalInstance';
import { apiRequest } from '../auth/authConfig';
import { env } from '../config/env';

// Instancia de Axios que apunta al API Gateway (que reenvía al BFF).
const api = axios.create({
  baseURL: env.apiBaseUrl,
});

// Interceptor REQUEST (equivalente a MsalInterceptor de Angular):
// pide a MSAL un access token vigente y lo agrega como "Authorization: Bearer".
api.interceptors.request.use(async (config) => {
  const account = getCurrentAccount();
  if (!account) return config; // usuario anónimo: endpoints públicos

  try {
    const { accessToken } = await msalInstance.acquireTokenSilent({ ...apiRequest, account });
    config.headers = config.headers ?? {};
    config.headers.Authorization = `Bearer ${accessToken}`;
    return config;
  } catch (error) {
    // El refresh token expiró o falta consentimiento: se pide interacción al usuario.
    if (error instanceof InteractionRequiredAuthError) {
      await msalInstance.acquireTokenRedirect({ ...apiRequest, account });
    }
    throw error;
  }
});

// Mensajes para mostrar en pantalla según el código que devuelva el BFF.
export function mensajeDeError(error) {
  const status = error?.response?.status;
  const mensajeBackend = error?.response?.data?.message;
  switch (status) {
    case 400:
      return mensajeBackend || 'Los datos enviados no son válidos.';
    case 401:
      return 'Tu sesión expiró o el token no es válido. Vuelve a iniciar sesión.';
    case 403:
      return 'No tienes permisos para realizar esta acción.';
    case 404:
      return 'El recurso solicitado no existe.';
    default:
      if (!error?.response) return 'No se pudo conectar con el servidor.';
      return mensajeBackend || 'Ocurrió un error inesperado.';
  }
}

// Interceptor RESPONSE: agrega "userMessage" al error para que las vistas lo muestren.
api.interceptors.response.use(
  (response) => response,
  (error) => {
    error.userMessage = mensajeDeError(error);
    return Promise.reject(error);
  },
);

export default api;
