import React, { useCallback, useEffect, useState } from 'react';
import { supervisorApi } from '../../api/supervisorApi';

// El acceso a esta página lo controla <ProtectedRoute roles={['SUPERVISOR']}> en App.jsx.
export default function DashboardSupervisor() {
  const [roles, setRoles] = useState([]);
  const [asignaciones, setAsignaciones] = useState([]);
  const [cargando, setCargando] = useState(true);

  const [buscar, setBuscar] = useState('');
  const [resultados, setResultados] = useState(null);
  const [rolElegido, setRolElegido] = useState({}); // usuarioId -> rol

  const [mensaje, setMensaje] = useState(null); // { tipo: 'ok' | 'error', texto }
  const [ocupado, setOcupado] = useState(false);

  const rolesAsignables = roles.filter((r) => r.asignable).map((r) => r.nombre);

  const cargar = useCallback(async () => {
    setCargando(true);
    try {
      const [r, a] = await Promise.all([supervisorApi.roles(), supervisorApi.asignaciones()]);
      setRoles(r);
      setAsignaciones(a);
    } catch (error) {
      setMensaje({ tipo: 'error', texto: error.userMessage || 'No se pudieron cargar los roles.' });
    } finally {
      setCargando(false);
    }
  }, []);

  useEffect(() => {
    cargar();
  }, [cargar]);

  const handleBuscar = async (e) => {
    e.preventDefault();
    if (!buscar.trim()) return;
    setOcupado(true);
    setMensaje(null);
    try {
      setResultados(await supervisorApi.buscarUsuarios(buscar.trim()));
    } catch (error) {
      setMensaje({ tipo: 'error', texto: error.userMessage || 'No se pudo buscar usuarios.' });
    } finally {
      setOcupado(false);
    }
  };

  const handleAsignar = async (usuario) => {
    const rol = rolElegido[usuario.id] ?? rolesAsignables[0];
    if (!rol) return;
    setOcupado(true);
    setMensaje(null);
    try {
      await supervisorApi.asignarRol(usuario.id, rol);
      setMensaje({ tipo: 'ok', texto: `Rol ${rol} asignado a ${usuario.nombre}.` });
      await cargar();
    } catch (error) {
      setMensaje({ tipo: 'error', texto: error.userMessage || 'No se pudo asignar el rol.' });
    } finally {
      setOcupado(false);
    }
  };

  const handleQuitar = async (asignacion) => {
    if (!window.confirm(`¿Quitar el rol ${asignacion.rol} a ${asignacion.nombre}?`)) return;
    setOcupado(true);
    setMensaje(null);
    try {
      await supervisorApi.quitarRol(asignacion.id);
      setMensaje({ tipo: 'ok', texto: `Rol ${asignacion.rol} quitado a ${asignacion.nombre}.` });
      await cargar();
    } catch (error) {
      setMensaje({ tipo: 'error', texto: error.userMessage || 'No se pudo quitar el rol.' });
    } finally {
      setOcupado(false);
    }
  };

  const rolesDe = (usuarioId) =>
    asignaciones.filter((a) => a.usuarioId === usuarioId).map((a) => a.rol);

  return (
    <div className="dashboard supervisor">
      <h2>Panel de Supervisor</h2>

      {mensaje && (
        <p role="status" className={`sup-mensaje ${mensaje.tipo}`}>
          {mensaje.texto}
        </p>
      )}

      {/* Roles y sus permisos */}
      <section className="admin-form">
        <h3>Roles y permisos</h3>
        {cargando && roles.length === 0 ? (
          <p>Cargando...</p>
        ) : (
          <div className="sup-roles">
            {roles.map((rol) => (
              <article key={rol.nombre} className="sup-rol">
                <h4>{rol.nombre}</h4>
                <p className="sup-desc">{rol.descripcion}</p>
                <ul>
                  {rol.permisos.map((p) => (
                    <li key={p}>{p}</li>
                  ))}
                </ul>
              </article>
            ))}
          </div>
        )}
      </section>

      {/* Buscar usuario y asignarle un rol */}
      <section className="admin-form">
        <h3>Asignar rol</h3>
        <form className="sup-buscar" onSubmit={handleBuscar}>
          <input
            type="search"
            placeholder="Nombre o correo del usuario"
            aria-label="Buscar usuario"
            value={buscar}
            onChange={(e) => setBuscar(e.target.value)}
          />
          <button type="submit" disabled={ocupado}>Buscar</button>
        </form>

        {resultados && resultados.length === 0 && <p>No se encontraron usuarios.</p>}
        {resultados?.map((u) => (
          <div key={u.id} className="product-item">
            <span>
              <strong>{u.nombre}</strong>
              <br />
              <small>{u.correo}</small>
              {rolesDe(u.id).length > 0 && <small className="sup-actual"> · Actual: {rolesDe(u.id).join(', ')}</small>}
            </span>
            <div className="actions">
              <select
                aria-label={`Rol para ${u.nombre}`}
                value={rolElegido[u.id] ?? rolesAsignables[0] ?? ''}
                onChange={(e) => setRolElegido({ ...rolElegido, [u.id]: e.target.value })}
              >
                {rolesAsignables.map((r) => (
                  <option key={r} value={r}>{r}</option>
                ))}
              </select>
              <button type="button" className="btn-editar" disabled={ocupado} onClick={() => handleAsignar(u)}>
                Asignar
              </button>
            </div>
          </div>
        ))}
      </section>

      {/* Usuarios que ya tienen rol */}
      <section className="product-list">
        <h3>Usuarios con rol</h3>
        {!cargando && asignaciones.length === 0 && <p>Todavía no hay usuarios con rol asignado.</p>}
        {asignaciones.map((a) => (
          <div key={a.id} className="product-item">
            <span>
              <strong>{a.nombre}</strong>
            </span>
            <span className={`sup-badge rol-${a.rol.toLowerCase()}`}>{a.rol}</span>
            <div className="actions">
              {rolesAsignables.includes(a.rol) ? (
                <button type="button" className="btn-eliminar" disabled={ocupado} onClick={() => handleQuitar(a)}>
                  Quitar
                </button>
              ) : (
                <small>Se gestiona en Azure</small>
              )}
            </div>
          </div>
        ))}
      </section>

      <p className="sup-nota">
        Los cambios se aplican en Azure al instante, pero el usuario debe cerrar sesión y volver a entrar
        para que su nuevo rol aparezca en el token.
      </p>
    </div>
  );
}
