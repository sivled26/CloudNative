import React, { useState, useEffect } from 'react';
import AgregarProductos from '../organisms/AgregarProductos';
import ListaProductos from '../organisms/ListaProductos';
import EditarProductos from '../organisms/EditarProductos';
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';

export default function DashboardAdmin() {
  const [refresh, setRefresh] = useState(false);
  const [productoEditar, setProductoEditar] = useState(null);
  const [isAdmin, setIsAdmin] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (token) {
      try {
        const decoded = jwtDecode(token);
        // Suponiendo que el claim se llama "role"
        if (decoded.rol !== "ADMIN") {
          setIsAdmin(false);
        }
      } catch (error) {
        console.error("Error decodificando token:", error);
        setIsAdmin(false);
      }
    } else {
      setIsAdmin(false);
    }
  }, []);

  const handleSuccess = () => setRefresh(!refresh);

  const handleDelete = async (id) => {
    try {
      const token = localStorage.getItem("token");
      await axios.delete(`http://localhost:8181/api/productos/${id}`, {
        headers: {
          "Authorization": `Bearer ${token}`
        }
      });
      alert("Producto eliminado");
      setRefresh(!refresh);
    } catch (error) {
      console.error(error);
      alert("Error al eliminar producto");
    }
  };

  const handleEdit = (producto) => {
    setProductoEditar(producto);
  };

  const handleCloseEdit = () => {
    setProductoEditar(null);
  };

  if (!isAdmin) {
    return (
      <div className="error-message">
        <h2>Acceso denegado</h2>
        <p>No tienes permisos para acceder al panel de administración.</p>
      </div>
    );
  }

  return (
    <div className="dashboard">
      <h2>Panel de Administración</h2>

      <div className="admin-form">
        <h3>Agregar Producto</h3>
        <AgregarProductos onSuccess={handleSuccess} />
      </div>

      <div className="product-list">
        <h3>Lista de Productos</h3>
        <ListaProductos onEdit={handleEdit} onDelete={handleDelete} key={refresh} />
      </div>

      {productoEditar && (
        <div className="modal">
          <div className="modal-content">
            <EditarProductos
              producto={productoEditar}
              onClose={handleCloseEdit}
              onSuccess={handleSuccess}
            />
          </div>
        </div>
      )}
    </div>
  );
}
