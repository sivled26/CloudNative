package com.example.redNorte.demo.messaging;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** Nombres leidos desde application.properties (prefijo "rabbit"). */
@ConfigurationProperties(prefix = "rabbit")
public record RabbitNamesProperties(Exchanges exchanges, Routing routing) {

    public record Exchanges(String direct, String topic) {
    }

    /** Routing keys con las que ESTE servicio publica. */
    public record Routing(String notificaciones, String tickets, String documentos) {
    }
}