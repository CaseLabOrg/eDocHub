package com.example.ecm;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@ApiResponse(responseCode = "200", description = "Hello World")
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.ecm.repository")
@EntityScan(basePackages = "com.example.ecm.model")
@ComponentScan(basePackages = "com.example.ecm")
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
