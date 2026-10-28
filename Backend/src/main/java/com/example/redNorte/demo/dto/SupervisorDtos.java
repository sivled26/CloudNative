package com.example.redNorte.demo.dto;

import java.util.List;

/** DTOs que usa el dashboard del supervisor. */
public final class SupervisorDtos {

    /** Un rol, sus permisos y si el supervisor lo puede asignar. */
    public record RolDto(String nombre, String descripcion, List<String> permisos, boolean asignable) {
    }

    /** Un usuario de Azure (resultado de búsqueda). */
    public record UsuarioDto(String id, String nombre, String correo) {
    }

    /** Un rol asignado a un usuario en la aplicación de Azure. */
    public record AsignacionDto(String id, String usuarioId, String nombre, String rol) {
    }

    /** Cuerpo de POST /api/supervisor/asignaciones. */
    public record AsignarRolRequest(String usuarioId, String rol) {
    }

    private SupervisorDtos() {
    }
}
