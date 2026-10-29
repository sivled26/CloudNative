package com.example.redNorte.demo.component;

import com.example.redNorte.demo.config.RabbitMQConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class Receiver {

    /**
     * Consumidor de la cola "hello".
     * Spring confirma (ack) el mensaje automáticamente si el método termina sin error.
     * Si lanza una excepción, Spring lo reintenta (3 intentos en total, ver application.properties)
     * y si sigue fallando lo rechaza: RabbitMQ lo manda a la DLQ "hello.dlq".
     */
    @RabbitListener(queues = RabbitMQConfig.COLA)
    public void receiveMessage(String message, Message rawMessage) {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));

        System.out.println("[" + timestamp + "] [ ] Mensaje recibido: '" + message + "'");
        System.out.println(" - Content-Type: " + rawMessage.getMessageProperties().getContentType());

        // Simulación de fallo para probar la DLQ: cualquier mensaje que contenga "error" falla.
        if (message.toLowerCase().contains("error")) {
            System.out.println(" - [X] Falló el procesamiento, se reintentará o irá a la DLQ");
            throw new IllegalStateException("No se pudo procesar el mensaje: " + message);
        }

        // Aquí va tu lógica de procesamiento del mensaje
    }
}
