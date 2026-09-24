import React, { useEffect, useState } from 'react'
import Seccion1 from '../organisms/Seccion1'
import Product from '../organisms/Product'
import axios from 'axios'

export default function Home() {
  const [productos, setProductos] = useState([])

  useEffect(() => {
    const token = localStorage.getItem("token");
    axios.get("http://localhost:8181/api/productos", {
      headers: {
        "Authorization": `Bearer ${token}`
      }
    })
      .then(res => setProductos(res.data))
      .catch(err => console.error("Error al obtener productos:", err))
  }, [])

  
  const idsPermitidos = [1,2,3,4] 
  const productosFiltrados = productos.filter(p => idsPermitidos.includes(p.id))

  return (
    <>
      <Seccion1/>
      <main className="main-content">
        <h2 className="section-title">Nuestros Productos</h2>
        <div className="product-grid">
          {productosFiltrados.map(p => (
            <Product
              key={p.id}
              image={p.imagenUrl}       
              title={p.nombre}        
              description={p.descripcion} 
              price={p.precio.toLocaleString("es-CL")}
            />
          ))}
        </div>
      </main>
    </>
  )
}
