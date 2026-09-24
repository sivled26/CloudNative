import React from 'react'

export default function Articulo(props) {
  const {code, titulo, descripcion, fecha} = props
  return (
    <div>
            <article className="product-card2">
                <h3>{titulo}</h3>
                <p className="description">{descripcion}</p>
                <span className="price">{fecha}</span>
            </article>
    </div>
  )
}