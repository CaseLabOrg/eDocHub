package com.example.ecm.kafka.service;

import com.example.ecm.kafka.event.SubscriptionCreateEvent;
import com.example.ecm.kafka.event.TenantCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TenantEventListener {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "tenant-created-events", groupId = "default", containerFactory = "kafkaListenerContainerFactory")
    public void listenTenantCreatedEvent(TenantCreatedEvent event) {
        kafkaTemplate.send("subscription-create-events", new SubscriptionCreateEvent(event.getTenantId()));
    }
}
