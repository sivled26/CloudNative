package com.example.redNorte.demo.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Colas de RabbitMQ con Dead Letter Queue (DLQ).
 *
 * Flujo:
 *   Sender ──> cola "hello" ──> Receiver
 *                   │ (si el Receiver falla 3 veces)
 *                   ▼
 *          exchange "hello.dlx" ──> cola "hello.dlq"  (mensajes fallidos)
 *
 * Los reintentos se configuran en application.properties (spring.rabbitmq.listener.simple.retry.*).
 */
@Configuration
public class RabbitMQConfig {

    public static final String COLA = "hello";
    public static final String DLX = "hello.dlx";
    public static final String DLQ = "hello.dlq";

    /**
     * Cola principal. Cuando un mensaje se rechaza sin reencolar, RabbitMQ lo envía
     * automáticamente al exchange DLX con la routing key "hello.dlq".
     * durable: la cola sobrevive si RabbitMQ se reinicia.
     */
    @Bean
    public Queue helloQueue() {
        return QueueBuilder.durable(COLA)
                .deadLetterExchange(DLX)
                .deadLetterRoutingKey(DLQ)
                .build();
    }

    /** Exchange donde llegan los mensajes rechazados (Dead Letter Exchange). */
    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DLX);
    }

    /** Cola donde quedan guardados los mensajes que fallaron (Dead Letter Queue). */
    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(DLQ).build();
    }

    /** Une el exchange DLX con la DLQ. */
    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange()).with(DLQ);
    }
}
