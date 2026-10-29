package com.example.redNorte.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * DTOs del administrador de RabbitMQ. Las anotaciones de validación impiden crear
 * colas, exchanges o bindings con nombres vacíos o configuraciones inválidas.
 */
public final class RabbitAdminDtos {

    /** Nombres válidos: letras, números, punto, guion y guion bajo. */
    public static final String NOMBRE_VALIDO = "^[a-zA-Z0-9._-]+$";
    private static final String MSG_NOMBRE = "solo puede contener letras, números, '.', '-' y '_'";

    /** POST /api/rabbit/queues */
    public record CrearColaRequest(
            @NotBlank(message = "El nombre de la cola es obligatorio")
            @Size(max = 255, message = "El nombre de la cola no puede superar 255 caracteres")
            @Pattern(regexp = NOMBRE_VALIDO, message = "El nombre de la cola " + MSG_NOMBRE)
            String nombre,

            /** true por defecto: la cola sobrevive a un reinicio de RabbitMQ. */
            Boolean durable,

            /** Opcional: exchange al que se envían los mensajes rechazados (DLX). */
            @Pattern(regexp = NOMBRE_VALIDO, message = "El dead letter exchange " + MSG_NOMBRE)
            String deadLetterExchange,

            /** Opcional: routing key con la que se envían los mensajes rechazados. */
            @Pattern(regexp = NOMBRE_VALIDO, message = "La dead letter routing key " + MSG_NOMBRE)
            String deadLetterRoutingKey,

            /** Opcional: tiempo de vida de los mensajes en milisegundos. */
            @Positive(message = "El TTL debe ser un número mayor que 0")
            Integer ttlMs) {
    }

    /** POST /api/rabbit/exchanges */
    public record CrearExchangeRequest(
            @NotBlank(message = "El nombre del exchange es obligatorio")
            @Size(max = 255, message = "El nombre del exchange no puede superar 255 caracteres")
            @Pattern(regexp = NOMBRE_VALIDO, message = "El nombre del exchange " + MSG_NOMBRE)
            String nombre,

            @NotBlank(message = "El tipo de exchange es obligatorio")
            @Pattern(regexp = "direct|topic|fanout|headers",
                    message = "El tipo de exchange debe ser direct, topic, fanout o headers")
            String tipo,

            Boolean durable) {
    }

    /** POST y DELETE /api/rabbit/bindings */
    public record BindingRequest(
            @NotBlank(message = "El nombre de la cola es obligatorio")
            @Pattern(regexp = NOMBRE_VALIDO, message = "El nombre de la cola " + MSG_NOMBRE)
            String cola,

            @NotBlank(message = "El nombre del exchange es obligatorio")
            @Pattern(regexp = NOMBRE_VALIDO, message = "El nombre del exchange " + MSG_NOMBRE)
            String exchange,

            /** Puede ir vacía (fanout). En topic se permiten los comodines * y #. */
            @Size(max = 255, message = "La routing key no puede superar 255 caracteres")
            @Pattern(regexp = "^[a-zA-Z0-9._*#-]*$",
                    message = "La routing key solo puede contener letras, números, '.', '-', '_', '*' y '#'")
            String routingKey) {
    }

    /** Respuesta con el estado de una cola. */
           public record ColaInfo(String nombre, long mensajes, long consumidores) {
    }

    private RabbitAdminDtos() {
    }
}
