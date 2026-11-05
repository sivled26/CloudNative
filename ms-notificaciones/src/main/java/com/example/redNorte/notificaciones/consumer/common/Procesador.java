package com.example.redNorte.notificaciones.consumer.common;

@FunctionalInterface
public interface Procesador {

    void procesar() throws Exception;
}