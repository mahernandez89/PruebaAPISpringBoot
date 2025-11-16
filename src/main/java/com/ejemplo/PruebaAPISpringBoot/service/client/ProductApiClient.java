package com.ejemplo.PruebaAPISpringBoot.service.client;

import com.ejemplo.PruebaAPISpringBoot.dto.ProductDto;
import reactor.core.publisher.Mono;
import java.util.List;

public interface ProductApiClient {
    Mono<List<ProductDto>> fetchAll();
    Mono<ProductDto> fetchById(Integer id);
}
