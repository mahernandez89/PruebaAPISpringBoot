package com.ejemplo.PruebaAPISpringBoot.controller;

import com.ejemplo.PruebaAPISpringBoot.dto.ProductDto;
import com.ejemplo.PruebaAPISpringBoot.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);
    private final ProductService productService;

    public ProductController(ProductService productService) { this.productService = productService; }

    // Reactivo: devuelve Mono<ResponseEntity<List<ProductDto>>> sin bloquear el hilo Netty
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<ProductDto>>> listProducts() {
        log.info("GET /api/products - listProducts");
        return productService.getAllProducts()
                .map(ResponseEntity::ok)
                .onErrorResume(ex -> {
                    log.error("listProducts - error", ex);
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }

    // Reactivo: devuelve Mono<ResponseEntity<ProductDto>> y 404 si no existe
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ProductDto>> productbyId(@PathVariable Integer id) {
        log.info("GET /api/products/{} - productbyId", id);
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}