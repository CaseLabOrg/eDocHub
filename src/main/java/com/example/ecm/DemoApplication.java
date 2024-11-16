package com.example.ecm;

import com.example.ecm.security.UserPrincipal;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@EnableScheduling
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.ecm.repository")
@EntityScan(basePackages = "com.example.ecm.model")
@ComponentScan(basePackages = "com.example.ecm")
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
