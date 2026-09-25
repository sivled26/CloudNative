import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import { useMsal } from '@azure/msal-react';
import AuthProvider from './AuthProvider';
import { useAuth } from './useAuth';

jest.mock('@azure/msal-react', () => ({ useMsal: jest.fn() }));

// Crea un JWT falso (sin firma real) solo para leer sus claims.
function jwt(payload) {
  const b64 = (obj) => Buffer.from(JSON.stringify(obj)).toString('base64url');
  return `${b64({ alg: 'none', typ: 'JWT' })}.${b64(payload)}.firma`;
}

function Mostrar() {
  const auth = useAuth();
  return (
    <div>
      <p>nombre:{auth.name}</p>
      <p>correo:{auth.email}</p>
      <p>roles:{auth.roles.join(',')}</p>
      <p>scopes:{auth.scopes.join(',')}</p>
      <p>admin:{String(auth.isAdmin)}</p>
    </div>
  );
}

describe('AuthProvider', () => {
  test('lee nombre, correo, roles y scopes desde los claims de los tokens', async () => {
    const account = {
      homeAccountId: 'abc',
      username: 'ana@levelup.cl',
      idTokenClaims: { name: 'Ana', preferred_username: 'ana@levelup.cl', roles: ['CLIENTE'] },
    };
    const instance = {
      getActiveAccount: () => account,
      acquireTokenSilent: jest.fn().mockResolvedValue({
        accessToken: jwt({ scp: 'access_as_user', roles: ['ADMIN'], aud: 'api://levelup-api' }),
      }),
    };
    useMsal.mockReturnValue({ instance, accounts: [account], inProgress: 'none' });

    render(<AuthProvider><Mostrar /></AuthProvider>);

    expect(screen.getByText('nombre:Ana')).toBeInTheDocument();
    expect(screen.getByText('correo:ana@levelup.cl')).toBeInTheDocument();
    await waitFor(() => expect(screen.getByText('scopes:access_as_user')).toBeInTheDocument());
    expect(screen.getByText('roles:CLIENTE,ADMIN')).toBeInTheDocument();
    expect(screen.getByText('admin:true')).toBeInTheDocument();
  });

  test('sin cuenta no pide tokens y no hay roles', () => {
    const instance = { getActiveAccount: () => null, acquireTokenSilent: jest.fn() };
    useMsal.mockReturnValue({ instance, accounts: [], inProgress: 'none' });

    render(<AuthProvider><Mostrar /></AuthProvider>);

    expect(instance.acquireTokenSilent).not.toHaveBeenCalled();
    expect(screen.getByText('admin:false')).toBeInTheDocument();
  });
});
