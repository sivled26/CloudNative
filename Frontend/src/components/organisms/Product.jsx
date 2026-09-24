import React from 'react'

export default function Product({ image, title, description, price }) {
  return (
    <div className="product-card">
        <img src={image} alt={title}/>
        <h3>{title}</h3>
        <p className="description">{description}</p>
        <span className="price">${price}</span>
        <button className="buy-button">Comprar</button>
    </div>
  )
}
