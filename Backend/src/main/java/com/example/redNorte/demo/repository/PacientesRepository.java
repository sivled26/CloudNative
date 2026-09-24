package com.example.redNorte.demo.repository;
import com.example.redNorte.demo.model.Pacientes;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
public interface PacientesRepository extends JpaRepository<Pacientes, Long> {
    Optional<Pacientes> findByCorreo(String correo);
}
