package com.example.redNorte.rabbitadmin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ExchangeRequest(
        @NotBlank(message = "El nombre del exchange es obligatorio")
        @Size(max = 255, message = "El nombre del exchange no puede superar 255 caracteres")
        @Pattern(regexp = "^[A-Za-z0-9._-]*$",
                message = "El nombre solo puede tener letras, numeros, punto, guion y guion bajo")
        String nombre,

        @NotNull(message = "El tipo de exchange es obligatorio (DIRECT, TOPIC, FANOUT, HEADERS)")
        Tipo tipo,

        Boolean durable) {

    public enum Tipo {
        DIRECT, TOPIC, FANOUT, HEADERS
    }
}