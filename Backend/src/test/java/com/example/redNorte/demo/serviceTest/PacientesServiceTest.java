package com.example.redNorte.demo.serviceTest;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.redNorte.demo.dto.LoginPacienteRequest;

import com.example.redNorte.demo.dto.RegistroPacienteRequest;
import com.example.redNorte.demo.dto.AuthResponse;
import com.example.redNorte.demo.model.Pacientes;
import com.example.redNorte.demo.repository.PacientesRepository;

import com.example.redNorte.demo.service.PacientesService;
import com.example.redNorte.demo.service.JwtService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class PacientesServiceTest {

    @Mock
    private PacientesRepository pacientesRepository;
    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private PacientesService pacientesService;

    @Test
    void testRegistrarPacienteExitoso() {
        RegistroPacienteRequest request = new RegistroPacienteRequest();
        request.setNombre("Arturo");
        request.setApellido("Santiago");
        request.setCorreo("arturo@mail.com");
        request.setRun("12345678-9");
        request.setContrasena("1234");
        request.setDireccion("Melipilla");

        when(pacientesRepository.findByCorreo("arturo@mail.com"))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode("1234"))
                .thenReturn("$2a$10$encodedPassword");

        String resultado = pacientesService.registrar(request);

        assertEquals("Paciente registrado correctamente", resultado);
        verify(pacientesRepository).save(any(Pacientes.class));
    }

    @Test
    void testLoginPacienteExitoso() {
        Pacientes pacientes = Pacientes.builder()
                .correo("arturo@mail.com")
                .contrasena("$2a$10$encodedPassword")
                .build();

        when(pacientesRepository.findByCorreo("arturo@mail.com"))
                .thenReturn(Optional.of(pacientes));
        when(passwordEncoder.matches("1234", "$2a$10$encodedPassword"))
                .thenReturn(true);
        when(jwtService.generateToken("arturo@mail.com"))
                .thenReturn("fake-jwt-token");

        LoginPacienteRequest request = new LoginPacienteRequest();
        request.setCorreo("arturo@mail.com");
        request.setContrasena("1234");

        AuthResponse response = pacientesService.login(request);

        assertNotNull(response);
        assertEquals("arturo@mail.com", response.getCorreo());
        assertEquals("fake-jwt-token", response.getToken());
        verify(pacientesRepository).findByCorreo("arturo@mail.com");
    }

}
