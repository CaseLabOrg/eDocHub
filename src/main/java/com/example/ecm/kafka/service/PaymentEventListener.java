package com.example.ecm.kafka.service;

import com.example.ecm.enums.InvoiceStatus;
import com.example.ecm.enums.PaymentStatus;
import com.example.ecm.kafka.event.InvoiceStatusUpdateEvent;
import com.example.ecm.kafka.event.PaymentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentEventListener {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "payment-events", groupId = "default", containerFactory = "kafkaListenerContainerFactory")
    public void listenPaymentEvent(PaymentEvent paymentEvent) {
        if(paymentEvent.getStatus() == PaymentStatus.SUCCEEDED) {
            kafkaTemplate.send("invoice-status-update-events", new InvoiceStatusUpdateEvent(paymentEvent.getInvoiceId(), InvoiceStatus.PAYED));
        }
    }
}
