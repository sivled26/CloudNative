import api from './AxiosConfig';

// Endpoints del dashboard del supervisor (el backend asigna los roles en Azure vía Microsoft Graph).
export const supervisorApi = {
  roles: () => api.get('/supervisor/roles').then((res) => res.data),
  asignaciones: () => api.get('/supervisor/asignaciones').then((res) => res.data),
  buscarUsuarios: (buscar) =>
    api.get('/supervisor/usuarios', { params: { buscar } }).then((res) => res.data),
  asignarRol: (usuarioId, rol) =>
    api.post('/supervisor/asignaciones', { usuarioId, rol }).then((res) => res.data),
  quitarRol: (asignacionId) => api.delete(`/supervisor/asignaciones/${asignacionId}`),
};
