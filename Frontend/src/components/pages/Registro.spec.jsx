import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import SectionRegistro from '../organisms/SectionRegistro';


const mockNavigate = jest.fn();

jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => mockNavigate,
}));
describe('Registro Component', () => {
  beforeEach(() => {
    jest.clearAllMocks()
  })

  const renderRegistro = () => {
    return render(
      <BrowserRouter>
        <SectionRegistro />
      </BrowserRouter>
    )
  }

  test('Renderiza el formulario de registro', () => {
    renderRegistro()
    const heading = screen.getByRole('heading', { name: /registro/i })
    const usernameInput = screen.getByLabelText(/usuario/i)
    const emailInput = screen.getByLabelText(/correo electrónico/i)
    const passwordInput = screen.getByLabelText(/contraseña/i)
    
    expect(heading).toBeInTheDocument()
    expect(usernameInput).toBeInTheDocument()
    expect(emailInput).toBeInTheDocument()
    expect(passwordInput).toBeInTheDocument()
  })

  test('Muestra error si el usuario está vacío', () => {
    renderRegistro()
    const submitButton = screen.getByRole('button', { name: /registrarse/i })
    fireEvent.click(submitButton)
    
    expect(screen.getByText('Ingresa un usuario.')).toBeInTheDocument()
  })

  test('Muestra error si el usuario es muy corto', () => {
    renderRegistro()
    const usernameInput = screen.getByLabelText(/usuario/i)
    const submitButton = screen.getByRole('button', { name: /registrarse/i })
    
    fireEvent.change(usernameInput, { target: { value: 'ab' } })
    fireEvent.click(submitButton)
    
    expect(screen.getByText('Mínimo 3 caracteres.')).toBeInTheDocument()
  })

  test('Muestra error si el email está vacío', () => {
    renderRegistro()
    const usernameInput = screen.getByLabelText(/usuario/i)
    const submitButton = screen.getByRole('button', { name: /registrarse/i })
    
    fireEvent.change(usernameInput, { target: { value: 'validuser' } })
    fireEvent.click(submitButton)
    
    expect(screen.getByText('Ingresa un correo.')).toBeInTheDocument()
  })

 

  test('Muestra error si la contraseña está vacía', () => {
    renderRegistro()
    const submitButton = screen.getByRole('button', { name: /registrarse/i })
    fireEvent.click(submitButton)
    
    expect(screen.getByText('Ingresa la contraseña.')).toBeInTheDocument()
  })

  test('Navega a login si el registro es exitoso', () => {
    renderRegistro()
    const usernameInput = screen.getByLabelText(/usuario/i)
    const emailInput = screen.getByLabelText(/correo electrónico/i)
    const passwordInput = screen.getByLabelText(/contraseña/i)
    const submitButton = screen.getByRole('button', { name: /registrarse/i })
    
    fireEvent.change(usernameInput, { target: { value: 'validuser' } })
    fireEvent.change(emailInput, { target: { value: 'test@test.com' } })
    fireEvent.change(passwordInput, { target: { value: '123456' } })
    fireEvent.click(submitButton)
    
    expect(mockNavigate).toHaveBeenCalledWith('/login')
  })

  test('Muestra enlace para ir a login', () => {
    renderRegistro()
    const loginLink = screen.getByText(/inicia sesión/i)
    expect(loginLink).toBeInTheDocument()
  })
})