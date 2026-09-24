import React from 'react'

export default function Resena() {
  return (
      <main className="resena-container">
        <h2 className="resena-titulo">
            Reseña: Controlador Inalámbrico Xbox Series X
        </h2>

        <div className="resena-contenido">

            <div className="resena-imagen">
                <img src="images/control-xbox.jpg" alt="control xbox" style={{width:"600px"}}/>
            </div>

            <div className="resena-detalles">
                <p>
                    El <strong>Control Inalámbrico Xbox Series X</strong> ofrece una experiencia de juego de
                    última generación. Diseñado ergonómicamente, con texturas antideslizantes y gatillos mejorados,
                    proporciona mayor precisión y comodidad en sesiones largas.
                </p>

                <h3>Características principales:</h3>
                <ul>
                    <li>Compatibilidad total con Xbox Series X|S, Xbox One y PC (Windows).</li>
                    <li>Botones mapeables y cruceta híbrida mejorada.</li>
                    <li>Conectividad inalámbrica de baja latencia.</li>
                    <li>Texturizado en gatillos, bumpers y carcasa trasera.</li>
                    <li>Puerto de 3.5 mm para auriculares con cable.</li>
                </ul>

                <p className="precio">
                    $59.990 CLP
                </p>

                <button type="button" className="agregar">Agregar al carro</button>
            </div>
        </div>
    </main>
  )
}
