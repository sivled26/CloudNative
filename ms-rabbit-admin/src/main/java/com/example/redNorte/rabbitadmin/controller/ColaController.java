package com.example.redNorte.rabbitadmin.controller;

import com.example.redNorte.rabbitadmin.dto.ColaRequest;
import com.example.redNorte.rabbitadmin.dto.ColaResponse;
import com.example.redNorte.rabbitadmin.service.RabbitAdminService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/queues")
public class ColaController {

    private final RabbitAdminService service;

    public ColaController(RabbitAdminService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ColaResponse> crear(@Valid @RequestBody ColaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearCola(request));
    }

    @GetMapping("/{name}")
    public ColaResponse obtener(@PathVariable("name") String name) {
        return service.obtenerCola(name);
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Void> eliminar(@PathVariable("name") String name) {
        service.eliminarCola(name);
        return ResponseEntity.noContent().build();
    }
}