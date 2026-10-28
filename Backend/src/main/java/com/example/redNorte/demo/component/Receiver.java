package com.example.redNorte.demo.component;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class Receiver {

    /**
     * Consumidor de la cola "hello".
     * Spring confirma (ack) el mensaje automáticamente si el método termina sin error;
     * si lanza una excepción, el mensaje se rechaza y vuelve a la cola.
     */
    @RabbitListener(queues = "hello")
    public void receiveMessage(String message, Message rawMessage) {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));

        System.out.println("[" + timestamp + "] [ ] Mensaje recibido: '" + message + "'");
        System.out.println(" - Content-Type: " + rawMessage.getMessageProperties().getContentType());

        // Aquí va tu lógica de procesamiento del mensaje
    }
}
