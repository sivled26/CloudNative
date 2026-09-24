import React from 'react'

export default function Ubicacion() {
  return (
    <div id="ubicacion">
      <p className="destacado">¡Encuéntranos!</p>
      <p>
        Estamos ubicados en el Mall Costanera Center. Av. Andrés Bello 2425, 7510689 Providencia, Región Metropolitana, Chile. Local xxxx Piso xxxx
      </p>
      <iframe
        src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3330.201563519307!2d-70.60897038980025!3d-33.417988995737595!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x9662cf69d4854951%3A0x9a87ef2fefaad0df!2sCenco%20Costanera!5e0!3m2!1ses-419!2sus!4v1757438731060!5m2!1ses-419!2sus"
        width="600"
        height="450"
        style={{ border: 0 }}
        allowFullScreen
        loading="lazy"
        referrerPolicy="no-referrer-when-downgrade"
        title="Ubicación en Google Maps"
      ></iframe>
    </div>
  );
}
