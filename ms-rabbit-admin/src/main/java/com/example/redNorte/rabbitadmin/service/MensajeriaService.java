package com.example.redNorte.rabbitadmin.service;

import com.example.redNorte.rabbitadmin.dto.MensajeRequest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/** Productor de este microservicio: publica mensajes de prueba en cualquier exchange. */
@Service
public class MensajeriaService {

    private final RabbitTemplate rabbitTemplate;

    public MensajeriaService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publicar(MensajeRequest r) {
        rabbitTemplate.convertAndSend(r.exchange(), r.routingKey() == null ? "" : r.routingKey(), r.mensaje());
    }
}