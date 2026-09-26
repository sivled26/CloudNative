package com.example.redNorte.demo.repository;

import com.example.redNorte.demo.model.Productos;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductosRepository extends JpaRepository<Productos, Long> {
    List<Productos> findByCategoriaIgnoreCase(String categoria);

    List<Productos> findByNombreContainingIgnoreCase(String nombre);
}
