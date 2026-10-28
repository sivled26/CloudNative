package com.example.redNorte.demo.config;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Roles de la aplicación (definidos como "App roles" en Azure) y los permisos que otorga cada uno.
 * El "value" de cada App role en Azure debe coincidir exactamente con estos nombres.
 */
public final class RolesPermisos {

    public static final String SUPERVISOR = "SUPERVISOR";
    public static final String ADMIN = "ADMIN";
    public static final String CLIENTE = "CLIENTE";

    /** Roles que el supervisor puede asignar o quitar desde el dashboard. */
    public static final Set<String> ASIGNABLES = Set.of(ADMIN, CLIENTE);

    /** Permisos de cada rol. SecurityConfig aplica estas reglas a los endpoints. */
    public static final Map<String, List<String>> PERMISOS = new LinkedHashMap<>();

    static {
        PERMISOS.put(ADMIN, List.of(
                "Ver catálogo de productos",
                "Crear productos",
                "Editar productos",
                "Eliminar productos",
                "Usar carrito y perfil"));
        PERMISOS.put(CLIENTE, List.of(
                "Ver catálogo de productos",
                "Usar carrito y perfil"));
        PERMISOS.put(SUPERVISOR, List.of(
                "Ver usuarios de la aplicación",
                "Asignar roles ADMIN y CLIENTE",
                "Quitar roles ADMIN y CLIENTE"));
    }

    private RolesPermisos() {
    }
}
