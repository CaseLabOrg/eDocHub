package com.example.ecm.init;

import lombok.RequiredArgsConstructor;
import org.flywaydb.core.Flyway;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FlywayRepairRunner implements ApplicationRunner {

    private final Flyway flyway;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        flyway.repair();
        System.out.println("Flyway repair executed successfully.");
    }
}
