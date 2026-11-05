package com.example.redNorte.notificaciones.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Nombres de exchanges, colas y routing keys leidos desde application.yml (prefijo "rabbit").
 * Ninguna clase de negocio escribe nombres a mano.
 */
@ConfigurationProperties(prefix = "rabbit")
public record RabbitNamesProperties(Exchanges exchanges, Queues queues, Routing routing) {

    public record Exchanges(String direct, String topic, String dlx) {
    }

    public record Queues(String notificaciones, String tickets, String documentos, String dlqSuffix) {
    }

    public record Routing(String notificaciones, String tickets, String documentos) {
    }
}