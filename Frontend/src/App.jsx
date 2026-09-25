import './App.css';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import AuthProvider from './auth/AuthProvider';
import ProtectedRoute from './auth/ProtectedRoute';
import { ROLES } from './auth/authConfig';
import Header from './components/organisms/Header';
import Footer from './components/organisms/Footer';
import Home from './components/pages/Home';
import Login from './components/pages/Login';
import Registro from './components/pages/Registro';
import PerfilPage from './components/pages/PerfilPage';
import CarritoPage from './components/pages/CarritoPage';
import Catalogo from './components/pages/Catalogo';
import AboutUs from './components/pages/AboutUs';
import Blog from './components/pages/Blog';
import Resena from './components/pages/Resena';
import DashboardAdmin from './components/pages/DashboardAdmin';

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Header />
        <Routes>
          {/* Rutas públicas */}
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/registro" element={<Registro />} />
          <Route path="/catalogo" element={<Catalogo />} />
          <Route path="/aboutus" element={<AboutUs />} />
          <Route path="/blog" element={<Blog />} />
          <Route path="/resena" element={<Resena />} />

          {/* Rutas que requieren sesión de Azure AD */}
          <Route path="/perfil" element={<ProtectedRoute><PerfilPage /></ProtectedRoute>} />
          <Route path="/carrito" element={<ProtectedRoute><CarritoPage /></ProtectedRoute>} />

          {/* Ruta que requiere el rol ADMIN */}
          <Route
            path="/admin"
            element={<ProtectedRoute roles={[ROLES.ADMIN]}><DashboardAdmin /></ProtectedRoute>}
          />
        </Routes>
        <Footer />
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;
