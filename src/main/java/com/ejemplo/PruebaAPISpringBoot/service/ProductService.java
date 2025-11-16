package com.ejemplo.PruebaAPISpringBoot.service;

import com.ejemplo.PruebaAPISpringBoot.dto.ProductDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.Arrays;
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

    // Reactive: devuelve Mono<List<ProductDto>>
    public Mono<List<ProductDto>> getAllProducts() {
        return webClient.get()
                .uri(EXTERNAL_BASE + "/products")
                .retrieve()
                .bodyToMono(ProductDto[].class)
                .timeout(Duration.ofSeconds(6))
                .map(Arrays::asList)
                .onErrorResume(ex -> {
                    log.warn("getAllProducts - fallo a fakestoreapi: {}, devolviendo lista vacía", ex.toString());
                    return Mono.just(Arrays.asList(createSampleArray()));
                });
    }

    // Reactive: devuelve Mono<ProductDto>
    public Mono<ProductDto> getProductById(Integer id) {
        return webClient.get()
                .uri(EXTERNAL_BASE + "/products/{id}", id)
                .retrieve()
                .bodyToMono(ProductDto.class)
                .timeout(Duration.ofSeconds(6))
                .onErrorResume(ex -> {
                    log.warn("getProductById({}) - fallo a fakestoreapi: {}", id, ex.toString());
                    return Mono.empty();
                });
    }

    // Blocking helper seguro: ejecuta la llamada reactiva en boundedElastic y bloquea ahí
    public Optional<ProductDto> getProductByIdBlocking(Integer id) {
        try {
            return getProductById(id)
                    .subscribeOn(Schedulers.boundedElastic())
                    .blockOptional();
        } catch (Exception ex) {
            log.warn("getProductByIdBlocking({}) - exception: {}", id, ex.toString());
            return Optional.empty();
        }
    }

    // Blocking helper para lista completa (si alguien necesita la versión sin Mono)
    public List<ProductDto> getAllProductsBlocking() {
        try {
            return getAllProducts()
                    .subscribeOn(Schedulers.boundedElastic())
                    .block();
        } catch (Exception ex) {
            log.warn("getAllProductsBlocking - exception: {}", ex.toString());
            return Arrays.asList(createSampleArray());
        }
    }

    private ProductDto[] createSampleArray() {
        ProductDto p1 = new ProductDto();
        p1.setId(1);
        p1.setTitle("Sample Product 1");
        p1.setPrice(19.99);
        p1.setDescription("Sample description 1");
        p1.setCategory("Sample category");

        ProductDto p2 = new ProductDto();
        p2.setId(2);
        p2.setTitle("Sample Product 2");
        p2.setPrice(29.99);
        p2.setDescription("Sample description 2");
        p2.setCategory("Sample category");

        return new ProductDto[]{p1, p2};
    }
}