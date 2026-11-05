package com.example.redNorte.rabbitadmin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/** Mensaje de prueba para publicar en un exchange (sirve para la demo de colas y DLQ). */
public record MensajeRequest(
        @NotBlank(message = "El exchange es obligatorio")
        @Pattern(regexp = "^[A-Za-z0-9._-]*$", message = "El exchange tiene caracteres invalidos")
        String exchange,

        @Pattern(regexp = "^[A-Za-z0-9._-]*$", message = "La routingKey tiene caracteres invalidos")
        String routingKey,

        @NotBlank(message = "El mensaje no puede estar vacio")
        String mensaje) {
}