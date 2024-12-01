package com.example.ecm.mapper;

import com.example.ecm.dto.requests.CreateInvoiceRequest;
import com.example.ecm.dto.responses.CreateInvoiceResponse;
import com.example.ecm.model.Invoice;
import com.example.ecm.model.Subscription;
import com.example.ecm.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InvoiceMapper {
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionMapper subscriptionMapper;


    public Invoice toInvoice(CreateInvoiceRequest request) {
        Invoice invoice = new Invoice();
        Subscription subscription = subscriptionRepository.findById(request.getSubscriptionId())
                .orElseThrow(() -> new RuntimeException("Subscription with id: " + request.getSubscriptionId() + " not found"));
        invoice.setSubscription(subscription);
        invoice.setDescription(request.getDescription());
        invoice.setAmount(request.getAmount());
        return invoice;
    }

    public CreateInvoiceResponse toCreateInvoiceResponse(Invoice invoice) {
        CreateInvoiceResponse response = new CreateInvoiceResponse();
        response.setId(invoice.getId());
        response.setSubscription(subscriptionMapper.toCreateSubscriptionResponse(invoice.getSubscription()));
        response.setDescription(invoice.getDescription());
        response.setAmount(invoice.getAmount());
        response.setStatus(invoice.getStatus().name());
        response.setCreatedDate(invoice.getCreatedDate());
        return response;
    }
}
