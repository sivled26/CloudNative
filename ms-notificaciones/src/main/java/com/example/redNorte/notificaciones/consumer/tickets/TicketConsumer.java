package com.example.redNorte.notificaciones.consumer.tickets;

import com.example.redNorte.notificaciones.consumer.common.BaseConsumer;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

/** Consumidor del dominio "tickets": escucha la cola ${rabbit.queues.tickets}. */
@Component
public class TicketConsumer extends BaseConsumer {

    private static final Logger log = LoggerFactory.getLogger(TicketConsumer.class);

    @RabbitListener(queues = "${rabbit.queues.tickets}")
    public void recibir(String payload, Message message, Channel channel,
                        @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        manejar("TICKET", payload, message, channel, tag, () -> {
            validar(payload, message);
            log.info("[TICKET] generando ticket: {}", payload);
        });
    }
}