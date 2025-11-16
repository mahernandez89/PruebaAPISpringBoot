package com.ejemplo.PruebaAPISpringBoot.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;

import java.util.Map;
import java.util.stream.Collectors;
@RestController
public class MappingController {
    private final RequestMappingHandlerMapping mappings;

    public MappingController(RequestMappingHandlerMapping mappings) {
        this.mappings = mappings;
    }

    @GetMapping("/__mappings")
    public Map<String, String> listMappings() {
        return mappings.getHandlerMethods().entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().getPatternsCondition().toString() + " " + e.getKey().getMethodsCondition(),
                        e -> e.getValue().toString()
                ));
    }
}
