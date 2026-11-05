package com.example.redNorte.rabbitadmin.controller;

import com.example.redNorte.rabbitadmin.dto.BindingRequest;
import com.example.redNorte.rabbitadmin.service.RabbitAdminService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bindings")
public class BindingController {

    private final RabbitAdminService service;

    public BindingController(RabbitAdminService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Void> crear(@Valid @RequestBody BindingRequest request) {
        service.crearBinding(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /** DELETE /bindings?destino=q.test&tipoDestino=QUEUE&exchange=rednorte.direct&routingKey=test.key */
    @DeleteMapping
    public ResponseEntity<Void> eliminar(@RequestParam("destino") String destino,
                                         @RequestParam("tipoDestino") BindingRequest.TipoDestino tipoDestino,
                                         @RequestParam("exchange") String exchange,
                                         @RequestParam(value = "routingKey", defaultValue = "") String routingKey) {
        service.eliminarBinding(destino, tipoDestino, exchange, routingKey);
        return ResponseEntity.noContent().build();
    }
}