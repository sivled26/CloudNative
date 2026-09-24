import React, { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom';
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';

export default function SectionLogin() {
    const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [errors, setError] = useState({});

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const res = await axios.post("http://localhost:8181/api/auth/login", {
        correo: email,
        password: password,
      });

      
      
      const data = res.data; // tu backend devuelve un String
      if (data.token) {
        const decoded = jwtDecode(data.token);
        localStorage.setItem("token", data.token);
        localStorage.setItem("rol", decoded.rol);
        alert("Login exitoso")
        if (decoded.rol === 'ADMIN') {
          navigate("/admin");
        } else {
          navigate("/");
        }
      } else {
        setError(data); // "Credenciales inválidas."
      }
    } catch (err) {
      console.error("Error en login:", err);
      setError("Error al conectar con el servidor");
    }
  };

  return (
      <section className="form-container">
        <div className="form-card">
          <h2>Iniciar Sesión</h2>
          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label htmlFor="login-email">Correo electrónico</label>
              <input
                type="email"
                id="login-email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="ejemplo@email.com"
                className={errors.email ? 'input-invalid' : ''}
              />
              {errors.email && <span className="input-error">{errors.email}</span>}
            </div>

            <div className="form-group">
              <label htmlFor="login-password">Contraseña</label>
              <input
                type="password"
                id="login-password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••"
                className={errors.password ? 'input-invalid' : ''}
              />
              {errors.password && <span className="input-error">{errors.password}</span>}
            </div>

            <button type="submit" className="form-btn">Iniciar Sesión</button>
          </form>

          <p className="switch-form">
            ¿No tienes cuenta? <Link to="/registro">Regístrate</Link>
          </p>
        </div>
      </section>
  );
}
