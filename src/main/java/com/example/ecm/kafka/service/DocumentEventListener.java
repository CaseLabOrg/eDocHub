package com.example.ecm.kafka.service;

import com.example.ecm.kafka.event.DocumentSignedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocumentEventListener {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "document-signed-events", groupId = "default")
    public void documentSigned(DocumentSignedEvent event) {
        System.out.println("Document signed"); // test
    }
}
