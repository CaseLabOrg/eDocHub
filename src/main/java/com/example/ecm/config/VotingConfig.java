package com.example.ecm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
public class VotingConfig {

    @Bean("votingFinisherScheduler")
    public ScheduledExecutorService scheduledExecutorService() {
        return Executors.newScheduledThreadPool(10);
    }
}
