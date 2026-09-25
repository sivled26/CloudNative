import { LogLevel } from '@azure/msal-browser';
import { env } from '../config/env';

// Roles definidos como "App roles" en el App Registration de Azure AD.
export const ROLES = {
  ADMIN: 'ADMIN',
  CLIENTE: 'CLIENTE',
};

export const msalConfig = {
  auth: {
    clientId: env.azureClientId,
    authority: `https://login.microsoftonline.com/${env.azureTenantId}`,
    redirectUri: env.redirectUri,
    postLogoutRedirectUri: env.redirectUri,
  },
  cache: {
    // sessionStorage: la sesión se borra al cerrar la pestaña.
    cacheLocation: 'sessionStorage',
  },
  system: {
    loggerOptions: {
      logLevel: LogLevel.Warning,
      piiLoggingEnabled: false,
      loggerCallback: (level, message, containsPii) => {
        if (containsPii) return;
        if (level === LogLevel.Error) console.error(message);
        else if (level === LogLevel.Warning) console.warn(message);
      },
    },
  },
};

// Scopes que se piden al iniciar sesión: identidad + permiso para llamar a la API.
export const loginRequest = {
  scopes: ['openid', 'profile', 'email', env.apiScope],
};

// Scope del access token que se envía al API Gateway / BFF.
export const apiRequest = {
  scopes: [env.apiScope],
};
