package com.example.ecm.service;

import com.example.ecm.dto.requests.CreatePaymentRequest;
import com.example.ecm.dto.responses.CreateInvoiceResponse;
import com.example.ecm.dto.responses.CreatePaymentResponse;
import com.example.ecm.enums.ExceptionMessage;
import com.example.ecm.enums.InvoiceStatus;
import com.example.ecm.enums.PaymentStatus;
import com.example.ecm.exception.ForbiddenException;
import com.example.ecm.exception.NotFoundException;
import com.example.ecm.kafka.event.PaymentEvent;
import com.example.ecm.kafka.service.PaymentEventProducer;
import com.example.ecm.mapper.PaymentMapper;
import com.example.ecm.model.Invoice;
import com.example.ecm.model.Payment;
import com.example.ecm.repository.PaymentRepository;
import com.example.ecm.saas.TenantContext;
import com.example.ecm.saas.annotation.TenantRestrictedForPayment;
import com.example.ecm.yookassa.PaymentResponse;
import com.example.ecm.yookassa.YooKassaService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final PaymentEventProducer paymentEventProducer;
    private final InvoiceService invoiceService;
    private final YooKassaService yooKassaService;

    public CreatePaymentResponse createPayment(CreatePaymentRequest request) {
        Payment payment = paymentMapper.toPayment(request);
        CreateInvoiceResponse invoice = invoiceService.getInvoiceById(request.getInvoiceId());
        if(invoice.getStatus().equals(InvoiceStatus.PAYED.name()))
            throw new NotFoundException("Invoice with id: " + request.getInvoiceId() + " already payed or not found");

        payment.setStatus(PaymentStatus.PENDING);
        payment.setIdempotenceKey(UUID.randomUUID().toString());
        payment = yooKassaService.createPayment(payment, "127.0.0.1:8080/swagger-ui/index.html");  // изменить url
        return paymentMapper.toCreatePaymentResponse(paymentRepository.save(payment));
    }

    @TenantRestrictedForPayment
    public CreatePaymentResponse getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.ENTITY_NOT_FOUND.generateNotFoundEntityMessage("Payment", id)));

        return paymentMapper.toCreatePaymentResponse(payment);
    }

    public List<CreatePaymentResponse> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(paymentMapper::toCreatePaymentResponse)
                .filter(payment -> Objects.equals(payment.getInvoice().getSubscription().getTenantId(), TenantContext.getCurrentTenantId()))
                .toList();
    }

    @Scheduled(fixedRate = 10000)
    public void checkPayments() {
        paymentRepository.findAllByStatus(PaymentStatus.PENDING)
                .ifPresent(payments -> payments.forEach(payment -> {
                    PaymentResponse response = yooKassaService.getPayment(payment);
                    if(!response.getStatus().equalsIgnoreCase(PaymentStatus.PENDING.name())) {
                        payment.setStatus(PaymentStatus.valueOf(response.getStatus().toUpperCase()));
                        paymentRepository.save(payment);
                        paymentEventProducer.sendPayment(new PaymentEvent(payment.getInvoice().getId(), payment.getStatus()));
                    }
                }));
    }
}
