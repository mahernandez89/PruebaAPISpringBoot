package com.ejemplo.PruebaAPISpringBoot.service;

import com.ejemplo.PruebaAPISpringBoot.dto.ProductDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final WebClient webClient;
    private final String EXTERNAL_BASE = "https://fakestoreapi.com";

    public ProductService(WebClient webClient) {
        this.webClient = webClient;
    }

    // Mantengo getAllProducts (opcional)
    public List<ProductDto> getAllProducts() {
        try {
            ProductDto[] arr = webClient.get()
                    .uri(EXTERNAL_BASE + "/products")
                    .retrieve()
                    .onStatus(status -> status.isError(), response -> response.createException())
                    .bodyToMono(ProductDto[].class)
                    .timeout(Duration.ofSeconds(6))
                    .onErrorResume(ex -> {
                        log.warn("getAllProducts - fallo al llamar a fakestoreapi: {}", ex.toString());
                        return Mono.just(new ProductDto[0]);
                    })
                    .block();
            return Arrays.asList(arr != null ? arr : new ProductDto[0]);
        } catch (Exception ex) {
            log.error("getAllProducts - excepción no controlada", ex);
            return Collections.emptyList();
        }
    }

    // Método recomendado para obtener un producto: maneja timeouts y errores y devuelve Optional
    public Optional<ProductDto> getProductById(Integer id) {
        try {
            ProductDto dto = webClient.get()
                    .uri(EXTERNAL_BASE + "/products/{id}", id)
                    .retrieve()
                    .onStatus(status -> status.isError(), response -> response.createException())
                    .bodyToMono(ProductDto.class)
                    .timeout(Duration.ofSeconds(6))
                    .onErrorResume(ex -> {
                        // loguea la causa (connectividad / timeout / HTTP error)
                        log.warn("getProductById({}) - error calling fakestoreapi: {}", id, ex.toString());
                        return Mono.empty();
                    })
                    .block(); 
            return Optional.ofNullable(dto);
        } catch (Exception ex) {
            log.error("getProductById({}) - excepción no controlada:", id, ex);
            return Optional.empty();
        }
    }

    // Si necesitas fallback rápido: buscar en la lista completa en memoria (si ya cacheaste)
    public Optional<ProductDto> findInListFallback(Integer id, List<ProductDto> cached) {
        if (cached == null) return Optional.empty();
        return cached.stream().filter(p -> p.getId() != null && p.getId().equals(id)).findFirst();
    }
}