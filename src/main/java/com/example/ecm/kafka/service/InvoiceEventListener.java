package com.example.ecm.kafka.service;

import com.example.ecm.enums.InvoiceStatus;
import com.example.ecm.enums.SubscriptionStatus;
import com.example.ecm.kafka.event.InvoiceCreateEvent;
import com.example.ecm.kafka.event.InvoiceStatusUpdateEvent;
import com.example.ecm.kafka.event.SubscriptionStatusUpdateEvent;
import com.example.ecm.model.Invoice;
import com.example.ecm.model.Subscription;
import com.example.ecm.repository.InvoiceRepository;
import com.example.ecm.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class InvoiceEventListener {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final InvoiceRepository invoiceRepository;
    private final SubscriptionRepository subscriptionRepository;

    @KafkaListener(topics = "invoice-status-update-events", groupId = "default", containerFactory = "kafkaListenerContainerFactory")
    public void listenInvoiceStatusUpdateEvent(InvoiceStatusUpdateEvent event) {
        Invoice invoice = invoiceRepository.findById(event.getInvoiceId())
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
        invoice.setStatus(event.getStatus());
        invoiceRepository.save(invoice);
        if(event.getStatus() == InvoiceStatus.PAYED) {
            kafkaTemplate.send("subscription-status-update-events", new SubscriptionStatusUpdateEvent(invoice.getSubscription().getId(), SubscriptionStatus.ACTIVE));
        }
    }

    @KafkaListener(topics = "invoice-create-events", groupId = "default", containerFactory = "kafkaListenerContainerFactory")
    public void listenInvoiceCreateEvent(InvoiceCreateEvent event) {
        Subscription subscription = subscriptionRepository.findById(event.getSubscriptionId())
                .orElseThrow(() -> new RuntimeException("Subscription not found"));
        Invoice invoice = new Invoice();
        invoice.setSubscription(subscription);
        invoice.setDescription("Subscription: " + subscription.getPlan().getName() + " for 1 Month");
        invoice.setAmount(subscription.getPlan().getPrice());
        invoice.setStatus(InvoiceStatus.AWAITING_PAYMENT);
        invoice.setCreatedDate(LocalDate.now());
        invoiceRepository.save(invoice);
    }
}
