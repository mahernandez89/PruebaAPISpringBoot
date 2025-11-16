package com.ejemplo.PruebaAPISpringBoot.service.query;

import com.ejemplo.PruebaAPISpringBoot.dto.ProductDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ProductQueryService {
    Mono<List<ProductDto>> getAllProducts();
    Mono<ProductDto> getProductById(Integer id);
}
