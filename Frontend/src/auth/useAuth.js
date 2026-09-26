import { useContext } from 'react';
import { AuthContext } from './AuthContext';

// Hook para leer la sesión: cuenta, nombre, correo, roles, scopes, login() y logout().
export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) {
    throw new Error('useAuth debe usarse dentro de <AuthProvider>');
  }
  return ctx;
}
