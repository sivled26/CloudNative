import React, { useCallback, useEffect, useMemo, useState } from 'react';
import { useMsal } from '@azure/msal-react';
import { InteractionStatus } from '@azure/msal-browser';
import { jwtDecode } from 'jwt-decode';
import { AuthContext } from './AuthContext';
import { apiRequest, loginRequest, ROLES } from './authConfig';

// Expone a toda la app los datos del usuario leídos desde los claims de los tokens de Azure AD.
export default function AuthProvider({ children }) {
  const { instance, accounts, inProgress } = useMsal();
  const account = instance.getActiveAccount() ?? accounts[0] ?? null;
  const accountId = account?.homeAccountId;

  const [accessClaims, setAccessClaims] = useState(null);
  const [loadingClaims, setLoadingClaims] = useState(false);

  // Se pide el access token de la API para leer sus claims (roles, scp, aud, exp).
  useEffect(() => {
    if (inProgress !== InteractionStatus.None) return undefined;

    if (!account) {
      setAccessClaims(null);
      return undefined;
    }

    let cancelado = false;
    setLoadingClaims(true);
    instance
      .acquireTokenSilent({ ...apiRequest, account })
      .then((res) => {
        if (!cancelado) setAccessClaims(jwtDecode(res.accessToken));
      })
      .catch((err) => {
        console.warn('No se pudo obtener el access token de la API:', err);
        if (!cancelado) setAccessClaims(null);
      })
      .finally(() => {
        if (!cancelado) setLoadingClaims(false);
      });

    return () => {
      cancelado = true;
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [accountId, inProgress, instance]);

  const login = useCallback(() => instance.loginRedirect(loginRequest), [instance]);

  const logout = useCallback(
    () => instance.logoutRedirect({ account: instance.getActiveAccount() ?? undefined }),
    [instance],
  );

  const value = useMemo(() => {
    const idClaims = account?.idTokenClaims ?? {};
    const roles = Array.from(
      new Set([...(idClaims.roles ?? []), ...(accessClaims?.roles ?? [])]),
    );
    const scopes = accessClaims?.scp ? accessClaims.scp.split(' ') : [];
    const hasRole = (rol) => roles.includes(rol);

    return {
      account,
      isAuthenticated: Boolean(account),
      ready: inProgress === InteractionStatus.None && !loadingClaims,
      name: idClaims.name ?? account?.name ?? '',
      email: idClaims.preferred_username ?? idClaims.email ?? account?.username ?? '',
      roles,
      scopes,
      idClaims,
      accessClaims,
      hasRole,
      hasAnyRole: (lista) => lista.some(hasRole),
      isAdmin: hasRole(ROLES.ADMIN),
      login,
      logout,
    };
  }, [account, accessClaims, inProgress, loadingClaims, login, logout]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
