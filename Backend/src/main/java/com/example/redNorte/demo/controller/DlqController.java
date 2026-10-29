package com.example.redNorte.demo.controller;

import com.example.redNorte.demo.config.RabbitMQConfig;
import com.example.redNorte.demo.service.DlqService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Endpoints para revisar y reprocesar la Dead Letter Queue (DLQ).
 *
 * GET  /api/messages/dlq             -> cuántos mensajes fallidos hay en la DLQ
 * POST /api/messages/dlq/reprocesar  -> devuelve los mensajes de la DLQ a la cola "hello"
 */
@RestController
@RequestMapping("/api/messages/dlq")
public class DlqController {

    private final DlqService dlqService;

    public DlqController(DlqService dlqService) {
        this.dlqService = dlqService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> estado() {
        return ResponseEntity.ok(Map.of(
                "cola", RabbitMQConfig.DLQ,
                "mensajesFallidos", dlqService.contarMensajes()));
    }

    @PostMapping("/reprocesar")
    public ResponseEntity<Map<String, Object>> reprocesar() {
        return ResponseEntity.ok(Map.of("reenviados", dlqService.reprocesar()));
    }
}
