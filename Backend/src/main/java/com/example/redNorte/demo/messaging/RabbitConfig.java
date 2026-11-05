package com.example.redNorte.demo.messaging;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuracion de mensajeria del productor: declara los exchanges (idempotente).
 * Las colas, DLQs y bindings las declara ms-notificaciones.
 */
@Configuration
@EnableConfigurationProperties(RabbitNamesProperties.class)
public class RabbitConfig {

    private final RabbitNamesProperties props;

    public RabbitConfig(RabbitNamesProperties props) {
        this.props = props;
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(props.exchanges().direct(), true, false);
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(props.exchanges().topic(), true, false);
    }
}