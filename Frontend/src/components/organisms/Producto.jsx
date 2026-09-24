import React from 'react'

function addToCart(product){
  const products = JSON.parse(localStorage.getItem('products')) || []
  console.log(products)
  products.push(product)
  localStorage.setItem('products',JSON.stringify(products))
}

export default function Producto(props) {
  const {code, image, name, price, description, category} = props
  return (
    <div className="producto">
        <a href="/resena">
            <div className="productolink">
                <div className="imagenProducto" style={{backgroundImage: `url(${image})`}}></div>
                <div className="prod">
                    <p className="nombreprod">{name}</p>
                </div>
            </div>
        </a>
        <p className="precio">${price}</p>
        <p className="categoria">Categoría<br/>{category}</p>
        <p className="descripcion">{description}</p>
        <button type="button" className="agregar" onClick={()=>addToCart(props)}>Agregar al carro</button>
    </div>
  )
}