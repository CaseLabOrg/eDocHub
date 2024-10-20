package com.example.ecm.kafka.service;

import com.example.ecm.kafka.event.DocumentSignedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EventProducerService {
    private final KafkaTemplate<String, DocumentSignedEvent> kafkaTemplate;

    public void sendDocumentSignedEvent(DocumentSignedEvent event) {
        String documentSignedEventTopic = "document_signed_events";
        kafkaTemplate.send(documentSignedEventTopic, event);
    }
}
