package com.example.redNorte.demo.service;

import com.example.redNorte.demo.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.QueueInformation;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * Operaciones sobre la Dead Letter Queue: contar los mensajes fallidos y reprocesarlos.
 * Mantiene el acceso a RabbitMQ fuera del controlador.
 */
@Service
public class DlqService {

    private static final Logger log = LoggerFactory.getLogger(DlqService.class);
    private static final int MAXIMO_POR_LLAMADA = 100;

    private final RabbitTemplate rabbitTemplate;
    private final AmqpAdmin amqpAdmin;

    public DlqService(RabbitTemplate rabbitTemplate, AmqpAdmin amqpAdmin) {
        this.rabbitTemplate = rabbitTemplate;
        this.amqpAdmin = amqpAdmin;
    }

    /** Cantidad de mensajes que hay en la DLQ. */
    public long contarMensajes() {
        QueueInformation info = amqpAdmin.getQueueInfo(RabbitMQConfig.DLQ);
        return info == null ? 0 : info.getMessageCount();
    }

    /** Devuelve los mensajes de la DLQ a la cola principal para procesarlos de nuevo. */
    public int reprocesar() {
        int reenviados = 0;
        Message mensaje;
        while (reenviados < MAXIMO_POR_LLAMADA
                && (mensaje = rabbitTemplate.receive(RabbitMQConfig.DLQ)) != null) {
            // Exchange "" (por defecto) + routing key = nombre de la cola principal.
            rabbitTemplate.send("", RabbitMQConfig.COLA, mensaje);
            reenviados++;
        }
        log.info("[DLQ] Reprocesados {} mensajes desde {}", reenviados, RabbitMQConfig.DLQ);
        return reenviados;
    }
}
