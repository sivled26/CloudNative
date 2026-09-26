import React, { useState } from 'react';

function leerCarrito() {
  try {
    return JSON.parse(localStorage.getItem('products')) || [];
  } catch {
    return [];
  }
}

export default function Carrito() {
  const [productos, setProductos] = useState(leerCarrito);

  const vaciar = () => {
    localStorage.removeItem('products');
    setProductos([]);
  };

  return (
    <main className="main-content">
      <h2 className="section-title">Tu Carrito</h2>
      {productos.length === 0 ? (
        <div className="product-card">
          <h3>No has agregado nada al carrito</h3>
        </div>
      ) : (
        <>
          {productos.map((p, i) => (
            <div key={`${p.code}-${i}`} className="product-card">
              <h3>{p.name}</h3>
              <p>${p.price}</p>
            </div>
          ))}
          <button type="button" className="btn btn-eliminar" onClick={vaciar}>
            Vaciar carrito
          </button>
        </>
      )}
    </main>
  );
}
