package com.ejemplo.PruebaAPISpringBoot.controller;

import com.ejemplo.PruebaAPISpringBoot.dto.ProductDto;
import com.ejemplo.PruebaAPISpringBoot.service.ProductService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) { this.productService = productService; }

    @GetMapping(produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<ProductDto> listProducts() {
        return Flux.fromIterable(productService.getAllProducts())
                .onErrorResume(ex -> Flux.empty());
    }

    @GetMapping(path = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<ProductDto>>> listProductsAsList() {
        return Mono.fromCallable(() -> productService.getAllProducts())
                .onErrorResume(ex -> Mono.just(List.of()))
                .map(list -> ResponseEntity.ok(list));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ProductDto>> getProduct(@PathVariable Integer id) {
        return Mono.fromCallable(() -> productService.getProductById(id))
                .map(optional -> optional.map(product -> ResponseEntity.ok(product))
                        .orElse(ResponseEntity.notFound().build()));
    }
}