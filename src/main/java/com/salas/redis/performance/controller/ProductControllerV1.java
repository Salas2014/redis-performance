package com.salas.redis.performance.controller;

import com.salas.redis.performance.entity.Product;
import com.salas.redis.performance.service.ProductServiceV1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/product/v1")
public class ProductControllerV1 {

    @Autowired
    private ProductServiceV1 productService;

    @GetMapping("{id}")
    public Mono<Product> get(@PathVariable int id) {
        return productService.getById(id);
    }

    @PutMapping("{id}")
    public Mono<Product> update(@PathVariable int id, @RequestBody Mono<Product> productMono) {
        return productService.updateProduct(id, productMono);
    }
}
