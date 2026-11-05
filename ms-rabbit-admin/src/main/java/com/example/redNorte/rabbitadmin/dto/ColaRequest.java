package com.example.redNorte.rabbitadmin.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ColaRequest(
        @NotBlank(message = "El nombre de la cola es obligatorio")
        @Size(max = 255, message = "El nombre de la cola no puede superar 255 caracteres")
        @Pattern(regexp = "^[A-Za-z0-9._-]*$",
                message = "El nombre solo puede tener letras, numeros, punto, guion y guion bajo")
        String nombre,

        Boolean durable,

        Tipo tipo,

        @Pattern(regexp = "^[A-Za-z0-9._-]*$", message = "deadLetterExchange tiene caracteres invalidos")
        String deadLetterExchange,

        @Pattern(regexp = "^[A-Za-z0-9._*#-]*$", message = "deadLetterRoutingKey tiene caracteres invalidos")
        String deadLetterRoutingKey,

        @Min(value = 1, message = "ttlMs debe ser mayor a 0")
        @Max(value = 2_000_000_000, message = "ttlMs demasiado grande")
        Integer ttlMs) {

    public enum Tipo {
        CLASSIC, QUORUM
    }
}