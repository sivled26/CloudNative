package com.example.redNorte.notificaciones.consumer.common;

/** Error NO recuperable: reintentar no sirve, el mensaje va directo a la DLQ. */
public class MensajeInvalidoException extends RuntimeException {

    public MensajeInvalidoException(String mensaje) {
        super(mensaje);
    }
}