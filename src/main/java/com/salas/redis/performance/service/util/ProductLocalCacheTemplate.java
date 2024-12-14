package com.salas.redis.performance.service.util;

import com.salas.redis.performance.entity.Product;
import com.salas.redis.performance.repos.ProductRepo;
import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.RedissonClient;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ProductLocalCacheTemplate extends CacheTemplate<Integer, Product> {

    @Autowired
    private ProductRepo repo;

    private RLocalCachedMap<Integer, Product> map;

    public ProductLocalCacheTemplate(RedissonClient client) {
        LocalCachedMapOptions<Integer, Product> cacheOptions = LocalCachedMapOptions.<Integer, Product>defaults()
                .evictionPolicy(LocalCachedMapOptions.EvictionPolicy.LFU)
                .syncStrategy(LocalCachedMapOptions.SyncStrategy.UPDATE)
                .reconnectionStrategy(LocalCachedMapOptions.ReconnectionStrategy.CLEAR);
       map = client.getLocalCachedMap("product", new TypedJsonJacksonCodec(Integer.class, Product.class), cacheOptions);

    }

    @Override
    protected Mono<Product> getFromSource(Integer id) {
        return repo.findById(id);
    }

    @Override
    protected Mono<Product> getFromCache(Integer id) {
        return Mono.justOrEmpty(map.get(id));
    }


    @Override
    protected Mono<Product> updateSource(Integer id, Product product) {
        return this.repo.findById(id)
                .doOnNext(p -> product.setId(id))
                .flatMap(p -> repo.save(product));
    }


    @Override
    protected Mono<Product> updateCache(Integer id, Product product) {
        return Mono.create(sink -> {
            map.fastPutAsync(id, product)
                    .thenAccept(b -> sink.success(product))
                    .exceptionally(ex -> {
                        sink.error(ex);
                        return null;
                    });
        });
    }


    @Override
    protected Mono<Void> deleteFromSource(Integer id) {
        return repo.deleteById(id);
    }


    @Override
    protected Mono<Void> deleteFromCache(Integer id) {
        return Mono.create(sink -> {
            map.fastRemoveAsync(id)
                    .thenAccept(b -> sink.success())
                    .exceptionally(ex -> {
                        sink.error(ex);
                        return null;
                    });
        });
    }
}
