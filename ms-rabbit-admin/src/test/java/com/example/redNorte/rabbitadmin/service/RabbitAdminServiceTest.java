package com.example.redNorte.rabbitadmin.service;

import com.example.redNorte.rabbitadmin.dto.BindingRequest;
import com.example.redNorte.rabbitadmin.dto.ColaRequest;
import com.example.redNorte.rabbitadmin.dto.ExchangeRequest;
import com.example.redNorte.rabbitadmin.exception.ConflictoException;
import com.example.redNorte.rabbitadmin.exception.RecursoNoEncontradoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueInformation;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RabbitAdminServiceTest {

    @Mock
    AmqpAdmin amqpAdmin;

    @InjectMocks
    RabbitAdminService service;

    @Test
    void creaColaValida() {
        when(amqpAdmin.getQueueInfo("q.test"))
                .thenReturn(null, new QueueInformation("q.test", 0, 0));

        service.crearCola(new ColaRequest("q.test", true, ColaRequest.Tipo.CLASSIC, null, null, null));

        verify(amqpAdmin).declareQueue(any(Queue.class));
    }

    @Test
    void colaExistenteLanzaConflicto() {
        when(amqpAdmin.getQueueInfo("q.test")).thenReturn(new QueueInformation("q.test", 0, 0));

        assertThrows(ConflictoException.class, () ->
                service.crearCola(new ColaRequest("q.test", true, null, null, null, null)));
        verify(amqpAdmin, never()).declareQueue(any(Queue.class));
    }

    @Test
    void nombreVacioEsRechazado() {
        assertThrows(IllegalArgumentException.class, () ->
                service.crearCola(new ColaRequest("   ", true, null, null, null, null)));
    }

    @Test
    void nombreReservadoEsRechazado() {
        assertThrows(IllegalArgumentException.class, () ->
                service.crearCola(new ColaRequest("amq.interna", true, null, null, null, null)));
    }

    @Test
    void colaQuorumNoDurableEsRechazada() {
        assertThrows(IllegalArgumentException.class, () ->
                service.crearCola(new ColaRequest("q.test", false, ColaRequest.Tipo.QUORUM, null, null, null)));
    }

    @Test
    void eliminarColaInexistenteLanzaNoEncontrado() {
        when(amqpAdmin.getQueueInfo("q.nada")).thenReturn(null);

        assertThrows(RecursoNoEncontradoException.class, () -> service.eliminarCola("q.nada"));
    }

    @Test
    void creaExchangeTopic() {
        service.crearExchange(new ExchangeRequest("ex.test", ExchangeRequest.Tipo.TOPIC, true));

        verify(amqpAdmin).declareExchange(any(Exchange.class));
    }

    @Test
    void creaBindingHaciaColaExistente() {
        when(amqpAdmin.getQueueInfo("q.test")).thenReturn(new QueueInformation("q.test", 0, 0));

        service.crearBinding(new BindingRequest("q.test", BindingRequest.TipoDestino.QUEUE, "ex.test", "k"));

        verify(amqpAdmin).declareBinding(any(Binding.class));
    }
}