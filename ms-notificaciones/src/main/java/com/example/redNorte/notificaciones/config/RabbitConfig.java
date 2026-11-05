package com.example.redNorte.notificaciones.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Topologia RabbitMQ (exchanges, colas, DLQs y bindings) de los 3 casos de uso:
 *  - notificaciones : exchange DIRECT  (rk notificacion.enviar)
 *  - tickets        : exchange DIRECT  (rk ticket.generar)
 *  - documentos     : exchange TOPIC   (rk documento.*)
 * Cada cola principal es quorum (replicada en los 2 nodos) y tiene su DLQ via el DLX.
 * Solo configuracion: aqui no hay logica de negocio.
 */
@Configuration
@EnableConfigurationProperties(RabbitNamesProperties.class)
public class RabbitConfig {

    private final RabbitNamesProperties props;

    public RabbitConfig(RabbitNamesProperties props) {
        this.props = props;
    }

    // ---------------------------------------------------------------- Exchanges
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(props.exchanges().direct(), true, false);
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(props.exchanges().topic(), true, false);
    }

    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(props.exchanges().dlx(), true, false);
    }

    // ---------------------------------------------------------------- Notificaciones
    @Bean
    public Queue notificacionesQueue() {
        return colaPrincipal(props.queues().notificaciones());
    }

    @Bean
    public Queue notificacionesDlq() {
        return colaDlq(props.queues().notificaciones());
    }

    @Bean
    public Binding notificacionesBinding() {
        return BindingBuilder.bind(notificacionesQueue()).to(directExchange())
                .with(props.routing().notificaciones());
    }

    @Bean
    public Binding notificacionesDlqBinding() {
        return dlqBinding(notificacionesDlq(), props.queues().notificaciones());
    }

    // ---------------------------------------------------------------- Tickets
    @Bean
    public Queue ticketsQueue() {
        return colaPrincipal(props.queues().tickets());
    }

    @Bean
    public Queue ticketsDlq() {
        return colaDlq(props.queues().tickets());
    }

    @Bean
    public Binding ticketsBinding() {
        return BindingBuilder.bind(ticketsQueue()).to(directExchange())
                .with(props.routing().tickets());
    }

    @Bean
    public Binding ticketsDlqBinding() {
        return dlqBinding(ticketsDlq(), props.queues().tickets());
    }

    // ---------------------------------------------------------------- Documentos
    @Bean
    public Queue documentosQueue() {
        return colaPrincipal(props.queues().documentos());
    }

    @Bean
    public Queue documentosDlq() {
        return colaDlq(props.queues().documentos());
    }

    @Bean
    public Binding documentosBinding() {
        return BindingBuilder.bind(documentosQueue()).to(topicExchange())
                .with(props.routing().documentos());
    }

    @Bean
    public Binding documentosDlqBinding() {
        return dlqBinding(documentosDlq(), props.queues().documentos());
    }

    // ---------------------------------------------------------------- Helpers de construccion
    private Queue colaPrincipal(String nombre) {
        return QueueBuilder.durable(nombre)
                .quorum()
                .deadLetterExchange(props.exchanges().dlx())
                .deadLetterRoutingKey(nombre + props.queues().dlqSuffix())
                .build();
    }

    private Queue colaDlq(String nombreCola) {
        return QueueBuilder.durable(nombreCola + props.queues().dlqSuffix()).build();
    }

    private Binding dlqBinding(Queue dlq, String nombreCola) {
        return BindingBuilder.bind(dlq).to(dlxExchange())
                .with(nombreCola + props.queues().dlqSuffix());
    }
}