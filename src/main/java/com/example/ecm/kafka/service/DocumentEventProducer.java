package com.example.ecm.kafka.service;

import com.example.ecm.kafka.event.DocumentSignedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocumentEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendDocumentSignedEvent(DocumentSignedEvent event) {
        String documentSignedEventTopic = "document-signed-events";
        kafkaTemplate.send(documentSignedEventTopic, event);
    }
}
