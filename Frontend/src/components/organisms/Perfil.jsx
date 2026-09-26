import React from 'react';
import { useAuth } from '../../auth/useAuth';

export default function Perfil() {
  const { name, email, roles, scopes, accessClaims } = useAuth();
  const expira = accessClaims?.exp ? new Date(accessClaims.exp * 1000).toLocaleString('es-CL') : null;

  return (
    <div className="perfil-container">
      <img src="/images/gamer-icon.png" alt="Avatar" className="perfil-avatar" />
      <div className="perfil-nombre">{name || 'Usuario'}</div>
      <div className="perfil-email">{email || 'Sin correo'}</div>

      <div className="perfil-puntos">
        Rol: {roles.length > 0 ? roles.join(', ') : 'Sin rol asignado'}
      </div>

      <div className="perfil-email">
        <p>Scopes del token: {scopes.length > 0 ? scopes.join(', ') : '-'}</p>
        {expira && <p>Token válido hasta: {expira}</p>}
      </div>
    </div>
  );
}
