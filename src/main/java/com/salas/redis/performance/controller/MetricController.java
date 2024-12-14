package com.salas.redis.performance.controller;

import com.salas.redis.performance.service.util.BusinessMetricService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("product/metric")
public class MetricController {

    @Autowired
    private BusinessMetricService metricService;

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Map<Integer, Double>> getTop3() {
        return metricService.top3Product()
                .repeatWhen(l -> Flux.interval(Duration.ofSeconds(3)));
    }
}
