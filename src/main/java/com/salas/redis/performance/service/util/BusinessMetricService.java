package com.salas.redis.performance.service.util;

import org.redisson.api.RScoredSortedSetReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.client.codec.IntegerCodec;
import org.redisson.client.protocol.ScoredEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BusinessMetricService {

    @Autowired
    private RedissonReactiveClient redissonReactiveClient;

    public Mono<Map<Integer, Double>> top3Product() {
        String format = DateTimeFormatter.ofPattern("YYYYMMdd").format(LocalDate.now());
        RScoredSortedSetReactive<Integer> scoredSortedSet = redissonReactiveClient.getScoredSortedSet("product:visit:" + format, IntegerCodec.INSTANCE);

        return scoredSortedSet.entryRangeReversed(0, 2)
                .map(list -> list.stream().collect(
                        Collectors.toMap(
                                ScoredEntry::getValue,
                                ScoredEntry::getScore,
                                (a, b) -> a, LinkedHashMap::new
                        )
                ));
    }
}
