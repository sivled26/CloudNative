import React from 'react'

export default function Filtros() {
  return (
    <div id="filtros">
      <p style={{ fontWeight: 800, padding: '1rem' }}>Filtros</p>
      <p>Categoría</p>
      <div id="xcategoria">
        <label><input type="checkbox" name="Juegos de mesa" id="jgomesa" /> Juegos de Mesa</label>
        <label><input type="checkbox" name="Accesorios" id="Accesorios" /> Accesorios</label>
        <label><input type="checkbox" name="Consolas" id="Consolas" /> Consolas</label>
        <label><input type="checkbox" name="Computadores Gamers" id="pcgamer" /> Computadores Gamers</label>
        <label><input type="checkbox" name="Sillas Gamers" id="sillagamer" /> Sillas Gamers</label>
        <label><input type="checkbox" name="Mouse" id="Mouse" /> Mouse</label>
        <label><input type="checkbox" name="Mousepad" id="Mousepad" /> Mousepad</label>
        <label><input type="checkbox" name="Poleras Personalizadas" id="polera" /> Poleras Personalizadas</label>
      </div>

      <p style={{ fontWeight: 600, marginTop: '1rem' }}>Precio</p>
      <div id="xprecio">
        <label><input type="checkbox" id="p1" /> $10.000 – $40.000</label>
        <label><input type="checkbox" id="p2" /> $40.001 – $80.000</label>
        <label><input type="checkbox" id="p3" /> $80.001 – $150.000</label>
        <label><input type="checkbox" id="p4" /> $150.001 – $500.000</label>
        <label><input type="checkbox" id="p5" /> $500.000 y más</label>
      </div>

      <p style={{ fontWeight: 600, marginTop: '1rem' }}>Marca</p>
      <div id="xmarca">
        <label><input type="checkbox" id="m1" /> Xbox</label>
        <label><input type="checkbox" id="m2" /> HyperX</label>
        <label><input type="checkbox" id="m3" /> Sony / PlayStation</label>
        <label><input type="checkbox" id="m4" /> ASUS ROG</label>
        <label><input type="checkbox" id="m5" /> Secretlab</label>
        <label><input type="checkbox" id="m6" /> Logitech</label>
        <label><input type="checkbox" id="m7" /> Razer</label>
      </div>

      <button id="btnAplicar">Aplicar filtro</button>
    </div>
  )
}
