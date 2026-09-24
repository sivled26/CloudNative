import React, { useState, useEffect } from 'react';
import axios from 'axios';

export default function EditarProductos({ producto, onClose, onSuccess }) {
  const [form, setForm] = useState({
    nombre: '',
    descripcion: '',
    categoria: '',
    precio: '',
    imagen: null,
  });

  useEffect(() => {
    if (producto) {
      setForm({
        nombre: producto.nombre,
        descripcion: producto.descripcion,
        categoria: producto.categoria,
        precio: producto.precio,
        imagen: null,
      });
    }
  }, [producto]);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleFileChange = (e) => {
    setForm({ ...form, imagen: e.target.files[0] });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const data = new FormData();
    Object.keys(form).forEach((key) => {
      if (form[key] !== null) data.append(key, form[key]);
    });
    const token = localStorage.getItem("token");
    try {
      await axios.put(`http://localhost:8181/api/productos/${producto.id}`, form, {
        headers: { 
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        }
      });
      alert("Producto actualizado correctamente");
      onSuccess();
      onClose();
    } catch (error) {
      console.error(error);
      alert("Error al actualizar producto");
    }
  };

  if (!producto) return null;

  return (
    <div className="modal">
      <div className="modal-content">
        <h3 className="section-title">Editar Producto</h3>
        <form onSubmit={handleSubmit} className="admin-form">
          <input
            type="text"
            name="nombre"
            value={form.nombre}
            onChange={handleChange}
            placeholder="Nombre"
            required
          />
          <textarea
            name="descripcion"
            value={form.descripcion}
            onChange={handleChange}
            placeholder="Descripción"
            required
          />
          <input
            type="text"
            name="categoria"
            value={form.categoria}
            onChange={handleChange}
            placeholder="Categoría"
            required
          />
          <input
            type="number"
            name="precio"
            value={form.precio}
            onChange={handleChange}
            placeholder="Precio"
            required
          />
          <input type="text" name="imagenUrl" value={form.imagenUrl} onChange={handleChange} placeholder="Imagen URL" required />
          <button type="submit" className="btn btn-editar">Guardar cambios</button>
          <button type="button" className="btn btn-eliminar" onClick={onClose}>Cancelar</button>
        </form>
      </div>
    </div>
  );
}
