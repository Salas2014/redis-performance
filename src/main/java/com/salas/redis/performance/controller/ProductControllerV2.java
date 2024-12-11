package com.salas.redis.performance.controller;

import com.salas.redis.performance.entity.Product;
import com.salas.redis.performance.service.ProductServiceV2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/product/v2")
public class ProductControllerV2 {

    @Autowired
    private ProductServiceV2 service;

    @GetMapping("{id}")
    public Mono<Product> getById(@PathVariable int id) {
        return service.get(id);
    }

    @PutMapping("{id}")
    public Mono<Product> update(@PathVariable int id, @RequestBody Mono<Product> productMono) {
        return service.update(id, productMono);
    }

    @DeleteMapping("{id}")
    public Mono<Void> delete(@PathVariable int id) {
        return service.delete(id);
    }
}
