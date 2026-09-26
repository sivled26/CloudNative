import React, { useState } from 'react';
import { productosApi } from '../../api/productosApi';

const FORM_VACIO = { nombre: '', descripcion: '', categoria: '', precio: '', imagenUrl: '' };

export default function AgregarProductos({ onSuccess }) {
  const [form, setForm] = useState(FORM_VACIO);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await productosApi.crear({ ...form, precio: Number(form.precio) });
      alert('Producto agregado correctamente');
      onSuccess();
      setForm(FORM_VACIO);
    } catch (error) {
      console.error(error);
      alert(error.userMessage || 'Error al agregar producto');
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <input type="text" name="nombre" value={form.nombre} onChange={handleChange} placeholder="Nombre" required />
      <textarea name="descripcion" value={form.descripcion} onChange={handleChange} placeholder="Descripción" required />
      <input type="text" name="categoria" value={form.categoria} onChange={handleChange} placeholder="Categoría" required />
      <input type="number" name="precio" value={form.precio} onChange={handleChange} placeholder="Precio" min="0" required />
      <input type="text" name="imagenUrl" value={form.imagenUrl} onChange={handleChange} placeholder="Imagen URL" required />
      <button type="submit">Agregar</button>
    </form>
  );
}
