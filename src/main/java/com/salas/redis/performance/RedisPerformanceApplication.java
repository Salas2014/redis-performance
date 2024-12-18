package com.salas.redis.performance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class RedisPerformanceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisPerformanceApplication.class, args);
    }

}
