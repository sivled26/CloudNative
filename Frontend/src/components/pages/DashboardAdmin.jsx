import React, { useState } from 'react';
import AgregarProductos from '../organisms/AgregarProductos';
import ListaProductos from '../organisms/ListaProductos';
import EditarProductos from '../organisms/EditarProductos';
import { productosApi } from '../../api/productosApi';

// El acceso a esta página lo controla <ProtectedRoute roles={['ADMIN']}> en App.jsx.
export default function DashboardAdmin() {
  const [refresh, setRefresh] = useState(0);
  const [productoEditar, setProductoEditar] = useState(null);

  const recargar = () => setRefresh((r) => r + 1);

  const handleDelete = async (id) => {
    if (!window.confirm('¿Eliminar este producto?')) return;
    try {
      await productosApi.eliminar(id);
      alert('Producto eliminado');
      recargar();
    } catch (error) {
      console.error(error);
      alert(error.userMessage || 'Error al eliminar producto');
    }
  };

  return (
    <div className="dashboard">
      <h2>Panel de Administración</h2>

      <div className="admin-form">
        <h3>Agregar Producto</h3>
        <AgregarProductos onSuccess={recargar} />
      </div>

      <ListaProductos key={refresh} onEdit={setProductoEditar} onDelete={handleDelete} />

      {productoEditar && (
        <EditarProductos
          producto={productoEditar}
          onClose={() => setProductoEditar(null)}
          onSuccess={recargar}
        />
      )}
    </div>
  );
}
