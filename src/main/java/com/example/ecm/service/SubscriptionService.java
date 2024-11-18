package com.example.ecm.service;

import com.example.ecm.dto.responses.CreateSubscriptionResponse;
import com.example.ecm.enums.ExceptionMessage;
import com.example.ecm.enums.SubscriptionStatus;
import com.example.ecm.exception.NotFoundException;
import com.example.ecm.kafka.event.SubscriptionStatusUpdateEvent;
import com.example.ecm.kafka.service.SubscriptionEventProducer;
import com.example.ecm.mapper.SubscriptionMapper;
import com.example.ecm.model.Subscription;
import com.example.ecm.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final SubscriptionEventProducer subscriptionEventProducer;

    public CreateSubscriptionResponse getSubscriptionById(Long id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.ENTITY_NOT_FOUND.generateNotFoundEntityMessage("Subscription", id)));

        return subscriptionMapper.toCreateSubscriptionResponse(subscription);
    }

    public List<CreateSubscriptionResponse> getAllSubscriptions() {
        return subscriptionRepository.findAll().stream()
                .map(subscriptionMapper::toCreateSubscriptionResponse)
                .toList();
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void checkSubscriptions() {
        subscriptionRepository.findAllByStatusAndEndDate(SubscriptionStatus.ACTIVE, LocalDate.now())
                .ifPresent(subscriptions -> subscriptions.forEach(subscription -> {
                    subscriptionEventProducer.sendStatusUpdateEvent(new SubscriptionStatusUpdateEvent(subscription.getId(), SubscriptionStatus.PAUSED));
                }));
        subscriptionRepository.findAllByStatus(SubscriptionStatus.PAUSED)
                .ifPresent(subscriptions -> subscriptions.forEach(subscription -> {
                    subscriptionEventProducer.sendStatusUpdateEvent(new SubscriptionStatusUpdateEvent(subscription.getId(), SubscriptionStatus.INACTIVE));
                }));
    }

    public void pauseSubscription(Long id) {
        subscriptionEventProducer.sendStatusUpdateEvent(new SubscriptionStatusUpdateEvent(id, SubscriptionStatus.PAUSED));
    }

    public void resumeSubscription(Long id) {
        subscriptionEventProducer.sendStatusUpdateEvent(new SubscriptionStatusUpdateEvent(id, SubscriptionStatus.ACTIVE));
    }
}
