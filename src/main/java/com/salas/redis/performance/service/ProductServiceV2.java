package com.salas.redis.performance.service;

import com.salas.redis.performance.entity.Product;
import com.salas.redis.performance.service.util.CacheTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ProductServiceV2 {

    @Autowired
    private CacheTemplate<Integer, Product> cacheTemplate;
    @Autowired
    private ProductVisitService productVisitService;

    public Mono<Product> get(int id) {
        return cacheTemplate.get(id)
                .doFirst(() -> productVisitService.addVisit(id));
    }

    public Mono<Product> update(int id, Mono<Product> product) {
        return product.flatMap(p -> cacheTemplate.update(id, p));
    }

    public Mono<Void> delete(int id) {
        return cacheTemplate.delete(id);
    }
}
