package com.example.ecm.kafka.service;


import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class EventConsumerService {

    @KafkaListener(topics = "document_signed_events", groupId = "default")
    public void consumeMessage(String message) {
        System.out.println(message); // test
    }
}
