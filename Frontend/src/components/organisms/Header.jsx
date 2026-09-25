import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../auth/useAuth';

export default function Header() {
  const { isAuthenticated, isAdmin, name, login, logout } = useAuth();

  return (
    <header>
      <div id="logo">
        <Link to="/">
          <img src="/images/Level-Up.png" alt="Level-Up" />
        </Link>
      </div>

      <nav id="catalogonav">
        <Link to="/">Inicio</Link>
        <Link to="/catalogo">Catalogo</Link>
        <Link to="/aboutus">Nosotros</Link>
        <Link to="/blog">Blog</Link>
      </nav>

      <div id="extra">
        <Link to="/carrito">Carro 🛒</Link>

        {isAuthenticated && isAdmin && <Link to="/admin">Admin</Link>}
        {isAuthenticated && <Link to="/perfil">{name || 'Mi perfil'}</Link>}

        {isAuthenticated ? (
          <button type="button" onClick={logout} className="logout-btn">
            Cerrar Sesión
          </button>
        ) : (
          <button type="button" onClick={login} className="logout-btn">
            Iniciar Sesión
          </button>
        )}
      </div>
    </header>
  );
}
