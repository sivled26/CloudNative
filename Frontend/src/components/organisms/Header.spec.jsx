import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import Header from './Header';
import { useAuth } from '../../auth/useAuth';
import { crearAuth } from '../../../test/authMock';

jest.mock('../../auth/useAuth');

const renderHeader = () => render(<MemoryRouter><Header /></MemoryRouter>);

describe('Header', () => {
  test('sin sesión muestra Iniciar Sesión y no muestra Admin', () => {
    const auth = crearAuth();
    useAuth.mockReturnValue(auth);
    renderHeader();
    fireEvent.click(screen.getByRole('button', { name: /iniciar sesión/i }));
    expect(auth.login).toHaveBeenCalled();
    expect(screen.queryByText('Admin')).not.toBeInTheDocument();
  });

  test('cliente ve su nombre y Cerrar Sesión, pero no Admin', () => {
    const auth = crearAuth({ isAuthenticated: true, name: 'Ana', roles: ['CLIENTE'] });
    useAuth.mockReturnValue(auth);
    renderHeader();
    expect(screen.getByText('Ana')).toBeInTheDocument();
    expect(screen.queryByText('Admin')).not.toBeInTheDocument();
    fireEvent.click(screen.getByRole('button', { name: /cerrar sesión/i }));
    expect(auth.logout).toHaveBeenCalled();
  });

  test('admin ve el enlace Admin', () => {
    useAuth.mockReturnValue(crearAuth({ isAuthenticated: true, name: 'Admin Uno', roles: ['ADMIN'] }));
    renderHeader();
    expect(screen.getByText('Admin')).toBeInTheDocument();
  });
});
