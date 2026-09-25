import { PublicClientApplication, EventType } from '@azure/msal-browser';
import { msalConfig } from './authConfig';

// Instancia única de MSAL para toda la aplicación.
export const msalInstance = new PublicClientApplication(msalConfig);

export function getCurrentAccount() {
  return msalInstance.getActiveAccount() ?? msalInstance.getAllAccounts()[0] ?? null;
}

// Debe llamarse una vez antes de renderizar la app (ver main.jsx).
export async function initMsal() {
  await msalInstance.initialize();

  if (!msalInstance.getActiveAccount() && msalInstance.getAllAccounts().length > 0) {
    msalInstance.setActiveAccount(msalInstance.getAllAccounts()[0]);
  }

  // Al volver del login de Microsoft, se marca la cuenta como activa.
  msalInstance.addEventCallback((event) => {
    if (
      (event.eventType === EventType.LOGIN_SUCCESS ||
        event.eventType === EventType.ACQUIRE_TOKEN_SUCCESS) &&
      event.payload?.account
    ) {
      msalInstance.setActiveAccount(event.payload.account);
    }
  });
}
