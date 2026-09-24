import React, { useEffect, useState } from 'react';
import axios from 'axios';

export default function ListaProductos({ onEdit, onDelete }) {
  const [productos, setProductos] = useState([]);

  useEffect(() => {
    const token = localStorage.getItem("token");
    axios.get("http://localhost:8181/api/productos", {
      headers: {
        "Authorization": `Bearer ${token}`
      }
    })
      .then(res => setProductos(res.data))
      .catch(err => console.error(err));
  }, []);

  return (
    <div className="product-list">
      <h3>Lista de Productos</h3>
      {productos.map((p) => (
        <div key={p.id} className="product-item">
          <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
            <img src={p.imagenUrl} alt={p.nombre} className="product-img" />
            <span>{p.nombre} - ${p.precio}</span>
          </div>
          <div className="actions">
            <button className="btn btn-editar" onClick={() => onEdit(p)}>Editar</button>
            <button className="btn btn-eliminar" onClick={() => onDelete(p.id)}>Eliminar</button>
          </div>
        </div>
      ))}
    </div>
  );
}
