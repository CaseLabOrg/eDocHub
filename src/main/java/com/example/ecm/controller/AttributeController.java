package com.example.ecm.controller;

import com.example.ecm.dto.*;
import com.example.ecm.service.AttributeService;
import lombok.RequiredArgsConstructor;
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
public class AttributeController {

    private final AttributeService attributeService;

    /**
     * POST-метод для создания нового атрибута документа.
     *
     * @param request Объект запроса с данными для создания нового атрибута документа.
     * @return Ответ с данными созданного атрибута документа.
     */
    @PostMapping
    public ResponseEntity<AttributeResponse> createAttribute(@RequestBody AttributeRequest request) {
        return ResponseEntity.ok(attributeService.createAttribute(request));
    }

    /**
     * GET-метод для получения атрибута документа по его ID.
     *
     * @param id Идентификатор атрибута документа.
     * @return Ответ с данными атрибута документа.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AttributeResponse> getAttributeById(@PathVariable Long id) {
        return ResponseEntity.ok(attributeService.getAttributeById(id));
    }

    /**
     * GET-метод для получения всех существующих атрибутов документов.
     *
     * @return Список всех атрибутов документов.
     */
    @GetMapping
    public List<AttributeResponse> getAllAttributes() {
        return attributeService.getAllAttributes();
    }

    /**
     * PUT-метод для обновления существующего атрибута документа по его ID.
     *
     * @param id      Идентификатор атрибута документа, который нужно обновить.
     * @param request Объект запроса с новыми данными для обновления атрибута документа.
     * @return Ответ с обновленными данными атрибута документа.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<AttributeResponse> updateAttribute(@PathVariable Long id, @RequestBody AttributeRequest request) {
        return ResponseEntity.ok(attributeService.updateAttribute(id, request));
    }

    /**
     * DELETE-метод для удаления атрибута документа по его ID.
     *
     * @param id Идентификатор атрибута документа, который нужно удалить.
     * @return Ответ без содержимого (No Content) после успешного удаления.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttribute(@PathVariable Long id) {
        attributeService.deleteAttribute(id);
        return ResponseEntity.noContent().build();
    }
}
