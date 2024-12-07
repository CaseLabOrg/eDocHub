package com.example.ecm.kafka.service;

import com.example.ecm.enums.ExceptionMessage;
import com.example.ecm.enums.SubscriptionStatus;
import com.example.ecm.exception.NotFoundException;
import com.example.ecm.kafka.event.*;
import com.example.ecm.mapper.SubscriptionMapper;
import com.example.ecm.model.Subscription;
import com.example.ecm.model.Tenant;
import com.example.ecm.repository.SubscriptionRepository;
import com.example.ecm.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class SubscriptionEventListener {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final TenantRepository tenantRepository;

    @KafkaListener(topics = "subscription-status-update-events", groupId = "default", containerFactory = "kafkaListenerContainerFactory")
    public void listenStatusUpdateEvent(SubscriptionStatusUpdateEvent event) {
        Subscription subscription = subscriptionRepository.findById(event.getSubscriptionId())
                .orElseThrow(() -> new RuntimeException("Subscription not found"));
        subscription.setStatus(event.getStatus());
        subscriptionRepository.save(subscription);
        if(event.getStatus() == SubscriptionStatus.PAUSED)
            kafkaTemplate.send("subscription-paused-events", new SubscriptionPausedEvent(subscription.getId()));
        else if(event.getStatus() == SubscriptionStatus.ACTIVE) {
            kafkaTemplate.send("subscription-activated-events", new SubscriptionActivatedEvent(subscription.getId()));
        }
    }

    @KafkaListener(topics = "subscription-create-events", groupId = "default", containerFactory = "kafkaListenerContainerFactory")
    public void listenCreateEvent(SubscriptionCreateEvent event) {
        Tenant tenant = tenantRepository.findById(event.getTenantId())
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.ENTITY_NOT_FOUND.generateNotFoundEntityMessage("Tenant", event.getTenantId())));

        Subscription subscription = subscriptionMapper.toSubscription(1L, tenant);
        subscription.setStatus(SubscriptionStatus.INACTIVE);
        subscription.setTenant(tenant);

        subscription = subscriptionRepository.save(subscription);
        kafkaTemplate.send("invoice-create-events", new InvoiceCreateEvent(subscription.getId()));
    }

    @KafkaListener(topics = "subscription-paused-events", groupId = "default", containerFactory = "kafkaListenerContainerFactory")
    public void listenPausedEvent(SubscriptionPausedEvent event) {
        kafkaTemplate.send("invoice-create-events", new InvoiceCreateEvent(event.getSubscriptionId()));
    }

    @KafkaListener(topics = "subscription-activated-events", groupId = "default", containerFactory = "kafkaListenerContainerFactory")
    public void listenActivatedEvent(SubscriptionActivatedEvent event) {
        Subscription subscription = subscriptionRepository.findById(event.getSubscriptionId())
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
        subscription.setStartDate(LocalDate.now());
        subscription.setEndDate(LocalDate.now().plusMonths(1));
        subscriptionRepository.save(subscription);
    }
}
