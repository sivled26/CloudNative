package com.example.redNorte.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "pacientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pacientes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String apellido;

    @Column(unique = true, nullable = false)
    private String correo;

    @Column(unique = true, nullable = false, length = 12)
    private String run;

    @Column(nullable = false)
    private String contrasena;

    private LocalDate fechaNacimiento;

    private String direccion;
}
