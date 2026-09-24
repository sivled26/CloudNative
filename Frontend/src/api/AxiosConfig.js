import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8181/api',
});

// 🔐 Interceptor REQUEST: agregar token automáticamente
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 🔐 Interceptor RESPONSE: manejar errores 401 y 403
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Token inválido o expirado
      localStorage.removeItem('token');
      localStorage.removeItem('username');
      localStorage.removeItem('role');
      window.location.href = '/login';
    }
    if (error.response?.status === 403) {
      // Sin permisos
      alert('No tienes permisos para realizar esta acción');
    }
    return Promise.reject(error);
  }
);

export default api;
