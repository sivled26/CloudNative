package com.example.redNorte.rabbitadmin.controller;

import com.example.redNorte.rabbitadmin.dto.MensajeRequest;
import com.example.redNorte.rabbitadmin.service.MensajeriaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/messages")
public class MensajeController {

    private final MensajeriaService service;

    public MensajeController(MensajeriaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Void> publicar(@Valid @RequestBody MensajeRequest request) {
        service.publicar(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}