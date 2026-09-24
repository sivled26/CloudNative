package com.example.redNorte.demo.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PacienteResponse {
    private String nombre;
    private String apellido;
    private String correo;
    private String run;
    private LocalDate fechaNacimiento;
    private String direccion;
}
