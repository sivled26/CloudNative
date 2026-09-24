package com.example.redNorte.demo.controller;

import com.example.redNorte.demo.dto.*;
import com.example.redNorte.demo.service.PacientesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pacientes")
@RequiredArgsConstructor
public class PacientesController {

    private final PacientesService pacientesService;

    @PostMapping("/registro")
    public ResponseEntity<String> registrar(@RequestBody RegistroPacienteRequest request) {
        return ResponseEntity.ok(pacientesService.registrar(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginPacienteRequest request) {
        return ResponseEntity.ok(pacientesService.login(request));
    }
}
