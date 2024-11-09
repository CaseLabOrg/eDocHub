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
 * Предоставляет конечные точки для создания, получения, обновления и удаления атрибутов документов.
 */
@RestController
@RequestMapping("/attributes")
@RequiredArgsConstructor
@Loggable
@Tag(name = "Attribute Controller", description = "Управление атрибутами документов в системе ECM")
public class AttributeController {

    private final AttributeService attributeService;

    /**
     * Создает новый атрибут документа.
     *
     * @param request Запрос с данными для создания атрибута документа.
     * @return Ответ с данными созданного атрибута документа.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    @Operation(summary = "Создание атрибута", description = "Создает новый атрибут документа")
    public ResponseEntity<CreateAttributeResponse> createAttribute(
            @Valid @RequestBody CreateAttributeRequest request) {
        return ResponseEntity.ok(attributeService.createAttribute(request));
    }

    /**
     * Получает атрибут документа по его ID.
     *
     * @param id            Идентификатор атрибута документа.
     * @param showOnlyAlive Флаг, показывающий, отображать ли только активные атрибуты (по умолчанию true).
     * @return Ответ с данными атрибута документа.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Получение атрибута", description = "Возвращает атрибут документа по его ID")
    public ResponseEntity<CreateAttributeResponse> getAttributeById(
            @PathVariable @Parameter(description = "Идентификатор атрибута") Long id,
            @RequestParam(defaultValue = "true") @Parameter(description = "Отображать только активные атрибуты") Boolean showOnlyAlive) {
        return ResponseEntity.ok(attributeService.getAttributeById(id, showOnlyAlive));
    }

    /**
     * Получает все существующие атрибуты документов с поддержкой пагинации.
     *
     * @param page          Номер страницы (по умолчанию 0).
     * @param size          Количество записей на странице (по умолчанию 10).
     * @param showOnlyAlive Флаг, показывающий, отображать ли только активные атрибуты (по умолчанию true).
     * @return Список атрибутов документов в виде объектов {@link CreateAttributeResponse}.
     */
    @GetMapping
    @Operation(summary = "Получение всех атрибутов", description = "Возвращает страницу атрибутов документов с поддержкой пагинации")
    public ResponseEntity<List<CreateAttributeResponse>> getAllAttributes(
            @RequestParam(defaultValue = "0") @Parameter(description = "Номер страницы") int page,
            @RequestParam(defaultValue = "10") @Parameter(description = "Количество записей на странице") int size,
            @RequestParam(defaultValue = "true") @Parameter(description = "Отображать только активные атрибуты") Boolean showOnlyAlive,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(attributeService.getAllAttributes(pageable, showOnlyAlive, userPrincipal));
    }

    /**
     * Обновляет существующий атрибут документа по его ID.
     *
     * @param id      Идентификатор атрибута документа.
     * @param request Запрос с новыми данными для обновления атрибута документа.
     * @return Ответ с обновленными данными атрибута документа.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Обновление атрибута", description = "Обновляет атрибут документа по его ID")
    public ResponseEntity<CreateAttributeResponse> updateAttribute(
            @PathVariable @Parameter(description = "Идентификатор атрибута") Long id,
            @Valid @RequestBody CreateAttributeRequest request) {
        return ResponseEntity.ok(attributeService.updateAttribute(id, request));
    }

    /**
     * Удаляет атрибут документа по его ID.
     *
     * @param id Идентификатор атрибута документа.
     * @return Ответ без содержимого после успешного удаления.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление атрибута", description = "Удаляет атрибут документа по его ID")
    public ResponseEntity<Void> deleteAttribute(
            @PathVariable @Parameter(description = "Идентификатор атрибута") Long id) {
        attributeService.deleteAttribute(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Восстанавливает атрибут документа по его ID.
     *
     * @param id Идентификатор атрибута документа.
     * @return Ответ без содержимого после успешного восстановления.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/{id}/recover")
    @Operation(summary = "Восстановление атрибута", description = "Восстанавливает атрибут документа по его ID")
    public ResponseEntity<Void> recoverAttribute(
            @PathVariable @Parameter(description = "Идентификатор атрибута") Long id) {
        attributeService.recoverAttribute(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Обновляет указанный атрибут документа частично.
     *
     * @param id      Идентификатор атрибута документа.
     * @param request Запрос с данными для частичного обновления атрибута документа.
     * @return Ответ с обновленными данными атрибута документа.
     */
    @PatchMapping("/{id}")
    @Operation(summary = "Частичное обновление атрибута", description = "Обновляет атрибут документа частично по его ID")
    public ResponseEntity<CreateAttributeResponse> patchAttribute(
            @PathVariable @Parameter(description = "Идентификатор атрибута") Long id,
            @RequestBody PatchAttributeRequest request) {
        CreateAttributeResponse response = attributeService.patchAttribute(id, request);
        return ResponseEntity.ok(response);
    }
}
