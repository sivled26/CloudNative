import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../auth/useAuth';

// Las cuentas ahora las administra Azure AD, por eso ya no hay formulario de registro propio.
export default function SectionRegistro() {
  const { login } = useAuth();

  return (
    <section className="form-container">
      <div className="form-card">
        <h2>Registro</h2>
        <p>
          Las cuentas de LevelUp Gamer se gestionan con Microsoft Azure AD.
          Si ya tienes una cuenta asignada, ingresa con Microsoft.
        </p>
        <button type="button" className="form-btn" onClick={login}>
          Ingresar con Microsoft
        </button>
        <p className="switch-form">
          ¿Ya tienes sesión? <Link to="/">Volver al inicio</Link>
        </p>
      </div>
    </section>
  );
}
