package com.example.ecm.service;

import com.example.ecm.dto.responses.CreateInvoiceResponse;
import com.example.ecm.enums.ExceptionMessage;
import com.example.ecm.exception.NotFoundException;
import com.example.ecm.mapper.InvoiceMapper;
import com.example.ecm.model.Invoice;
import com.example.ecm.repository.InvoiceRepository;
import com.example.ecm.saas.TenantContext;
import com.example.ecm.saas.annotation.TenantRestrictedForInvoice;
import com.example.ecm.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;

    @TenantRestrictedForInvoice
    public CreateInvoiceResponse getInvoiceById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.ENTITY_NOT_FOUND.generateNotFoundEntityMessage("Invoice", id)));
        return invoiceMapper.toCreateInvoiceResponse(invoice);
    }

    public List<CreateInvoiceResponse> getAllInvoices() {
        return invoiceRepository.findAll().stream()
                .map(invoiceMapper::toCreateInvoiceResponse)
                .filter(invoice -> invoice.getSubscription().getTenantId().equals(TenantContext.getCurrentTenantId()))
                .toList();
    }
}