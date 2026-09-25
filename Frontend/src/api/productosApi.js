import api from './AxiosConfig';

// Endpoints de productos expuestos por el BFF a través del API Gateway.
export const productosApi = {
  listar: () => api.get('/productos').then((res) => res.data),
  obtener: (id) => api.get(`/productos/${id}`).then((res) => res.data),
  crear: (producto) => api.post('/productos', producto).then((res) => res.data),
  actualizar: (id, producto) => api.put(`/productos/${id}`, producto).then((res) => res.data),
  eliminar: (id) => api.delete(`/productos/${id}`),
};
