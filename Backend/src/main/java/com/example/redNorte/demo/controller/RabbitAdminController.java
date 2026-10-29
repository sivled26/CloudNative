package com.example.redNorte.demo.controller;

import com.example.redNorte.demo.dto.RabbitAdminDtos.BindingRequest;
import com.example.redNorte.demo.dto.RabbitAdminDtos.ColaInfo;
import com.example.redNorte.demo.dto.RabbitAdminDtos.CrearColaRequest;
import com.example.redNorte.demo.dto.RabbitAdminDtos.CrearExchangeRequest;
import com.example.redNorte.demo.service.RabbitAdminService;
import jakarta.validation.Valid;
import org.springframework.amqp.AmqpException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * API REST del administrador de RabbitMQ.
 *
 * POST   /api/rabbit/queues            crea una cola
 * GET    /api/rabbit/queues/{nombre}   muestra mensajes y consumidores de una cola
 * DELETE /api/rabbit/queues/{nombre}   elimina una cola
 * POST   /api/rabbit/exchanges         crea un exchange (direct, topic, fanout o headers)
 * DELETE /api/rabbit/exchanges/{nombre} elimina un exchange
 * POST   /api/rabbit/bindings          une una cola a un exchange con una routing key
 * DELETE /api/rabbit/bindings          elimina esa unión
 *
 * Toda la lógica está en RabbitAdminService; aquí solo se reciben y validan los datos.
 */
@RestController
@RequestMapping("/api/rabbit")
public class RabbitAdminController {

    private final RabbitAdminService rabbitAdminService;

    public RabbitAdminController(RabbitAdminService rabbitAdminService) {
        this.rabbitAdminService = rabbitAdminService;
    }

    @PostMapping("/queues")
    public ResponseEntity<ColaInfo> crearCola(@Valid @RequestBody CrearColaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rabbitAdminService.crearCola(request));
    }

    @GetMapping("/queues/{nombre}")
    public ColaInfo obtenerCola(@PathVariable String nombre) {
        return rabbitAdminService.obtenerCola(nombre);
    }

    @DeleteMapping("/queues/{nombre}")
    public ResponseEntity<Void> eliminarCola(@PathVariable String nombre) {
        rabbitAdminService.eliminarCola(nombre);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/exchanges")
    public ResponseEntity<Map<String, String>> crearExchange(@Valid @RequestBody CrearExchangeRequest request) {
        rabbitAdminService.crearExchange(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("exchange", request.nombre(), "tipo", request.tipo()));
    }

    @DeleteMapping("/exchanges/{nombre}")
    public ResponseEntity<Void> eliminarExchange(@PathVariable String nombre) {
        rabbitAdminService.eliminarExchange(nombre);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bindings")
    public ResponseEntity<BindingRequest> crearBinding(@Valid @RequestBody BindingRequest request) {
        rabbitAdminService.crearBinding(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(request);
    }

    @DeleteMapping("/bindings")
    public ResponseEntity<Void> eliminarBinding(@Valid @RequestBody BindingRequest request) {
        rabbitAdminService.eliminarBinding(request);
        return ResponseEntity.noContent().build();
    }

    // ---------- Errores con mensajes claros ----------

    /** Falla de las anotaciones de validación (@NotBlank, @Pattern...): 400 con el detalle por campo. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> datosInvalidos(MethodArgumentNotValidException e) {
        Map<String, String> errores = new LinkedHashMap<>();
        for (FieldError campo : e.getBindingResult().getFieldErrors()) {
            errores.putIfAbsent(campo.getField(), campo.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(Map.of(
                "message", "Los datos enviados no son válidos",
                "errores", errores));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> argumentoInvalido(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("message", String.valueOf(e.getMessage())));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> noEncontrado(NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", String.valueOf(e.getMessage())));
    }

    /** RabbitMQ rechazó la operación (por ejemplo, una cola que ya existe con otra configuración). */
    @ExceptionHandler(AmqpException.class)
    public ResponseEntity<Map<String, String>> errorRabbit(AmqpException e) {
        String detalle = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "message", "RabbitMQ rechazó la operación. Revisa que la cola o el exchange existan "
                        + "y que no exista ya con otra configuración.",
                "detalle", String.valueOf(detalle)));
    }
}
