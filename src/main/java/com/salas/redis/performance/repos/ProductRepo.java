package com.salas.redis.performance.repos;

import com.salas.redis.performance.entity.Product;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepo extends ReactiveCrudRepository<Product, Integer> {
}
