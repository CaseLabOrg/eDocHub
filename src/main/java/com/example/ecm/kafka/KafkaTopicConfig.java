package com.example.ecm.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic createUserNotificationsTopic() {
        return new NewTopic("document_signed_events", 1, (short) 1);
    }
}
