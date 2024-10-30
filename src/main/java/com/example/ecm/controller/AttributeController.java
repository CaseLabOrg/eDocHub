package com.example.ecm.controller;

import com.example.ecm.aop.Loggable;
import com.example.ecm.dto.patch_requests.PatchAttributeRequest;
import com.example.ecm.dto.requests.CreateAttributeRequest;
import com.example.ecm.dto.responses.CreateAttributeResponse;
import com.example.ecm.service.AttributeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для управления атрибутами документов в системе ECM.
 * Предоставляет конечные точки для создания, получения, обновления и удаления автрибутов документов.
 */
@RestController
@RequestMapping("/attributes")
@RequiredArgsConstructor
@Loggable
public class AttributeController {

    private final AttributeService attributeService;

    /**
     * POST-метод для создания нового атрибута документа.
     *
     * @param request Объект запроса с данными для создания нового атрибута документа.
     * @return Ответ с данными созданного атрибута документа.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<CreateAttributeResponse> createAttribute(@Valid @RequestBody CreateAttributeRequest request) {
        return ResponseEntity.ok(attributeService.createAttribute(request));
    }

    /**
     * GET-метод для получения атрибута документа по его ID.
     *
     * @param id Идентификатор атрибута документа.
     * @return Ответ с данными атрибута документа.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CreateAttributeResponse> getAttributeById(@PathVariable Long id, @RequestParam(defaultValue = "true") Boolean showOnlyAlive) {
        return ResponseEntity.ok(attributeService.getAttributeById(id, showOnlyAlive));
    }

    /**
     * GET-метод для получения всех существующих атрибутов документов с поддержкой пагинации.
     * Возвращает страницу, содержащую список атрибутов документов, с указанием количества записей на странице и номера страницы.
     *
     * @param page номер страницы (по умолчанию 0), указывает, какую страницу данных необходимо вернуть.
     * @param size количество записей на странице (по умолчанию 10), указывает, сколько записей должно быть на одной странице.
     * @return страница с атрибутами документов в виде объектов {@link CreateAttributeResponse}.
     */
    @GetMapping
    public ResponseEntity<List<CreateAttributeResponse>> getAllAttributes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "true") Boolean showOnlyAlive) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(attributeService.getAllAttributes(pageable, showOnlyAlive));
    }

    /**
     * PUT-метод для обновления существующего атрибута документа по его ID.
     *
     * @param id      Идентификатор атрибута документа, который нужно обновить.
     * @param request Объект запроса с новыми данными для обновления атрибута документа.
     * @return Ответ с обновленными данными атрибута документа.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CreateAttributeResponse> updateAttribute(@PathVariable Long id, @Valid @RequestBody CreateAttributeRequest request) {
        return ResponseEntity.ok(attributeService.updateAttribute(id, request));
    }

    /**
     * DELETE-метод для удаления атрибута документа по его ID.
     *
     * @param id Идентификатор атрибута документа, который нужно удалить.
     * @return Ответ без содержимого (No Content) после успешного удаления.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttribute(@PathVariable Long id) {
        attributeService.deleteAttribute(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/{id}/recover")
    public ResponseEntity<Void> recoverAttribute(@PathVariable Long id) {
        attributeService.recoverAttribute(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Обрабатывает частичное обновление атрибута документа.
     * <p>
     * Метод принимает идентификатор атрибута и данные для частичного обновления,
     * переданные в запросе. Только те поля, которые указаны в запросе, будут обновлены.
     * Остальные поля атрибута останутся без изменений.
     *
     * @param id      идентификатор атрибута, который необходимо обновить
     * @param request объект {@link PatchAttributeRequest}, содержащий поля для частичного обновления
     * @return {@link ResponseEntity} с данными обновлённого атрибута в формате {@link CreateAttributeResponse}
     */
    @PatchMapping("/{id}")
    public ResponseEntity<CreateAttributeResponse> patchAttribute(@PathVariable Long id, @RequestBody PatchAttributeRequest request) {
        CreateAttributeResponse response = attributeService.patchAttribute(id, request);
        return ResponseEntity.ok(response);
    }
}
