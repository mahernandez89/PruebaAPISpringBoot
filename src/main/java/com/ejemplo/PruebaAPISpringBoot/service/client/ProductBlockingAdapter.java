package com.ejemplo.PruebaAPISpringBoot.service.client;

import com.ejemplo.PruebaAPISpringBoot.dto.ProductDto;
import org.springframework.stereotype.Component;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ProductBlockingAdapter {

    private final ProductApiClient client;

    public ProductBlockingAdapter(ProductApiClient client) {
        this.client = client;
    }

    public Optional<ProductDto> fetchByIdBlocking(Integer id) {
        try {
            return client.fetchById(id)
                    .subscribeOn(Schedulers.boundedElastic())
                    .blockOptional();
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    public Map<Integer, ProductDto> fetchByIdsBlocking(List<Integer> ids) {
        return ids.stream()
                .distinct()
                .map(id -> Map.entry(id, fetchByIdBlocking(id).orElse(null)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}