package com.example.ecm.controller;

import com.example.ecm.dto.requests.CreatePaymentRequest;
import com.example.ecm.dto.responses.CreatePaymentResponse;
import com.example.ecm.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService paymentService;

    @Operation(summary = "Создать новый платеж", description = "Создает новый платеж на основе предоставленных данных")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешное создание платежа"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            @ApiResponse(responseCode = "401", description = "У вас нет прав")
    })
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<CreatePaymentResponse> createPayment(@Valid @RequestBody CreatePaymentRequest request) {
        return ResponseEntity.ok(paymentService.createPayment(request));
    }

    @Operation(summary = "Получить платеж по ID", description = "Возвращает данные платежа по указанному ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Платеж найден"),
            @ApiResponse(responseCode = "401", description = "У вас нет прав"),
            @ApiResponse(responseCode = "404", description = "Платеж не найден")
    })
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<CreatePaymentResponse> getPaymentById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    @Operation(summary = "Получить все платежи", description = "Возвращает список всех платежей")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Успешное получение списка платежей"),
        @ApiResponse(responseCode = "401", description = "У вас нет прав")
    })
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<List<CreatePaymentResponse>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }
}
