import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { BrowserRouter, useNavigate } from 'react-router-dom';
import SectionLogin from '../organisms/SectionLogin';
const mockNavigate = jest.fn();

jest.mock('react-router-dom', () => {
  const originalModule = jest.requireActual('react-router-dom');
  return {
    ...originalModule,
    useNavigate: () => mockNavigate,
  };
});

describe('Login Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  const renderLogin = () =>
    render(
      <BrowserRouter>
        <SectionLogin />
      </BrowserRouter>
    );

  test('Renderiza el formulario de inicio de sesión', () => {
    renderLogin();
    expect(screen.getByRole('heading', { name: /iniciar sesión/i })).toBeInTheDocument();
    expect(screen.getByLabelText(/correo electrónico/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/contraseña/i)).toBeInTheDocument();
  });

  test('Muestra error si el correo está vacío', () => {
    renderLogin();
    fireEvent.click(screen.getByRole('button', { name: /iniciar sesión/i }));
    expect(screen.getByText('Ingresa un correo.')).toBeInTheDocument();
  });

 

  test('Muestra error si la contraseña está vacía', () => {
    renderLogin();
    fireEvent.change(screen.getByLabelText(/correo electrónico/i), {
      target: { value: 'valid@email.com' },
    });
    fireEvent.click(screen.getByRole('button', { name: /iniciar sesión/i }));
    expect(screen.getByText('Ingresa la contraseña.')).toBeInTheDocument();
  });

  test('Navega a home si el login es exitoso', () => {
    renderLogin();
    fireEvent.change(screen.getByLabelText(/correo electrónico/i), {
      target: { value: 'test@test.com' },
    });
    fireEvent.change(screen.getByLabelText(/contraseña/i), {
      target: { value: '123456' },
    });
    fireEvent.click(screen.getByRole('button', { name: /iniciar sesión/i }));
    expect(mockNavigate).toHaveBeenCalledWith('/');
  });
});
