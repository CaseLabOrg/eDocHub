package com.example.ecm.kafka.service;

import com.example.ecm.kafka.event.InvoiceCreateEvent;
import com.example.ecm.kafka.event.InvoiceStatusUpdateEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InvoiceEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendStatusUpdateEvent(InvoiceStatusUpdateEvent event) {
        kafkaTemplate.send("invoice-status-update-events", event);
    }

    public void sendCreateEvent(InvoiceCreateEvent event) {
        kafkaTemplate.send("invoice-create-events", event);
    }
}
