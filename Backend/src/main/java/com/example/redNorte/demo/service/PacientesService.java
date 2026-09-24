package com.example.redNorte.demo.service;

import com.example.redNorte.demo.dto.*;
import com.example.redNorte.demo.model.Pacientes;
import com.example.redNorte.demo.repository.PacientesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PacientesService {

    private final PacientesRepository pacientesRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder; // 👈 usa el bean inyectado

    public String registrar(RegistroPacienteRequest request) {
        if (pacientesRepository.findByCorreo(request.getCorreo()).isPresent()) {
            throw new RuntimeException("Correo ya registrado");
        }

        Pacientes paciente = Pacientes.builder()
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .correo(request.getCorreo())
                .run(request.getRun())
                .contrasena(passwordEncoder.encode(request.getContrasena())) // 👈 usa el bean
                .direccion(request.getDireccion())
                .build();
        try {
            pacientesRepository.save(paciente);
            return "Paciente registrado correctamente";
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Datos inválidos: " + e.getMessage());
        }

   //     pacientesRepository.save(paciente);
   //     return "Paciente registrado con éxito";
    }

    public AuthResponse login(LoginPacienteRequest request) {
        Pacientes paciente = pacientesRepository.findByCorreo(request.getCorreo())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        System.out.println("Plain: " + request.getContrasena());
        System.out.println("Encoded: " + paciente.getContrasena());
        System.out.println("Match: " + passwordEncoder.matches(request.getContrasena(), paciente.getContrasena()));


        if (!passwordEncoder.matches(request.getContrasena(), paciente.getContrasena())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        }

        String token = jwtService.generateToken(paciente.getCorreo());
        return new AuthResponse(token, paciente.getCorreo());
    }
}
