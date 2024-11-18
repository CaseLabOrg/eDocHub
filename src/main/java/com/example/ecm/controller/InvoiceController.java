package com.example.ecm.controller;

import com.example.ecm.dto.responses.CreateInvoiceResponse;
import com.example.ecm.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/invoices")
public class InvoiceController {
    private final InvoiceService invoiceService;

    @Operation(summary = "Получить счёт по ID", description = "Возвращает данные счёта по указанному ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Счёт найден"),
            @ApiResponse(responseCode = "404", description = "Счёт не найден")
    })
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<CreateInvoiceResponse> getInvoiceById(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(id));
    }

    @Operation(summary = "Получить все счета", description = "Возвращает список всех счетов")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Успешное получение списка счетов"),
        @ApiResponse(responseCode = "401", description = "У вас нет прав")
    })
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<List<CreateInvoiceResponse>> getAllInvoices() {
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }
}
