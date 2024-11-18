package com.example.ecm.kafka.service;

import com.example.ecm.kafka.event.InvoiceCreateEvent;
import com.example.ecm.kafka.event.InvoiceStatusUpdateEvent;
import com.example.ecm.kafka.event.TenantCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TenantEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendCreatedEvent(TenantCreatedEvent event) {
        kafkaTemplate.send("tenant-created-events", event);
    }
}
