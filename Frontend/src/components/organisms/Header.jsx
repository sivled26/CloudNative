import React from "react";
import { Link, useNavigate } from "react-router-dom";

export default function Header() {
    const navigate = useNavigate();
    const token = localStorage.getItem("token");
    const rol = localStorage.getItem("rol"); // 👈 obtenemos el rol

    const handleLogout = () => {
        localStorage.removeItem("token");
        localStorage.removeItem("rol");
        navigate("/login");
    };

    return (
        <header>
            <div id="logo">
                <Link to="/">
                    <img src="images/Level-Up.png" alt="Level-Up" />
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

                {/* 👇 cambia la ruta según el rol */}
                {rol === "ADMIN" ? (
                    <Link to="/admin">Admin</Link>
                ) : (
                    <Link to="/perfil">Usuario</Link>
                )}

                {token ? (
                    <button onClick={handleLogout} className="logout-btn">
                        Cerrar Sesión
                    </button>
                ) : (
                    <Link to="/login">Iniciar Sesión</Link>
                )}
            </div>
        </header>
    );
}
