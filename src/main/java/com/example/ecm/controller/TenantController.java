package com.example.ecm.controller;

import com.example.ecm.aop.Loggable;
import com.example.ecm.dto.requests.CreateTenantRequest;
import com.example.ecm.dto.responses.TenantResponse;
import com.example.ecm.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST-контроллер для управления тенантами (организациями).
 *
 * <p>Этот контроллер предоставляет API для:
 * <ul>
 *     <li>Создания новой организации.</li>
 *     <li>Получения данных об организации по её идентификатору.</li>
 *     <li>Получения списка всех организаций (с фильтрацией по активности).</li>
 *     <li>Деактивации (удаления) организации.</li>
 *     <li>Восстановления активности организации.</li>
 * </ul>
 */
@RestController
@RequestMapping("/tenants")
@RequiredArgsConstructor
@Loggable
@Tag(name = "Tenant Controller", description = "Управление тенантами документов в системе ECM")
public class TenantController {

    private final TenantService tenantService;

    /**
     * Создаёт новую организацию.
     *
     * @param request данные для создания организации.
     * @return данные созданной организации.
     */
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN')")
    @PostMapping
    @Operation(summary = "Создание организации", description = "Создаёт новую организацию в системе.")
    public ResponseEntity<TenantResponse> createTenant(@Valid @RequestBody CreateTenantRequest request) {
        return ResponseEntity.ok(tenantService.createTenant(request));
    }

    /**
     * Возвращает данные об организации по её идентификатору.
     *
     * @param id идентификатор организации.
     * @return данные организации.
     */
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN')")
    @GetMapping("/{id}")
    @Operation(summary = "Получение организации по ID", description = "Возвращает информацию об организации по её ID.")
    public ResponseEntity<TenantResponse> getTenantById(@PathVariable Long id) {
        return ResponseEntity.ok(tenantService.getTenantById(id));
    }

    /**
     * Возвращает список всех организаций.
     *
     * @param isAlive если true — возвращаются только активные организации, иначе — все.
     * @return список организаций.
     */
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN')")
    @GetMapping
    @Operation(summary = "Получение списка организаций", description = "Возвращает список всех организаций, с возможностью фильтрации только активных.")
    public ResponseEntity<List<TenantResponse>> getAllTenants(@RequestParam(required = false, defaultValue = "true") boolean isAlive) {
        return ResponseEntity.ok(tenantService.getAllTenants(isAlive));
    }

    /**
     * Деактивирует (удаляет) организацию.
     *
     * @param id идентификатор организации.
     * @return пустой ответ.
     */
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление организации", description = "Помечает организацию как неактивную.")
    public ResponseEntity<Void> deleteTenant(@PathVariable Long id) {
        tenantService.deleteTenant(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Восстанавливает активность организации.
     *
     * @param id идентификатор организации.
     * @return данные восстановленной организации.
     */
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN')")
    @PatchMapping("/{id}/recover")
    @Operation(summary = "Восстановление организации", description = "Восстанавливает активность ранее деактивированной организации.")
    public ResponseEntity<TenantResponse> recoverTenant(@PathVariable Long id) {
        tenantService.recoverTenant(id);
        return ResponseEntity.ok(tenantService.getTenantById(id));
    }
}
