import React from 'react'
import Articulo from '../organisms/Articulo'

export default function Blog() {
  return (
    <div>
    <main class="main-content">
        <h2 class="section-title">Blog Gamer</h2>
        <section class="blog-posts">
            <Articulo
                code="1"
                titulo="¿Por qué elegir teclados mecánicos?"
                descripcion="Descubre las ventajas de los teclados mecánicos para gaming y productividad."
                fecha="Publicado: 11 Septiembre 2025"
            />
            <Articulo
                code="2"
                titulo="Guía para monitores de alta frecuencia"
                descripcion="¿144Hz o 240Hz? Te explicamos cuál es mejor para tu setup gamer."
                fecha="Publicado: 10 Septiembre 2025"
            />
        </section>
    </main>
    </div>
  )
}
