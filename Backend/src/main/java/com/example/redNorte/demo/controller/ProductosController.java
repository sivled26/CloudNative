package com.example.redNorte.demo.controller;

import com.example.redNorte.demo.model.Productos;
import com.example.redNorte.demo.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductosController {
    private final ProductoService productoService;

    @PostMapping("/crear")
    public ResponseEntity<Productos> crear(@RequestBody Productos producto) {
        Productos creado= productoService.agregarProducto(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Productos> actualizar(@RequestBody Productos producto, @PathVariable Long id) {
        try {
            Productos actualizado= productoService.actualizarProducto(id, producto);
            return ResponseEntity.ok(actualizado);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Productos> eliminar(@PathVariable Long id) {
        try {
            productoService.eliminarProducto(id);
            return ResponseEntity.ok().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<Productos> obtenerPorId(@PathVariable Long id) {
        try{
            Productos producto = productoService.obtenerPorId(id);
            return ResponseEntity.ok(producto);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping
    public ResponseEntity<List<Productos>> listarTodos() {
        return ResponseEntity.ok(productoService.listarTodoS());
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<Productos>> buscarPorCategoria(@PathVariable String categoria) {
        return ResponseEntity.ok(productoService.buscarPorCategoria(categoria));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Productos>> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(productoService.buscarPorNombre(nombre));
    }
}
