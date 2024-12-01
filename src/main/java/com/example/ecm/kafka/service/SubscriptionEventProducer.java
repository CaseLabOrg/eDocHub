package com.example.ecm.kafka.service;


import com.example.ecm.kafka.event.SubscriptionActivatedEvent;
import com.example.ecm.kafka.event.SubscriptionCreateEvent;
import com.example.ecm.kafka.event.SubscriptionPausedEvent;
import com.example.ecm.kafka.event.SubscriptionStatusUpdateEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubscriptionEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendStatusUpdateEvent(SubscriptionStatusUpdateEvent event) {
        kafkaTemplate.send("subscription-status-update-events", event);
    }

    public void sendCreateEvent(SubscriptionCreateEvent event) {
        kafkaTemplate.send("subscription-create-events", event);
    }

    public void sendPausedEvent(SubscriptionPausedEvent event) {
        kafkaTemplate.send("subscription-paused-events", event);
    }

    public void sendActivatedEvent(SubscriptionActivatedEvent event) {
        kafkaTemplate.send("subscription-activated-events", event);
    }
}
