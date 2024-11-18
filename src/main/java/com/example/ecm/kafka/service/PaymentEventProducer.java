package com.example.ecm.kafka.service;

import com.example.ecm.kafka.event.PaymentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendPayment(PaymentEvent event) {
        kafkaTemplate.send("payment-events", event);
    }
}
