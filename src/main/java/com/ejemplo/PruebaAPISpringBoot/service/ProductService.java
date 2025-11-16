package com.ejemplo.PruebaAPISpringBoot.service;

import com.ejemplo.PruebaAPISpringBoot.dto.ProductDto;
import com.ejemplo.PruebaAPISpringBoot.service.client.ProductApiClient;
import com.ejemplo.PruebaAPISpringBoot.service.query.ProductQueryService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
public class ProductService implements ProductQueryService {

    private final ProductApiClient productApiClient;

    public ProductService(ProductApiClient productApiClient) {
        this.productApiClient = productApiClient;
    }

    @Override
    public Mono<java.util.List<ProductDto>> getAllProducts() {
        return productApiClient.fetchAll();
    }

    @Override
    public Mono<ProductDto> getProductById(Integer id) {
        return productApiClient.fetchById(id);
    }


}