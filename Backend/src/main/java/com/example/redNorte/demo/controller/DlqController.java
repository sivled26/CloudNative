package com.example.redNorte.demo.controller;

import com.example.redNorte.demo.config.RabbitMQConfig;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.QueueInformation;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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

    private static final int MAXIMO_POR_LLAMADA = 100;

    private final RabbitTemplate rabbitTemplate;
    private final AmqpAdmin amqpAdmin;

    public DlqController(RabbitTemplate rabbitTemplate, AmqpAdmin amqpAdmin) {
        this.rabbitTemplate = rabbitTemplate;
        this.amqpAdmin = amqpAdmin;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> estado() {
        QueueInformation info = amqpAdmin.getQueueInfo(RabbitMQConfig.DLQ);
        long cantidad = info == null ? 0 : info.getMessageCount();
        return ResponseEntity.ok(Map.of(
                "cola", RabbitMQConfig.DLQ,
                "mensajesFallidos", cantidad));
    }

    @PostMapping("/reprocesar")
    public ResponseEntity<Map<String, Object>> reprocesar() {
        int reenviados = 0;
        Message mensaje;
        while (reenviados < MAXIMO_POR_LLAMADA
                && (mensaje = rabbitTemplate.receive(RabbitMQConfig.DLQ)) != null) {
            // Exchange "" (por defecto) + routing key = nombre de la cola principal.
            rabbitTemplate.send("", RabbitMQConfig.COLA, mensaje);
            reenviados++;
        }
        System.out.println("[ ] Reprocesados " + reenviados + " mensajes desde la DLQ");
        return ResponseEntity.ok(Map.of("reenviados", reenviados));
    }
}
