package com.example.redNorte.demo.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Productor de eventos asincronos. La logica de negocio solo llama a estos metodos
 * y no conoce exchanges ni routing keys.
 */
@Component
public class EventoPublisher {

    private static final Logger log = LoggerFactory.getLogger(EventoPublisher.class);

    private final RabbitTemplate rabbitTemplate;
    private final RabbitNamesProperties props;

    public EventoPublisher(RabbitTemplate rabbitTemplate, RabbitNamesProperties props) {
        this.rabbitTemplate = rabbitTemplate;
        this.props = props;
    }

    public void publicarNotificacion(String detalle) {
        enviar(props.exchanges().direct(), props.routing().notificaciones(), "NOTIFICACION", detalle);
    }

    public void publicarTicket(String detalle) {
        enviar(props.exchanges().direct(), props.routing().tickets(), "TICKET", detalle);
    }

    public void publicarDocumento(String detalle) {
        enviar(props.exchanges().topic(), props.routing().documentos(), "DOCUMENTO", detalle);
    }

    private void enviar(String exchange, String routingKey, String tipo, String detalle) {
        String json = "{\"tipo\":\"" + escapar(tipo) + "\",\"detalle\":\"" + escapar(detalle)
                + "\",\"fecha\":\"" + Instant.now() + "\"}";
        // Un fallo de mensajeria no debe romper la operacion de negocio.
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, json);
            log.info("Evento publicado exchange={} rk={} payload={}", exchange, routingKey, json);
        } catch (Exception e) {
            log.error("No se pudo publicar el evento {} en {}: {}", tipo, exchange, e.getMessage());
        }
    }

    private String escapar(String texto) {
        return texto == null ? "" : texto.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ");
    }
}