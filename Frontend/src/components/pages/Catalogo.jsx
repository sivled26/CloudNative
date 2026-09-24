import React, { useEffect, useState } from 'react'
import Filtros from '../organisms/Filtros'
import Buscador from '../molecules/Buscador'
import Producto from '../organisms/Producto'
import axios from 'axios'

export default function Catalogo() {
  const [productos, setProductos] = useState([])

  useEffect(() => {
   //const token = localStorage.getItem("token");
    axios.get("http://localhost:8181/api/productos")/*, {
      headers: {
        "Authorization": `Bearer ${token}`
      }
    })*/
      .then(res => setProductos(res.data))
      .catch(err => console.error("Error al obtener productos:", err))
  }, [])
  /*const idsPermitidos = [5,6,7,8,9,10] 
  const productosFiltrados = productos.filter(p => idsPermitidos.includes(p.id))*/
  return (
    <>
      <Buscador/>
      <div id="seccion2">
        <Filtros/>
        <div id="productos">
          {productos.map(p => (
            <Producto
            code={p.id}
            image={p.imagenUrl}       
            name={p.nombre}        
            description={p.descripcion}
            category={p.categoria}
            price={p.precio.toLocaleString("es-CL")}
          />
          ))}
        </div>
      </div>
    </>
    
  )
}
