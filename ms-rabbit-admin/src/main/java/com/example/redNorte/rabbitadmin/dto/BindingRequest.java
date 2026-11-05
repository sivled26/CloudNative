package com.example.redNorte.rabbitadmin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record BindingRequest(
        @NotBlank(message = "El destino (cola o exchange) es obligatorio")
        @Pattern(regexp = "^[A-Za-z0-9._-]*$", message = "El destino tiene caracteres invalidos")
        String destino,

        @NotNull(message = "tipoDestino es obligatorio (QUEUE o EXCHANGE)")
        TipoDestino tipoDestino,

        @NotBlank(message = "El exchange de origen es obligatorio")
        @Pattern(regexp = "^[A-Za-z0-9._-]*$", message = "El exchange tiene caracteres invalidos")
        String exchange,

        @Pattern(regexp = "^[A-Za-z0-9._*#-]*$", message = "La routingKey tiene caracteres invalidos")
        String routingKey) {

    public enum TipoDestino {
        QUEUE, EXCHANGE
    }
}