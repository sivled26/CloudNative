// Único lugar donde se leen las variables de entorno de Vite.
// En los tests (Jest) este módulo se reemplaza por test/envMock.js.
export const env = {
  azureClientId: import.meta.env.VITE_AZURE_CLIENT_ID,
  azureTenantId: import.meta.env.VITE_AZURE_TENANT_ID,
  apiScope: import.meta.env.VITE_API_SCOPE,
  apiBaseUrl: import.meta.env.VITE_API_BASE_URL,
  redirectUri: import.meta.env.VITE_REDIRECT_URI || window.location.origin,
};
