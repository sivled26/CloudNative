package com.example.redNorte.notificaciones.consumer.documentos;

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

/** Consumidor del dominio "documentos": escucha la cola ${rabbit.queues.documentos}. */
@Component
public class DocumentoConsumer extends BaseConsumer {

    private static final Logger log = LoggerFactory.getLogger(DocumentoConsumer.class);

    @RabbitListener(queues = "${rabbit.queues.documentos}")
    public void recibir(String payload, Message message, Channel channel,
                        @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        manejar("DOCUMENTO", payload, message, channel, tag, () -> {
            validar(payload, message);
            log.info("[DOCUMENTO] generando documento: {}", payload);
        });
    }
}