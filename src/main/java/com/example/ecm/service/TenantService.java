package com.example.ecm.service;


import com.example.ecm.dto.requests.CreateTenantRequest;
import com.example.ecm.dto.responses.TenantResponse;
import com.example.ecm.exception.NotFoundException;
import com.example.ecm.mapper.TenantMapper;
import com.example.ecm.model.Tenant;
import com.example.ecm.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TenantService {
    private final TenantRepository tenantRepository;
    private final TenantMapper tenantMapper;

    public TenantResponse createTenant(CreateTenantRequest createTenantRequest) {
        Tenant tenant = tenantMapper.toTenant(createTenantRequest);
        return tenantMapper.toTenantResponse(tenantRepository.save(tenant));
    }

    public List<TenantResponse> getAllTenants() {
        List<Tenant> tenants = tenantRepository.findAll();
        return tenants.stream().map(tenantMapper::toTenantResponse).toList();
    }

    public TenantResponse getTenantById(Long id) {
        Tenant tenant = tenantRepository.findById(id).orElseThrow( () -> new NotFoundException("Tenant not found"));
        return tenantMapper.toTenantResponse(tenant);
    }

    public void deleteTenant(Long id) {

        tenantRepository.deleteById(id);

    }
}
