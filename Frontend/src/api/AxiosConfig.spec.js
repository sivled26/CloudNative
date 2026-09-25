import { InteractionRequiredAuthError } from '@azure/msal-browser';
import api, { mensajeDeError } from './AxiosConfig';
import { msalInstance, getCurrentAccount } from '../auth/msalInstance';

jest.mock('../auth/msalInstance', () => ({
  getCurrentAccount: jest.fn(),
  msalInstance: {
    acquireTokenSilent: jest.fn(),
    acquireTokenRedirect: jest.fn(),
  },
}));

const interceptorRequest = api.interceptors.request.handlers[0].fulfilled;

describe('Interceptor de Axios (MsalInterceptor)', () => {
  beforeEach(() => jest.clearAllMocks());

  test('usa la URL del API Gateway', () => {
    expect(api.defaults.baseURL).toBe('http://gateway.test/api');
  });

  test('adjunta el access token como Bearer cuando hay sesión', async () => {
    getCurrentAccount.mockReturnValue({ homeAccountId: 'abc' });
    msalInstance.acquireTokenSilent.mockResolvedValue({ accessToken: 'token-123' });

    const config = await interceptorRequest({ headers: {} });

    expect(msalInstance.acquireTokenSilent).toHaveBeenCalledWith(
      expect.objectContaining({ scopes: ['api://levelup-api/access_as_user'] }),
    );
    expect(config.headers.Authorization).toBe('Bearer token-123');
  });

  test('sin sesión no agrega Authorization', async () => {
    getCurrentAccount.mockReturnValue(null);
    const config = await interceptorRequest({ headers: {} });
    expect(config.headers.Authorization).toBeUndefined();
  });

  test('si el token no se puede renovar en silencio pide login interactivo', async () => {
    getCurrentAccount.mockReturnValue({ homeAccountId: 'abc' });
    msalInstance.acquireTokenSilent.mockRejectedValue(new InteractionRequiredAuthError('login_required'));

    await expect(interceptorRequest({ headers: {} })).rejects.toBeInstanceOf(InteractionRequiredAuthError);
    expect(msalInstance.acquireTokenRedirect).toHaveBeenCalled();
  });
});

describe('mensajeDeError', () => {
  test.each([
    [401, /sesión expiró/i],
    [403, /no tienes permisos/i],
    [404, /no existe/i],
  ])('status %i', (status, esperado) => {
    expect(mensajeDeError({ response: { status, data: {} } })).toMatch(esperado);
  });

  test('sin respuesta del servidor', () => {
    expect(mensajeDeError({})).toMatch(/no se pudo conectar/i);
  });
});
