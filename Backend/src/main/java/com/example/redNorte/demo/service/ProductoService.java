package com.example.redNorte.demo.service;

import com.example.redNorte.demo.model.Productos;
import com.example.redNorte.demo.repository.PacientesRepository;
import com.example.redNorte.demo.repository.ProductosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ProductoService {
    private final ProductosRepository productosRepository;

    public Productos agregarProducto(Productos productos){
       return productosRepository.save(productos);
    }
    public Productos actualizarProducto(Long id, Productos productos){
        Productos existente = productosRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Producto no Encontrado con ID" + id));
                        existente.setNombre(productos.getNombre());
                        existente.setDescripcion(productos.getDescripcion());
                        existente.setCategoria(productos.getCategoria());
                        existente.setPrecio(productos.getPrecio());
                        existente.setImagenUrl(productos.getImagenUrl());
                        return productosRepository.save(existente);
    }

    public void eliminarProducto(Long id){
        if(!productosRepository.existsById(id)){
            throw new NoSuchElementException("Producto no encontrado con ID" + id);
        }
        productosRepository.deleteById(id);
    }
    @Transactional(readOnly = true)
    public Productos obtenerPorId(Long id) {
        return productosRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Producto no encontrado con id: " + id));
    }
    @Transactional(readOnly = true )
    public List<Productos> listarTodoS(){
        return productosRepository.findAll();
    }
    @Transactional (readOnly = true)
    public List<Productos> buscarPorCategoria(String categoria){
        return productosRepository.findByCategoriaIgnoreCase(categoria);
    }
    @Transactional(readOnly = true)
    public List<Productos> buscarPorNombre(String nombre){
        return productosRepository.findByNombreContainingIgnoreCase(nombre);
    }
}
