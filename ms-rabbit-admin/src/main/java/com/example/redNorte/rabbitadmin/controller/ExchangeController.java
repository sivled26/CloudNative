package com.example.redNorte.rabbitadmin.controller;

import com.example.redNorte.rabbitadmin.dto.ExchangeRequest;
import com.example.redNorte.rabbitadmin.service.RabbitAdminService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/exchanges")
public class ExchangeController {

    private final RabbitAdminService service;

    public ExchangeController(RabbitAdminService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Void> crear(@Valid @RequestBody ExchangeRequest request) {
        service.crearExchange(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Void> eliminar(@PathVariable("name") String name) {
        service.eliminarExchange(name);
        return ResponseEntity.noContent().build();
    }
}