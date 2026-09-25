import React, { useEffect, useState } from 'react';
import { productosApi } from '../../api/productosApi';

export default function EditarProductos({ producto, onClose, onSuccess }) {
  const [form, setForm] = useState({
    nombre: '',
    descripcion: '',
    categoria: '',
    precio: '',
    imagenUrl: '',
  });

  useEffect(() => {
    if (producto) {
      setForm({
        nombre: producto.nombre ?? '',
        descripcion: producto.descripcion ?? '',
        categoria: producto.categoria ?? '',
        precio: producto.precio ?? '',
        imagenUrl: producto.imagenUrl ?? '',
      });
    }
  }, [producto]);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await productosApi.actualizar(producto.id, { ...form, precio: Number(form.precio) });
      alert('Producto actualizado correctamente');
      onSuccess();
      onClose();
    } catch (error) {
      console.error(error);
      alert(error.userMessage || 'Error al actualizar producto');
    }
  };

  if (!producto) return null;

  return (
    <div className="modal">
      <div className="modal-content">
        <h3 className="section-title">Editar Producto</h3>
        <form onSubmit={handleSubmit} className="admin-form">
          <input type="text" name="nombre" value={form.nombre} onChange={handleChange} placeholder="Nombre" required />
          <textarea name="descripcion" value={form.descripcion} onChange={handleChange} placeholder="Descripción" required />
          <input type="text" name="categoria" value={form.categoria} onChange={handleChange} placeholder="Categoría" required />
          <input type="number" name="precio" value={form.precio} onChange={handleChange} placeholder="Precio" min="0" required />
          <input type="text" name="imagenUrl" value={form.imagenUrl} onChange={handleChange} placeholder="Imagen URL" required />
          <button type="submit" className="btn btn-editar">Guardar cambios</button>
          <button type="button" className="btn btn-eliminar" onClick={onClose}>Cancelar</button>
        </form>
      </div>
    </div>
  );
}
