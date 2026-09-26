import React, { useEffect } from 'react';
import { useAuth } from './useAuth';

// Equivalente a MsalGuard de Angular + validación de roles.
// - Sin sesión: redirige al login de Azure AD y vuelve a esta misma página.
// - Con sesión pero sin el rol pedido: muestra "Acceso denegado".
export default function ProtectedRoute({ roles, children }) {
  const { isAuthenticated, ready, hasAnyRole, login } = useAuth();

  useEffect(() => {
    if (ready && !isAuthenticated) {
      login().catch((err) => console.error('Error al iniciar sesión:', err));
    }
  }, [ready, isAuthenticated, login]);

  if (!ready) {
    return <div className="error-message"><p>Cargando sesión...</p></div>;
  }

  if (!isAuthenticated) {
    return <div className="error-message"><p>Redirigiendo al inicio de sesión de Microsoft...</p></div>;
  }

  if (roles && roles.length > 0 && !hasAnyRole(roles)) {
    return (
      <div className="error-message">
        <h2>Acceso denegado</h2>
        <p>Tu cuenta no tiene el rol necesario ({roles.join(' o ')}) para ver esta página.</p>
      </div>
    );
  }

  return children;
}
