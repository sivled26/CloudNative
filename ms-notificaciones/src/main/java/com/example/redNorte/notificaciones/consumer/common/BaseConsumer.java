package com.example.redNorte.notificaciones.consumer.common;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;

import java.io.IOException;

/**
 * Manejo comun de confirmacion (ACK) y errores para todos los consumidores.
 *
 * Decisiones:
 *  - OK                              -> ACK
 *  - MensajeInvalidoException        -> NACK sin requeue  -> el broker lo envia a la DLQ (se registra en log)
 *  - Otro error, primera entrega     -> NACK con requeue  (1 reintento)
 *  - Otro error, mensaje reentregado -> NACK sin requeue  -> DLQ (se registra en log)
 */
public abstract class BaseConsumer {

    private static final Logger log = LoggerFactory.getLogger(BaseConsumer.class);

    protected void manejar(String tipo, String payload, Message message, Channel channel,
                           long tag, Procesador procesador) throws IOException {
        boolean reentregado = message.getMessageProperties().isRedelivered();
        try {
            procesador.procesar();
            channel.basicAck(tag, false);
            log.info("[{}] procesado OK -> ACK. payload={}", tipo, payload);
        } catch (MensajeInvalidoException e) {
            log.error("[{}] mensaje invalido (no recuperable) -> NACK sin requeue, va a DLQ. motivo={} payload={}",
                    tipo, e.getMessage(), payload);
            channel.basicNack(tag, false, false);
        } catch (Exception e) {
            if (!reentregado) {
                log.warn("[{}] fallo recuperable -> NACK con requeue (1 reintento). motivo={} payload={}",
                        tipo, e.getMessage(), payload);
                channel.basicNack(tag, false, true);
            } else {
                log.error("[{}] fallo repetido -> NACK sin requeue, va a DLQ. motivo={} payload={}",
                        tipo, e.getMessage(), payload);
                channel.basicNack(tag, false, false);
            }
        }
    }

    /**
     * Validacion y simulacion de fallos para la demo:
     *  - cuerpo vacio o con la palabra FALLAR  -> error no recuperable (directo a DLQ)
     *  - cuerpo con REINTENTAR                 -> falla la 1a vez y funciona en el reintento
     *  - cuerpo con ERROR_SIEMPRE              -> falla siempre (reintento y luego DLQ)
     */
    protected void validar(String payload, Message message) {
        if (payload == null || payload.isBlank()) {
            throw new MensajeInvalidoException("el cuerpo del mensaje esta vacio");
        }
        if (payload.contains("FALLAR")) {
            throw new MensajeInvalidoException("mensaje marcado como invalido");
        }
        boolean reentregado = message.getMessageProperties().isRedelivered();
        if (payload.contains("REINTENTAR") && !reentregado) {
            throw new IllegalStateException("fallo transitorio simulado");
        }
        if (payload.contains("ERROR_SIEMPRE")) {
            throw new IllegalStateException("fallo persistente simulado");
        }
    }
}