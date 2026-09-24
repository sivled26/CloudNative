import React, { useState } from 'react';
import axios from 'axios';

export default function AgregarProductos({ onSuccess }) {
  const [form, setForm] = useState({
    nombre: '',
    descripcion: '',
    categoria: '',
    precio: '',
    imagenUrl: '',
  });

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleFileChange = (e) => {
    setForm({ ...form, imagen: e.target.files[0] });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const data = new FormData();
    Object.keys(form).forEach((key) => data.append(key, form[key]));
    const token = localStorage.getItem("token");
    try {
      await axios.post("http://localhost:8181/api/productos", form, {
        headers: { 
          "Content-Type":"application/json",
          "Authorization": `Bearer ${token}`
        }
        
      });
      alert("Producto agregado correctamente");
      onSuccess(); // refresca lista
      setForm({ nombre: '', descripcion: '', categoria: '', precio: '', imagenUrl: '' });
    } catch (error) {
      console.error(error);
      alert("Error al agregar producto");
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <input type="text" name="nombre" value={form.nombre} onChange={handleChange} placeholder="Nombre" required />
      <textarea name="descripcion" value={form.descripcion} onChange={handleChange} placeholder="Descripción" required />
      <input type="text" name="categoria" value={form.categoria} onChange={handleChange} placeholder="Categoría" required />
      <input type="number" name="precio" value={form.precio} onChange={handleChange} placeholder="Precio" required />
      <input type="text" name="imagenUrl" value={form.imagenUrl} onChange={handleChange} placeholder="Imagen URL" required />
      <button type="submit">Agregar</button>
    </form>
  );
}
