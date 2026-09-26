import React, { useEffect, useState } from 'react';
import Seccion1 from '../organisms/Seccion1';
import Product from '../organisms/Product';
import { productosApi } from '../../api/productosApi';
import { formatearPrecio } from '../../utils/formato';

export default function Home() {
  const [productos, setProductos] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    productosApi
      .listar()
      .then(setProductos)
      .catch((err) => setError(err.userMessage || 'Error al obtener productos'));
  }, []);

  // Se muestran los 4 primeros productos como destacados.
  const destacados = productos.slice(0, 4);

  return (
    <>
      <Seccion1 />
      <main className="main-content">
        <h2 className="section-title">Nuestros Productos</h2>
        {error && <p className="input-error">{error}</p>}
        <div className="product-grid">
          {destacados.map((p) => (
            <Product
              key={p.id}
              image={p.imagenUrl}
              title={p.nombre}
              description={p.descripcion}
              price={formatearPrecio(p.precio)}
            />
          ))}
        </div>
      </main>
    </>
  );
}
