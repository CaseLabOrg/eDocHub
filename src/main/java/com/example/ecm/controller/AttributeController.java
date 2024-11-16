package com.example.ecm.controller;

import com.example.ecm.aop.Loggable;
import com.example.ecm.dto.patch_requests.PatchAttributeRequest;
import com.example.ecm.dto.requests.CreateAttributeRequest;
import com.example.ecm.dto.responses.CreateAttributeResponse;
import com.example.ecm.security.UserPrincipal;
import com.example.ecm.service.AttributeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для управления атрибутами документов в системе ECM.
 *
 * <p>Предоставляет API для:
 * <ul>
 *     <li>Создания нового атрибута документа.</li>
 *     <li>Получения информации об атрибуте по ID.</li>
 *     <li>Получения списка атрибутов с пагинацией.</li>
 *     <li>Обновления атрибута полностью или частично.</li>
 *     <li>Удаления и восстановления атрибута.</li>
 * </ul>
 */
@RestController
@RequestMapping("/attributes")
@RequiredArgsConstructor
@Loggable
@Tag(name = "Attribute Controller", description = "Управление атрибутами документов в системе ECM")
public class AttributeController {

    private final AttributeService attributeService;

    /**
     * Создаёт новый атрибут документа.
     *
     * @param request запрос с данными для создания атрибута.
     * @return данные созданного атрибута.
     */
    @PostMapping
    @Operation(summary = "Создание атрибута", description = "Создает новый атрибут документа.")
    public ResponseEntity<CreateAttributeResponse> createAttribute(
            @Valid @RequestBody CreateAttributeRequest request) {
        return ResponseEntity.ok(attributeService.createAttribute(request));
    }

    /**
     * Возвращает данные атрибута документа по его ID.
     *
     * @param id            идентификатор атрибута.
     * @param showOnlyAlive флаг отображения только активных атрибутов (по умолчанию true).
     * @return данные атрибута.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Получение атрибута", description = "Возвращает атрибут документа по его ID.")
    public ResponseEntity<CreateAttributeResponse> getAttributeById(
            @PathVariable @Parameter(description = "Идентификатор атрибута") Long id,
            @RequestParam(defaultValue = "true") @Parameter(description = "Отображать только активные атрибуты") Boolean showOnlyAlive) {
        return ResponseEntity.ok(attributeService.getAttributeById(id, showOnlyAlive));
    }

    /**
     * Возвращает страницу атрибутов документов.
     *
     * @param page          номер страницы (по умолчанию 0).
     * @param size          количество записей на странице (по умолчанию 10).
     * @param showOnlyAlive флаг отображения только активных атрибутов (по умолчанию true).
     * @return список атрибутов.
     */
    @GetMapping
    @Operation(summary = "Получение всех атрибутов", description = "Возвращает список атрибутов документов с поддержкой пагинации.")
    public ResponseEntity<List<CreateAttributeResponse>> getAllAttributes(
            @RequestParam(defaultValue = "0") @Parameter(description = "Номер страницы") int page,
            @RequestParam(defaultValue = "10") @Parameter(description = "Количество записей на странице") int size,
            @RequestParam(defaultValue = "true") @Parameter(description = "Отображать только активные атрибуты") Boolean showOnlyAlive) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(attributeService.getAllAttributes(pageable, showOnlyAlive));
    }

    /**
     * Обновляет атрибут документа по его ID.
     *
     * @param id      идентификатор атрибута.
     * @param request запрос с новыми данными для атрибута.
     * @return обновлённые данные атрибута.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Обновление атрибута", description = "Обновляет данные атрибута документа полностью по его ID.")
    public ResponseEntity<CreateAttributeResponse> updateAttribute(
            @PathVariable @Parameter(description = "Идентификатор атрибута") Long id,
            @Valid @RequestBody CreateAttributeRequest request) {
        return ResponseEntity.ok(attributeService.updateAttribute(id, request));
    }

    /**
     * Удаляет атрибут документа по его ID.
     *
     * @param id идентификатор атрибута.
     * @return пустой ответ после удаления.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление атрибута", description = "Удаляет атрибут документа по его ID.")
    public ResponseEntity<Void> deleteAttribute(
            @PathVariable @Parameter(description = "Идентификатор атрибута") Long id) {
        attributeService.deleteAttribute(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Восстанавливает атрибут документа по его ID.
     *
     * @param id идентификатор атрибута.
     * @return пустой ответ после восстановления.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/{id}/recover")
    @Operation(summary = "Восстановление атрибута", description = "Восстанавливает атрибут документа по его ID.")
    public ResponseEntity<Void> recoverAttribute(
            @PathVariable @Parameter(description = "Идентификатор атрибута") Long id) {
        attributeService.recoverAttribute(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Частично обновляет атрибут документа.
     *
     * @param id      идентификатор атрибута.
     * @param request запрос с данными для частичного обновления.
     * @return обновлённые данные атрибута.
     */
    @PatchMapping("/{id}")
    @Operation(summary = "Частичное обновление атрибута", description = "Частично обновляет данные атрибута документа по его ID.")
    public ResponseEntity<CreateAttributeResponse> patchAttribute(
            @PathVariable @Parameter(description = "Идентификатор атрибута") Long id,
            @RequestBody PatchAttributeRequest request) {
        CreateAttributeResponse response = attributeService.patchAttribute(id, request);
        return ResponseEntity.ok(response);
    }
}
