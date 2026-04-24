package com.example.product.actuator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class ProductInfoContributor implements InfoContributor {

    private final LocalDateTime startupTime = LocalDateTime.now();

    @Override
    public void contribute(Info.Builder builder) {
        log.debug("Contributing custom info to actuator /info endpoint");

        Map<String, Object> productServiceInfo = new HashMap<>();
        productServiceInfo.put("startupTime", startupTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        productServiceInfo.put("status", "UP");
        productServiceInfo.put("features", new String[]{"CRUD Operations", "Redis Caching", "MySQL Persistence", "RxJS Frontend"});

        builder.withDetail("productService", productServiceInfo);
        log.info("Actuator info contribution completed");
    }
}

