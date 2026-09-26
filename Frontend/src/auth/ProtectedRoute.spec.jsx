import React from 'react';
import { render, screen } from '@testing-library/react';
import ProtectedRoute from './ProtectedRoute';
import { useAuth } from './useAuth';
import { crearAuth } from '../../test/authMock';

jest.mock('./useAuth');

describe('ProtectedRoute (guard)', () => {
  test('sin sesión redirige al login de Azure AD', () => {
    const auth = crearAuth({ isAuthenticated: false });
    useAuth.mockReturnValue(auth);
    render(<ProtectedRoute><p>Contenido privado</p></ProtectedRoute>);
    expect(auth.login).toHaveBeenCalledTimes(1);
    expect(screen.queryByText('Contenido privado')).not.toBeInTheDocument();
  });

  test('mientras MSAL procesa la sesión no redirige', () => {
    const auth = crearAuth({ ready: false });
    useAuth.mockReturnValue(auth);
    render(<ProtectedRoute><p>Contenido privado</p></ProtectedRoute>);
    expect(auth.login).not.toHaveBeenCalled();
    expect(screen.getByText(/cargando sesión/i)).toBeInTheDocument();
  });

  test('con sesión muestra el contenido', () => {
    useAuth.mockReturnValue(crearAuth({ isAuthenticated: true, roles: ['CLIENTE'] }));
    render(<ProtectedRoute><p>Contenido privado</p></ProtectedRoute>);
    expect(screen.getByText('Contenido privado')).toBeInTheDocument();
  });

  test('sin el rol requerido muestra acceso denegado', () => {
    useAuth.mockReturnValue(crearAuth({ isAuthenticated: true, roles: ['CLIENTE'] }));
    render(<ProtectedRoute roles={['ADMIN']}><p>Panel admin</p></ProtectedRoute>);
    expect(screen.getByText(/acceso denegado/i)).toBeInTheDocument();
    expect(screen.queryByText('Panel admin')).not.toBeInTheDocument();
  });

  test('con rol ADMIN muestra el panel', () => {
    useAuth.mockReturnValue(crearAuth({ isAuthenticated: true, roles: ['ADMIN'] }));
    render(<ProtectedRoute roles={['ADMIN']}><p>Panel admin</p></ProtectedRoute>);
    expect(screen.getByText('Panel admin')).toBeInTheDocument();
  });
});
