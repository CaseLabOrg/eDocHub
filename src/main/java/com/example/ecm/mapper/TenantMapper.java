package com.example.ecm.mapper;

import com.example.ecm.dto.requests.CreateTenantRequest;
import com.example.ecm.dto.responses.TenantResponse;
import com.example.ecm.model.Tenant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TenantMapper {

    public Tenant toTenant(CreateTenantRequest request) {
        Tenant tenant = new Tenant();
        tenant.setName(request.getName());
        tenant.setCreatedAt(LocalDateTime.now());
        tenant.setIsAlive(true);
        return tenant;
    }

    public TenantResponse toTenantResponse(Tenant tenant) {
        TenantResponse tenantResponse = new TenantResponse();
        tenantResponse.setId(tenant.getId());
        tenantResponse.setCreatedAt(tenant.getCreatedAt());
        tenantResponse.setName(tenant.getName());
        tenantResponse.setAlive(tenant.getIsAlive());
        return tenantResponse;
    }
}
