package com.example.redNorte.demo.controller;

import com.example.redNorte.demo.dto.SupervisorDtos.AsignacionDto;
import com.example.redNorte.demo.dto.SupervisorDtos.AsignarRolRequest;
import com.example.redNorte.demo.dto.SupervisorDtos.RolDto;
import com.example.redNorte.demo.dto.SupervisorDtos.UsuarioDto;
import com.example.redNorte.demo.service.GraphService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

/**
 * Endpoints del dashboard del supervisor. Solo los puede llamar un usuario con el rol SUPERVISOR
 * (regla definida en SecurityConfig).
 */
@Slf4j
@RestController
@RequestMapping("/api/supervisor")
@RequiredArgsConstructor
public class SupervisorController {

    private final GraphService graphService;

    @GetMapping("/roles")
    public List<RolDto> roles() {
        return graphService.listarRoles();
    }

    @GetMapping("/asignaciones")
    public List<AsignacionDto> asignaciones() {
        return graphService.listarAsignaciones();
    }

    @GetMapping("/usuarios")
    public List<UsuarioDto> buscarUsuarios(@RequestParam(defaultValue = "") String buscar) {
        return graphService.buscarUsuarios(buscar);
    }

    @PostMapping("/asignaciones")
    public ResponseEntity<AsignacionDto> asignar(@RequestBody AsignarRolRequest request,
                                                 @AuthenticationPrincipal Jwt supervisor) {
        AsignacionDto creada = graphService.asignarRol(request.usuarioId(), request.rol());
        log.info("{} asignó el rol {} al usuario {}", quien(supervisor), request.rol(), request.usuarioId());
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @DeleteMapping("/asignaciones/{id}")
    public ResponseEntity<Void> quitar(@PathVariable String id, @AuthenticationPrincipal Jwt supervisor) {
        graphService.quitarRol(id);
        log.info("{} quitó la asignación de rol {}", quien(supervisor), id);
        return ResponseEntity.noContent().build();
    }

    /** Devuelve el error como { "message": "..." }, que es lo que muestra el frontend. */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> manejarError(ResponseStatusException e) {
        String mensaje = e.getReason() != null ? e.getReason() : "Ocurrió un error inesperado.";
        return ResponseEntity.status(e.getStatusCode()).body(Map.of("message", mensaje));
    }

    private static String quien(Jwt jwt) {
        if (jwt == null) return "desconocido";
        String correo = jwt.getClaimAsString("preferred_username");
        return correo != null ? correo : jwt.getSubject();
    }
}
