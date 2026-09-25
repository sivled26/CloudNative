import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import SectionLogin from '../organisms/SectionLogin';
import { useAuth } from '../../auth/useAuth';
import { crearAuth } from '../../../test/authMock';

jest.mock('../../auth/useAuth');

const renderLogin = () =>
  render(
    <MemoryRouter initialEntries={['/login']}>
      <Routes>
        <Route path="/login" element={<SectionLogin />} />
        <Route path="/" element={<p>Página inicio</p>} />
        <Route path="/admin" element={<p>Página admin</p>} />
      </Routes>
    </MemoryRouter>,
  );

describe('Login con Azure AD', () => {
  test('muestra el botón para ingresar con Microsoft', () => {
    useAuth.mockReturnValue(crearAuth());
    renderLogin();
    expect(screen.getByRole('heading', { name: /iniciar sesión/i })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /ingresar con microsoft/i })).toBeInTheDocument();
  });

  test('al hacer clic inicia el flujo de login de MSAL', () => {
    const auth = crearAuth();
    useAuth.mockReturnValue(auth);
    renderLogin();
    fireEvent.click(screen.getByRole('button', { name: /ingresar con microsoft/i }));
    expect(auth.login).toHaveBeenCalledTimes(1);
  });

  test('si ya hay sesión de cliente redirige al inicio', () => {
    useAuth.mockReturnValue(crearAuth({ isAuthenticated: true, roles: ['CLIENTE'] }));
    renderLogin();
    expect(screen.getByText('Página inicio')).toBeInTheDocument();
  });

  test('si ya hay sesión de admin redirige al panel', () => {
    useAuth.mockReturnValue(crearAuth({ isAuthenticated: true, roles: ['ADMIN'] }));
    renderLogin();
    expect(screen.getByText('Página admin')).toBeInTheDocument();
  });
});
