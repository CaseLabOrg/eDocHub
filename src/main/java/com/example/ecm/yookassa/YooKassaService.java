package com.example.ecm.yookassa;

import com.example.ecm.model.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class YooKassaService {
    private final RestTemplate restTemplate;
    private final HttpHeaders headers;

    private final String YOOKASSA_API_URL = "https://api.yookassa.ru/v3/payments";

    public Payment createPayment(Payment payment, String returnUrl) {
        PaymentRequest paymentRequest = toPaymentRequest(payment, returnUrl);

        headers.set("Idempotence-Key", payment.getIdempotenceKey());
        HttpEntity<PaymentRequest> request = new HttpEntity<>(paymentRequest, headers);

        ResponseEntity<PaymentResponse> response = restTemplate.exchange(YOOKASSA_API_URL, HttpMethod.POST, request, PaymentResponse.class);
        if (!response.getStatusCode().is2xxSuccessful())
            throw new RuntimeException("Failed to create payment " + payment.getInvoice().getId() + payment.getInvoice().getDescription());

        payment.setCreatedAt(response.getBody().getCreatedAt());
        payment.setPaymentId(response.getBody().getId());

        return payment;
    }

    public PaymentResponse getPayment(Payment payment) {
        headers.set("Idempotence-Key", payment.getIdempotenceKey());
        HttpEntity<PaymentRequest> request = new HttpEntity<>(headers);

        ResponseEntity<PaymentResponse> response = restTemplate.exchange(
                YOOKASSA_API_URL + '/' + payment.getPaymentId(),
                HttpMethod.GET,
                request,
                PaymentResponse.class);

        if (!response.getStatusCode().is2xxSuccessful())
            throw new RuntimeException("Failed to get payment " + payment.getInvoice().getId() + payment.getInvoice().getDescription());

        return response.getBody();
    }

    public void cancelPayment(Payment payment) {
        headers.set("Idempotence-Key", payment.getIdempotenceKey());
        HttpEntity<PaymentRequest> request = new HttpEntity<>(headers);
        ResponseEntity<PaymentResponse> response = restTemplate.exchange(
                YOOKASSA_API_URL + "/id=?" + payment.getPaymentId() + "/cancel",
                HttpMethod.POST,
                request,
                PaymentResponse.class);

        if (!response.getStatusCode().is2xxSuccessful())
            throw new RuntimeException("Failed to cancel payment " + payment.getInvoice().getId() + payment.getInvoice().getDescription());
    }

    private PaymentRequest toPaymentRequest(Payment payment, String returnUrl) {
        PaymentRequest paymentRequest = new PaymentRequest();
        PaymentRequest.Amount amount = new PaymentRequest.Amount(payment.getInvoice().getAmount().toString(), "RUB");
        paymentRequest.setAmount(amount);
        paymentRequest.setCapture(true);
        PaymentRequest.PaymentMethodData paymentMethodData = new PaymentRequest.PaymentMethodData(payment.getPaymentMethod().name().toLowerCase());
        paymentRequest.setPayment_method_data(paymentMethodData);
        PaymentRequest.Confirmation confirmation = new PaymentRequest.Confirmation("redirect", returnUrl);
        paymentRequest.setConfirmation(confirmation);
        paymentRequest.setDescription(payment.getInvoice().getDescription());
        return paymentRequest;
    }

}
