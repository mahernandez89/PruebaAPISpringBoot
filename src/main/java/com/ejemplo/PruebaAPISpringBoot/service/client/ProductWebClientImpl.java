package com.ejemplo.PruebaAPISpringBoot.service.client;

import com.ejemplo.PruebaAPISpringBoot.dto.ProductDto;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Component
public class ProductWebClientImpl implements ProductApiClient {

    private final WebClient webClient;
    private final String EXTERNAL_BASE = "https://fakestoreapi.com";

    public ProductWebClientImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Mono<List<ProductDto>> fetchAll() {
        return webClient.get()
                .uri(EXTERNAL_BASE + "/products")
                .retrieve()
                .bodyToMono(ProductDto[].class)
                .timeout(Duration.ofSeconds(6))
                .map(Arrays::asList)
                .onErrorResume(ex -> Mono.just(Arrays.asList(createSampleArray())));
    }

    @Override
    public Mono<ProductDto> fetchById(Integer id) {
        return webClient.get()
                .uri(EXTERNAL_BASE + "/products/{id}", id)
                .retrieve()
                .bodyToMono(ProductDto.class)
                .timeout(Duration.ofSeconds(6))
                .onErrorResume(ex -> Mono.empty());
    }

    private ProductDto[] createSampleArray() {
        ProductDto p1 = new ProductDto(); p1.setId(1); p1.setTitle("Sample Product 1"); p1.setPrice(19.99); p1.setDescription("Sample description 1"); p1.setCategory("Sample category");
        ProductDto p2 = new ProductDto(); p2.setId(2); p2.setTitle("Sample Product 2"); p2.setPrice(29.99); p2.setDescription("Sample description 2"); p2.setCategory("Sample category");
        return new ProductDto[]{p1, p2};
    }
}