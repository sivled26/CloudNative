package com.example.redNorte.demo.service;

import com.example.redNorte.demo.config.RabbitMQConfig;
import com.example.redNorte.demo.dto.RabbitAdminDtos.BindingRequest;
import com.example.redNorte.demo.dto.RabbitAdminDtos.ColaInfo;
import com.example.redNorte.demo.dto.RabbitAdminDtos.CrearColaRequest;
import com.example.redNorte.demo.dto.RabbitAdminDtos.CrearExchangeRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.HeadersExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.QueueInformation;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Encapsula toda la administración de RabbitMQ: crear y eliminar colas, exchanges y bindings.
 * El controlador solo llama a estos métodos y no conoce los detalles de la librería de RabbitMQ.
 */
@Service
public class RabbitAdminService {

    private static final Logger log = LoggerFactory.getLogger(RabbitAdminService.class);

    /** Colas y exchanges que usa el propio sistema: no se pueden eliminar desde la API. */
    private static final Set<String> PROTEGIDOS = Set.of(
            RabbitMQConfig.COLA, RabbitMQConfig.DLX, RabbitMQConfig.DLQ);

    private final AmqpAdmin amqpAdmin;

    public RabbitAdminService(AmqpAdmin amqpAdmin) {
        this.amqpAdmin = amqpAdmin;
    }

    // ---------- Colas ----------

    public ColaInfo crearCola(CrearColaRequest req) {
        validarNoReservado(req.nombre());
        QueueBuilder builder = Boolean.FALSE.equals(req.durable())
                ? QueueBuilder.nonDurable(req.nombre())
                : QueueBuilder.durable(req.nombre());
        if (tieneTexto(req.deadLetterExchange())) {
            builder.deadLetterExchange(req.deadLetterExchange());
        }
        if (tieneTexto(req.deadLetterRoutingKey())) {
            if (!tieneTexto(req.deadLetterExchange())) {
                throw new IllegalArgumentException(
                        "Para usar deadLetterRoutingKey también debes indicar deadLetterExchange");
            }
            builder.deadLetterRoutingKey(req.deadLetterRoutingKey());
        }
        if (req.ttlMs() != null) {
            builder.ttl(req.ttlMs());
        }
        Queue cola = builder.build();
        amqpAdmin.declareQueue(cola);
        log.info("[RabbitAdmin] Cola creada: {} (durable={})", cola.getName(), cola.isDurable());
        return obtenerCola(cola.getName());
    }

    public ColaInfo obtenerCola(String nombre) {
        validarNombre(nombre, "cola");
        QueueInformation info = amqpAdmin.getQueueInfo(nombre);
        if (info == null) {
            throw new NoSuchElementException("La cola '" + nombre + "' no existe");
        }
        return new ColaInfo(info.getName(), info.getMessageCount(), info.getConsumerCount());
    }

    public void eliminarCola(String nombre) {
        validarNombre(nombre, "cola");
        validarNoProtegido(nombre);
        obtenerCola(nombre); // lanza 404 si no existe
        amqpAdmin.deleteQueue(nombre);
        log.info("[RabbitAdmin] Cola eliminada: {}", nombre);
    }

    // ---------- Exchanges ----------

    public void crearExchange(CrearExchangeRequest req) {
        validarNoReservado(req.nombre());
        boolean durable = !Boolean.FALSE.equals(req.durable());
        Exchange exchange = switch (req.tipo()) {
            case "direct" -> new DirectExchange(req.nombre(), durable, false);
            case "topic" -> new TopicExchange(req.nombre(), durable, false);
            case "fanout" -> new FanoutExchange(req.nombre(), durable, false);
            case "headers" -> new HeadersExchange(req.nombre(), durable, false);
            default -> throw new IllegalArgumentException("Tipo de exchange no soportado: " + req.tipo());
        };
        amqpAdmin.declareExchange(exchange);
        log.info("[RabbitAdmin] Exchange creado: {} (tipo={}, durable={})", req.nombre(), req.tipo(), durable);
    }

    public void eliminarExchange(String nombre) {
        validarNombre(nombre, "exchange");
        validarNoReservado(nombre);
        validarNoProtegido(nombre);
        amqpAdmin.deleteExchange(nombre);
        log.info("[RabbitAdmin] Exchange eliminado: {}", nombre);
    }

    // ---------- Bindings ----------

    public void crearBinding(BindingRequest req) {
        amqpAdmin.declareBinding(aBinding(req));
        log.info("[RabbitAdmin] Binding creado: {} -> {} (routingKey='{}')",
                req.exchange(), req.cola(), routingKey(req));
    }

    public void eliminarBinding(BindingRequest req) {
        if (PROTEGIDOS.contains(req.cola()) && PROTEGIDOS.contains(req.exchange())) {
            throw new IllegalArgumentException("Ese binding es del sistema y no se puede eliminar");
        }
        amqpAdmin.removeBinding(aBinding(req));
        log.info("[RabbitAdmin] Binding eliminado: {} -> {} (routingKey='{}')",
                req.exchange(), req.cola(), routingKey(req));
    }

    // ---------- Validaciones internas ----------

    private Binding aBinding(BindingRequest req) {
        return new Binding(req.cola(), Binding.DestinationType.QUEUE, req.exchange(), routingKey(req), null);
    }

    private static String routingKey(BindingRequest req) {
        return req.routingKey() == null ? "" : req.routingKey();
    }

    private static void validarNombre(String nombre, String tipo) {
        if (!tieneTexto(nombre) || !nombre.matches(com.example.redNorte.demo.dto.RabbitAdminDtos.NOMBRE_VALIDO)) {
            throw new IllegalArgumentException("Nombre de " + tipo + " inválido: '" + nombre + "'");
        }
    }

    /** Los nombres que empiezan con "amq." están reservados por RabbitMQ. */
    private static void validarNoReservado(String nombre) {
        if (nombre.startsWith("amq.")) {
            throw new IllegalArgumentException("Los nombres que empiezan con 'amq.' están reservados por RabbitMQ");
        }
    }

    private static void validarNoProtegido(String nombre) {
        if (PROTEGIDOS.contains(nombre)) {
            throw new IllegalArgumentException("'" + nombre + "' es usado por el sistema y no se puede eliminar");
        }
    }

    private static boolean tieneTexto(String valor) {
        return valor != null && !valor.isBlank();
    }
}
