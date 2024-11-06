package com.example.ecm.controller;

import com.example.ecm.aop.Loggable;
import com.example.ecm.dto.patch_requests.PatchAttributeRequest;
import com.example.ecm.dto.requests.CreateAttributeRequest;
import com.example.ecm.dto.requests.CreateTenantRequest;
import com.example.ecm.dto.responses.CreateAttributeResponse;
import com.example.ecm.dto.responses.TenantResponse;
import com.example.ecm.repository.TenantRepository;
import com.example.ecm.service.AttributeService;
import com.example.ecm.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tenants")
@RequiredArgsConstructor
@Loggable
@Tag(name = "Tenant Controller", description = "Управление тенантами документов в системе ECM")
public class TenantController {
    private final TenantService tenantService;


    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    @Operation(summary = "Создание атрибута", description = "Создает новый атрибут документа")
    public ResponseEntity<TenantResponse> createTenant(@Valid @RequestBody CreateTenantRequest request) {
        return ResponseEntity.ok(tenantService.createTenant(request));
    }


    @GetMapping("/{id}")
    public ResponseEntity<TenantResponse> getAttributeById(Long id) {
        return ResponseEntity.ok(tenantService.getTenantById(id));
    }


    @GetMapping
    public ResponseEntity<List<TenantResponse>> getAllAttributes(){
        return ResponseEntity.ok(tenantService.getAllTenants());
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttribute(
            @PathVariable @Parameter(description = "Идентификатор тенанта") Long id) {
        tenantService.deleteTenant(id);
        return ResponseEntity.noContent().build();
    }


}
