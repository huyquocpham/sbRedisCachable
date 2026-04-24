package com.example.product;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@Slf4j
@SpringBootApplication
@EnableCaching
public class ProductServiceApplication {
    public static void main(String[] args) {
        log.info("Starting Product Service Application...");
        SpringApplication.run(ProductServiceApplication.class, args);
        log.info("Product Service Application started successfully");
    }
}

