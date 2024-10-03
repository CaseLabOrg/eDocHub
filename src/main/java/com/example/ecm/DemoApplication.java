package com.example.ecm;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@ApiResponse(responseCode = "200", description = "Hello World")
@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
