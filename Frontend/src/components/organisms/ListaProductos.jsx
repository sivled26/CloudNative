import React, { useEffect, useState } from 'react';
import { productosApi } from '../../api/productosApi';
import { formatearPrecio } from '../../utils/formato';

export default function ListaProductos({ onEdit, onDelete }) {
  const [productos, setProductos] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    productosApi
      .listar()
      .then(setProductos)
      .catch((err) => setError(err.userMessage || 'Error al cargar productos'));
  }, []);

  return (
    <div className="product-list">
      <h3>Lista de Productos</h3>
      {error && <p className="input-error">{error}</p>}
      {productos.map((p) => (
        <div key={p.id} className="product-item">
          <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
            <img src={p.imagenUrl} alt={p.nombre} className="product-img" />
            <span>{p.nombre} - ${formatearPrecio(p.precio)}</span>
          </div>
          <div className="actions">
            <button type="button" className="btn btn-editar" onClick={() => onEdit(p)}>Editar</button>
            <button type="button" className="btn btn-eliminar" onClick={() => onDelete(p.id)}>Eliminar</button>
          </div>
        </div>
      ))}
    </div>
  );
}
