// Valores por defecto de useAuth() para las pruebas.
export function crearAuth(overrides = {}) {
  const roles = overrides.roles ?? [];
  return {
    account: null,
    isAuthenticated: false,
    ready: true,
    name: '',
    email: '',
    roles,
    scopes: [],
    idClaims: {},
    accessClaims: null,
    hasRole: (r) => roles.includes(r),
    hasAnyRole: (lista) => lista.some((r) => roles.includes(r)),
    isAdmin: roles.includes('ADMIN'),
    login: jest.fn(() => Promise.resolve()),
    logout: jest.fn(),
    ...overrides,
  };
}
