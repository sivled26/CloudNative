package com.example.redNorte.demo.service;

import com.example.redNorte.demo.config.RolesPermisos;
import com.example.redNorte.demo.dto.SupervisorDtos.AsignacionDto;
import com.example.redNorte.demo.dto.SupervisorDtos.RolDto;
import com.example.redNorte.demo.dto.SupervisorDtos.UsuarioDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Asigna y quita App roles de Azure usando Microsoft Graph.
 *
 * El backend se autentica ante Graph como la propia aplicación "Cloud" (client credentials),
 * por eso necesita un client secret y estos permisos de aplicación con consentimiento de administrador:
 * AppRoleAssignment.ReadWrite.All, Application.Read.All y User.Read.All.
 */
@Service
public class GraphService {

    private static final String GRAPH = "https://graph.microsoft.com/v1.0";
    private static final ParameterizedTypeReference<Map<String, Object>> JSON =
            new ParameterizedTypeReference<>() {
            };

    private final RestClient http = RestClient.create();
    private final String tenantId;
    private final String clientId;
    private final String clientSecret;

    private String token;
    private Instant tokenExpira = Instant.EPOCH;

    // Datos del service principal de la app "Cloud" (se leen una vez).
    private String servicePrincipalId;
    private final Map<String, String> rolIdPorNombre = new HashMap<>();
    private final Map<String, String> rolNombrePorId = new HashMap<>();

    public GraphService(@Value("${azure.tenant-id}") String tenantId,
                        @Value("${azure.client-id}") String clientId,
                        @Value("${azure.client-secret:}") String clientSecret) {
        this.tenantId = tenantId;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    // ---------- Operaciones que usa el controller ----------

    public List<RolDto> listarRoles() {
        List<RolDto> roles = new ArrayList<>();
        RolesPermisos.PERMISOS.forEach((nombre, permisos) -> roles.add(new RolDto(
                nombre, descripcion(nombre), permisos, RolesPermisos.ASIGNABLES.contains(nombre))));
        return roles;
    }

    public List<AsignacionDto> listarAsignaciones() {
        cargarServicePrincipal();
        List<AsignacionDto> resultado = new ArrayList<>();
        String url = GRAPH + "/servicePrincipals/" + servicePrincipalId + "/appRoleAssignedTo?$top=999";
        while (url != null) {
            Map<String, Object> pagina = get(url);
            for (Map<String, Object> a : lista(pagina.get("value"))) {
                String rol = rolNombrePorId.get(texto(a.get("appRoleId")));
                if (rol == null || !"User".equals(a.get("principalType"))) continue; // acceso por defecto o grupos
                resultado.add(new AsignacionDto(texto(a.get("id")), texto(a.get("principalId")),
                        texto(a.get("principalDisplayName")), rol));
            }
            url = (String) pagina.get("@odata.nextLink");
        }
        return resultado;
    }

    public List<UsuarioDto> buscarUsuarios(String buscar) {
        if (buscar == null || buscar.isBlank()) return List.of();
        String t = buscar.trim().replace("'", "''");
        String filtro = "startswith(displayName,'" + t + "') or startswith(mail,'" + t
                + "') or startswith(userPrincipalName,'" + t + "')";
        Map<String, Object> res = llamar(() -> http.get()
                .uri(GRAPH + "/users?$filter={f}&$select=id,displayName,mail,userPrincipalName&$top=20", filtro)
                .headers(h -> h.setBearerAuth(token()))
                .retrieve().body(JSON));
        List<UsuarioDto> usuarios = new ArrayList<>();
        for (Map<String, Object> u : lista(res.get("value"))) {
            String correo = u.get("mail") != null ? texto(u.get("mail")) : texto(u.get("userPrincipalName"));
            usuarios.add(new UsuarioDto(texto(u.get("id")), texto(u.get("displayName")), correo));
        }
        return usuarios;
    }

    public AsignacionDto asignarRol(String usuarioId, String rol) {
        if (usuarioId == null || usuarioId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Falta el usuario.");
        }
        if (!RolesPermisos.ASIGNABLES.contains(rol)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solo se pueden asignar los roles ADMIN o CLIENTE.");
        }
        cargarServicePrincipal();
        String appRoleId = rolIdPorNombre.get(rol);
        if (appRoleId == null) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "El rol " + rol + " no existe en la aplicación de Azure. Créalo en App roles.");
        }
        Map<String, Object> body = Map.of(
                "principalId", usuarioId,
                "resourceId", servicePrincipalId,
                "appRoleId", appRoleId);
        Map<String, Object> creado = llamar(() -> http.post()
                .uri(GRAPH + "/servicePrincipals/" + servicePrincipalId + "/appRoleAssignedTo")
                .headers(h -> h.setBearerAuth(token()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve().body(JSON));
        return new AsignacionDto(texto(creado.get("id")), usuarioId, texto(creado.get("principalDisplayName")), rol);
    }

    public void quitarRol(String asignacionId) {
        AsignacionDto asignacion = listarAsignaciones().stream()
                .filter(a -> a.id().equals(asignacionId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "La asignación no existe."));
        if (!RolesPermisos.ASIGNABLES.contains(asignacion.rol())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El rol " + asignacion.rol() + " no se puede quitar desde el dashboard.");
        }
        llamar(() -> http.delete()
                .uri(GRAPH + "/servicePrincipals/" + servicePrincipalId + "/appRoleAssignedTo/" + asignacionId)
                .headers(h -> h.setBearerAuth(token()))
                .retrieve().toBodilessEntity());
    }

    // ---------- Internos ----------

    private synchronized void cargarServicePrincipal() {
        if (servicePrincipalId != null) return;
        Map<String, Object> sp = get(GRAPH + "/servicePrincipals(appId='" + clientId + "')?$select=id,appRoles");
        for (Map<String, Object> r : lista(sp.get("appRoles"))) {
            rolIdPorNombre.put(texto(r.get("value")), texto(r.get("id")));
            rolNombrePorId.put(texto(r.get("id")), texto(r.get("value")));
        }
        servicePrincipalId = texto(sp.get("id"));
    }

    /** Token de la app para llamar a Graph (se reutiliza hasta que vence). */
    private synchronized String token() {
        if (clientSecret == null || clientSecret.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Falta configurar AZURE_CLIENT_SECRET en el backend.");
        }
        if (token != null && Instant.now().isBefore(tokenExpira.minusSeconds(60))) return token;

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("scope", "https://graph.microsoft.com/.default");
        try {
            Map<String, Object> res = http.post()
                    .uri("https://login.microsoftonline.com/{tenant}/oauth2/v2.0/token", tenantId)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve().body(JSON);
            token = texto(res.get("access_token"));
            tokenExpira = Instant.now().plusSeconds(((Number) res.get("expires_in")).longValue());
            return token;
        } catch (HttpClientErrorException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Azure rechazó las credenciales del backend. Revisa AZURE_CLIENT_SECRET.");
        }
    }

    private Map<String, Object> get(String url) {
        return llamar(() -> http.get().uri(java.net.URI.create(url.replace("'", "%27").replace("$", "%24")))
                .headers(h -> h.setBearerAuth(token()))
                .retrieve().body(JSON));
    }

    /** Ejecuta una llamada a Graph y traduce sus errores a mensajes entendibles. */
    private <T> T llamar(java.util.function.Supplier<T> llamada) {
        try {
            return llamada.get();
        } catch (HttpClientErrorException e) {
            String detalle = e.getResponseBodyAsString();
            if (e.getStatusCode().value() == 400 && detalle.contains("already exists")) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "El usuario ya tiene ese rol.");
            }
            if (e.getStatusCode().value() == 401 || e.getStatusCode().value() == 403) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                        "Azure no permitió la operación. Falta dar consentimiento de administrador a los "
                                + "permisos de Microsoft Graph de la app Cloud.");
            }
            if (e.getStatusCode().value() == 404) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró el recurso en Azure.");
            }
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error de Microsoft Graph: " + e.getStatusText());
        }
    }

    private static String descripcion(String rol) {
        return switch (rol) {
            case RolesPermisos.ADMIN -> "Administra el catálogo de productos";
            case RolesPermisos.CLIENTE -> "Usuario normal de la tienda";
            case RolesPermisos.SUPERVISOR -> "Gestiona los roles de los usuarios (se asigna en el portal de Azure)";
            default -> "";
        };
    }

    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> lista(Object valor) {
        return valor instanceof List<?> l ? (List<Map<String, Object>>) l : List.of();
    }

    private static String texto(Object valor) {
        return valor == null ? null : valor.toString();
    }
}
