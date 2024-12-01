package com.example.ecm.service;

import com.example.ecm.dto.requests.CreateTenantRequest;
import com.example.ecm.dto.responses.TenantResponse;
import com.example.ecm.exception.NotFoundException;
import com.example.ecm.kafka.event.TenantCreatedEvent;
import com.example.ecm.kafka.service.TenantEventProducer;
import com.example.ecm.mapper.TenantMapper;
import com.example.ecm.model.Tenant;
import com.example.ecm.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

/**
 * Сервис для управления организациями (тенантами).
 *
 * <p>Этот класс предоставляет методы для:
 * <ul>
 *     <li>Создания новой организации.</li>
 *     <li>Получения списка всех организаций с фильтрацией по статусу активности.</li>
 *     <li>Получения информации об организации по её идентификатору.</li>
 *     <li>Деактивации (удаления) организации.</li>
 *     <li>Восстановления активности организации.</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantRepository tenantRepository;
    private final TenantMapper tenantMapper;
    private final TenantEventProducer tenantEventProducer;

    /**
     * Создаёт новую организацию на основе переданного запроса.
     *
     * @param createTenantRequest объект запроса с данными для создания организации.
     * @return объект ответа с данными созданной организации.
     */
    public TenantResponse createTenant(CreateTenantRequest createTenantRequest) {
        Tenant tenant = tenantMapper.toTenant(createTenantRequest);
        tenant = tenantRepository.save(tenant);
        tenantEventProducer.sendCreatedEvent(new TenantCreatedEvent(tenant.getId()));
        return tenantMapper.toTenantResponse(tenant);
    }

    /**
     * Получает список всех организаций, с возможностью фильтрации по статусу активности.
     *
     * @param isAlive если true — возвращаются только активные организации, иначе — все.
     * @return список организаций в формате ответа.
     */
    public List<TenantResponse> getAllTenants(boolean isAlive) {
        Stream<Tenant> tenantStream = tenantRepository.findAll().stream();

        if (isAlive) {
            tenantStream = tenantStream.filter(Tenant::getIsAlive);
        }

        return tenantStream
                .map(tenantMapper::toTenantResponse)
                .toList();
    }

    /**
     * Получает информацию об организации по её идентификатору.
     *
     * @param id идентификатор организации.
     * @return объект ответа с данными об организации.
     * @throws NotFoundException если организация с указанным идентификатором не найдена.
     */
    public TenantResponse getTenantById(Long id) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tenant not found"));
        return tenantMapper.toTenantResponse(tenant);
    }

    /**
     * Деактивирует (помечает как удалённую) организацию по её идентификатору.
     *
     * @param id идентификатор организации.
     * @throws NotFoundException если организация с указанным идентификатором не найдена.
     */
    public void deleteTenant(Long id) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tenant not found"));
        tenant.setIsAlive(false);
        tenantRepository.save(tenant);
    }

    /**
     * Восстанавливает активность организации по её идентификатору.
     *
     * @param id идентификатор организации.
     * @throws NotFoundException если организация с указанным идентификатором не найдена.
     */
    public void recoverTenant(Long id) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tenant not found"));
        tenant.setIsAlive(true);
        tenantRepository.save(tenant);
    }
}
