import React, { useState } from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../../auth/useAuth';

export default function SectionLogin() {
  const { isAuthenticated, isAdmin, ready, login } = useAuth();
  const [error, setError] = useState('');

  if (ready && isAuthenticated) {
    return <Navigate to={isAdmin ? '/admin' : '/'} replace />;
  }

  const handleLogin = async () => {
    setError('');
    try {
      await login(); // redirige a la página de login de Microsoft (Azure AD)
    } catch (err) {
      console.error('Error en login:', err);
      setError('No se pudo iniciar sesión con Microsoft. Intenta nuevamente.');
    }
  };

  return (
    <section className="form-container">
      <div className="form-card">
        <h2>Iniciar Sesión</h2>
        <p>Ingresa con tu cuenta Microsoft (Azure AD). No guardamos tu contraseña.</p>
        <button type="button" className="form-btn" onClick={handleLogin} disabled={!ready}>
          Ingresar con Microsoft
        </button>
        {error && <span className="input-error">{error}</span>}
      </div>
    </section>
  );
}
