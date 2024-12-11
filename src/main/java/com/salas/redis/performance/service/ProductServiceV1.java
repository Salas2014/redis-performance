package com.salas.redis.performance.service;

import com.salas.redis.performance.entity.Product;
import com.salas.redis.performance.repos.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ProductServiceV1 {

    @Autowired
    ProductRepo productRepo;

    public Mono<Product> getById(int id) {
        return productRepo.findById(id);
    }

    public Mono<Product> updateProduct(int id, Mono<Product> productMono) {
        return productRepo.findById(id)
                .flatMap(product -> productMono.doOnNext(pr -> pr.setId(id)))
                .flatMap(productRepo::save);
    }
}
