package com.example.product.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Slf4j
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI productServiceOpenAPI() {
        log.info("Configuring OpenAPI/Swagger documentation on port {}", serverPort);

        Server localServer = new Server();
        localServer.setUrl("http://localhost:" + serverPort);
        localServer.setDescription("Local Development Server");

        return new OpenAPI()
                .info(new Info()
                        .title("Product Service API")
                        .description("RESTful API for Product Management with Redis Caching")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Product Team")
                                .email("support@example.com")
                                .url("https://github.com/huyquocpham/sbRedisCachable"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(localServer));
    }
}

