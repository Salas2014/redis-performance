package com.salas.redis.performance.service;


import jakarta.annotation.PostConstruct;
import org.redisson.api.BatchOptions;
import org.redisson.api.RBatchReactive;
import org.redisson.api.RScoredSortedSetReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.client.codec.IntegerCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProductVisitService {

    @Autowired
    private RedissonReactiveClient redissonReactiveClient;
    private Sinks.Many<Integer> sink;

    public ProductVisitService() {
        sink = Sinks.many().unicast().onBackpressureBuffer();
    }

    @PostConstruct
    private void init() {
        sink.asFlux()
                .buffer(Duration.ofSeconds(3))
                .map(l ->
                    l.stream().collect(
                            Collectors.groupingBy(Function.identity(), Collectors.counting())
                    ))
                .flatMap(this::updateBatch)
                .subscribe();
    }

     public void addVisit(int productId) {
        sink.tryEmitNext(productId);
     }


    private Mono<Void> updateBatch(Map<Integer, Long> map) {
        RBatchReactive batch = redissonReactiveClient.createBatch(BatchOptions.defaults());
        String format = DateTimeFormatter.ofPattern("YYYYMMdd").format(LocalDate.now());
        RScoredSortedSetReactive<Integer> scoredSortedSet = batch.getScoredSortedSet("product:visit:" + format, IntegerCodec.INSTANCE);

        return Flux.fromIterable(map.entrySet())
                .map(entry -> scoredSortedSet.addScore(entry.getKey(), entry.getValue()))
                .then(batch.execute())
                .then();
    }
}
