package com.example.redNorte.notificaciones.consumer.common;

import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class BaseConsumerTest {

    static class ConsumerDePrueba extends BaseConsumer {
        void ejecutar(Message m, Channel c, Procesador p) throws IOException {
            manejar("TEST", "payload", m, c, 1L, p);
        }
    }

    private Message mensaje(boolean reentregado) {
        MessageProperties props = new MessageProperties();
        props.setRedelivered(reentregado);
        return new Message("x".getBytes(), props);
    }

    @Test
    void haceAckCuandoTodoSaleBien() throws Exception {
        Channel ch = mock(Channel.class);
        new ConsumerDePrueba().ejecutar(mensaje(false), ch, () -> { });
        verify(ch).basicAck(1L, false);
    }

    @Test
    void mensajeInvalidoVaADlqSinRequeue() throws Exception {
        Channel ch = mock(Channel.class);
        new ConsumerDePrueba().ejecutar(mensaje(false), ch, () -> {
            throw new MensajeInvalidoException("malo");
        });
        verify(ch).basicNack(1L, false, false);
    }

    @Test
    void falloTransitorioHaceRequeueEnLaPrimeraEntrega() throws Exception {
        Channel ch = mock(Channel.class);
        new ConsumerDePrueba().ejecutar(mensaje(false), ch, () -> {
            throw new IllegalStateException("transitorio");
        });
        verify(ch).basicNack(1L, false, true);
    }

    @Test
    void falloRepetidoVaADlq() throws Exception {
        Channel ch = mock(Channel.class);
        new ConsumerDePrueba().ejecutar(mensaje(true), ch, () -> {
            throw new IllegalStateException("transitorio");
        });
        verify(ch).basicNack(1L, false, false);
    }
}