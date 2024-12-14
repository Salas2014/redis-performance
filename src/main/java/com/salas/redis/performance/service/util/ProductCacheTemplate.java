package com.salas.redis.performance.service.util;

import com.salas.redis.performance.entity.Product;
import com.salas.redis.performance.repos.ProductRepo;
import org.redisson.api.RMapReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.crypto.Mac;

//@Service
public class ProductCacheTemplate extends CacheTemplate<Integer, Product> {

    @Autowired
    private ProductRepo repo;
    private RMapReactive<Integer, Product> map;

    public ProductCacheTemplate(RedissonReactiveClient client) {
        map = client.getMap("product", new TypedJsonJacksonCodec(Integer.class, Product.class));
    }

    @Override
    protected Mono<Product> getFromSource(Integer id) {
        return repo.findById(id);
    }

    @Override
    protected Mono<Product> getFromCache(Integer id) {
        return map.get(id);
    }


    @Override
    protected Mono<Product> updateSource(Integer id, Product product) {
        return this.repo.findById(id)
                .doOnNext(p -> product.setId(id))
                .flatMap(p -> repo.save(product));
    }


    @Override
    protected Mono<Product> updateCache(Integer id, Product product) {
        return map.fastPut(id, product).thenReturn(product);
    }


    @Override
    protected Mono<Void> deleteFromSource(Integer id) {
        return repo.deleteById(id);
    }


    @Override
    protected Mono<Void> deleteFromCache(Integer id) {
        return map.fastRemove(id).then();
    }
}

