package com.example.redNorte.rabbitadmin.service;

import com.example.redNorte.rabbitadmin.dto.BindingRequest;
import com.example.redNorte.rabbitadmin.dto.ColaRequest;
import com.example.redNorte.rabbitadmin.dto.ColaResponse;
import com.example.redNorte.rabbitadmin.dto.ExchangeRequest;
import com.example.redNorte.rabbitadmin.exception.ConflictoException;
import com.example.redNorte.rabbitadmin.exception.RecursoNoEncontradoException;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.QueueInformation;
import org.springframework.stereotype.Service;

/**
 * Toda la logica de creacion y eliminacion de colas, exchanges y bindings vive aqui.
 * Los controladores solo llaman a estos metodos y no conocen la libreria de RabbitMQ.
 */
@Service
public class RabbitAdminService {

    private static final String PATRON_NOMBRE = "^[A-Za-z0-9._-]{1,255}$";

    private final AmqpAdmin amqpAdmin;

    public RabbitAdminService(AmqpAdmin amqpAdmin) {
        this.amqpAdmin = amqpAdmin;
    }

    // ------------------------------------------------------------------ Colas
    public ColaResponse crearCola(ColaRequest r) {
        String nombre = validarNombre(r.nombre(), "cola");
        boolean durable = r.durable() == null || r.durable();
        ColaRequest.Tipo tipo = r.tipo() == null ? ColaRequest.Tipo.CLASSIC : r.tipo();

        if (tipo == ColaRequest.Tipo.QUORUM && !durable) {
            throw new IllegalArgumentException("Una cola quorum debe ser durable");
        }
        if (!vacio(r.deadLetterRoutingKey()) && vacio(r.deadLetterExchange())) {
            throw new IllegalArgumentException("deadLetterRoutingKey requiere deadLetterExchange");
        }
        if (amqpAdmin.getQueueInfo(nombre) != null) {
            throw new ConflictoException("La cola '" + nombre + "' ya existe");
        }

        QueueBuilder builder = durable ? QueueBuilder.durable(nombre) : QueueBuilder.nonDurable(nombre);
        if (tipo == ColaRequest.Tipo.QUORUM) {
            builder.quorum();
        }
        if (!vacio(r.deadLetterExchange())) {
            builder.deadLetterExchange(r.deadLetterExchange());
        }
        if (!vacio(r.deadLetterRoutingKey())) {
            builder.deadLetterRoutingKey(r.deadLetterRoutingKey());
        }
        if (r.ttlMs() != null) {
            builder.ttl(r.ttlMs());
        }
        amqpAdmin.declareQueue(builder.build());
        return obtenerCola(nombre);
    }

    public ColaResponse obtenerCola(String nombre) {
        validarNombre(nombre, "cola");
        QueueInformation info = amqpAdmin.getQueueInfo(nombre);
        if (info == null) {
            throw new RecursoNoEncontradoException("La cola '" + nombre + "' no existe");
        }
        return new ColaResponse(info.getName(), info.getMessageCount(), info.getConsumerCount());
    }

    public void eliminarCola(String nombre) {
        validarNombre(nombre, "cola");
        if (amqpAdmin.getQueueInfo(nombre) == null) {
            throw new RecursoNoEncontradoException("La cola '" + nombre + "' no existe");
        }
        amqpAdmin.deleteQueue(nombre);
    }

    // ------------------------------------------------------------------ Exchanges
    public void crearExchange(ExchangeRequest r) {
        String nombre = validarNombre(r.nombre(), "exchange");
        boolean durable = r.durable() == null || r.durable();
        ExchangeBuilder builder = switch (r.tipo()) {
            case DIRECT -> ExchangeBuilder.directExchange(nombre);
            case TOPIC -> ExchangeBuilder.topicExchange(nombre);
            case FANOUT -> ExchangeBuilder.fanoutExchange(nombre);
            case HEADERS -> ExchangeBuilder.headersExchange(nombre);
        };
        Exchange exchange = builder.durable(durable).build();
        amqpAdmin.declareExchange(exchange);
    }

    public void eliminarExchange(String nombre) {
        validarNombre(nombre, "exchange");
        amqpAdmin.deleteExchange(nombre);
    }

    // ------------------------------------------------------------------ Bindings
    public void crearBinding(BindingRequest r) {
        validarNombre(r.destino(), "destino");
        validarNombre(r.exchange(), "exchange");
        if (r.tipoDestino() == BindingRequest.TipoDestino.QUEUE && amqpAdmin.getQueueInfo(r.destino()) == null) {
            throw new RecursoNoEncontradoException("La cola '" + r.destino() + "' no existe");
        }
        amqpAdmin.declareBinding(construirBinding(r.destino(), r.tipoDestino(), r.exchange(), r.routingKey()));
    }

    public void eliminarBinding(String destino, BindingRequest.TipoDestino tipoDestino,
                                String exchange, String routingKey) {
        validarNombre(destino, "destino");
        validarNombre(exchange, "exchange");
        amqpAdmin.removeBinding(construirBinding(destino, tipoDestino, exchange, routingKey));
    }

    // ------------------------------------------------------------------ Helpers
    private Binding construirBinding(String destino, BindingRequest.TipoDestino tipo,
                                     String exchange, String routingKey) {
        Binding.DestinationType tipoDestino = Binding.DestinationType.valueOf(tipo.name());
        return new Binding(destino, tipoDestino, exchange, routingKey == null ? "" : routingKey, null);
    }

    private String validarNombre(String nombre, String entidad) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de " + entidad + " no puede estar vacio");
        }
        if (!nombre.matches(PATRON_NOMBRE)) {
            throw new IllegalArgumentException("El nombre de " + entidad
                    + " solo puede tener letras, numeros, punto, guion y guion bajo (max. 255)");
        }
        if (nombre.startsWith("amq.")) {
            throw new IllegalArgumentException("Los nombres que empiezan con 'amq.' estan reservados por RabbitMQ");
        }
        return nombre;
    }

    private boolean vacio(String valor) {
        return valor == null || valor.isBlank();
    }
}