import React, { useEffect, useState } from 'react';
import Filtros from '../organisms/Filtros';
import Buscador from '../molecules/Buscador';
import Producto from '../organisms/Producto';
import { productosApi } from '../../api/productosApi';
import { formatearPrecio } from '../../utils/formato';

export default function Catalogo() {
  const [productos, setProductos] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    productosApi
      .listar()
      .then(setProductos)
      .catch((err) => setError(err.userMessage || 'Error al obtener productos'));
  }, []);

  return (
    <>
      <Buscador />
      <div id="seccion2">
        <Filtros />
        <div id="productos">
          {error && <p className="input-error">{error}</p>}
          {productos.map((p) => (
            <Producto
              key={p.id}
              code={p.id}
              image={p.imagenUrl}
              name={p.nombre}
              description={p.descripcion}
              category={p.categoria}
              price={formatearPrecio(p.precio)}
            />
          ))}
        </div>
      </div>
    </>
  );
}
