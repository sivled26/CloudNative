import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import Registro from './Registro';
import { useAuth } from '../../auth/useAuth';
import { crearAuth } from '../../../test/authMock';

jest.mock('../../auth/useAuth');

describe('Registro', () => {
  test('explica que las cuentas se gestionan con Azure AD y permite ingresar', () => {
    const auth = crearAuth();
    useAuth.mockReturnValue(auth);
    render(<MemoryRouter><Registro /></MemoryRouter>);
    expect(screen.getByText(/azure ad/i)).toBeInTheDocument();
    fireEvent.click(screen.getByRole('button', { name: /ingresar con microsoft/i }));
    expect(auth.login).toHaveBeenCalled();
  });
});
