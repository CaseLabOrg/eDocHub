package com.example.ecm.controller;

import com.example.ecm.dto.CreateDocumentTypeRequest;
import com.example.ecm.dto.CreateDocumentTypeResponse;
import com.example.ecm.service.DocumentTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для управления типами документов в системе ECM.
 * Предоставляет конечные точки для создания, получения, обновления и удаления типов документов.
 */
@RestController
@RequestMapping("/document-types")
public class DocumentTypeController {

    // Экземпляр DocumentTypeService для выполнения бизнес-логики, связанной с типами документов.
    private final DocumentTypeService documentTypeService;

    /**
     * Конструктор для внедрения зависимости сервиса типов документов.
     *
     * @param documentTypeService Сервис для работы с типами документов.
     */
    public DocumentTypeController(DocumentTypeService documentTypeService) {
        this.documentTypeService = documentTypeService;
    }

    /**
     * POST-метод для создания нового типа документа.
     *
     * @param request Объект запроса с данными для создания нового типа документа.
     * @return Ответ с данными созданного типа документа.
     */
    @PostMapping
    public ResponseEntity<CreateDocumentTypeResponse> createDocumentType(@RequestBody CreateDocumentTypeRequest request) {
        return ResponseEntity.ok(documentTypeService.createDocumentType(request));
    }

    /**
     * GET-метод для получения типа документа по его ID.
     *
     * @param id Идентификатор типа документа.
     * @return Ответ с данными типа документа.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CreateDocumentTypeResponse> getDocumentTypeById(@PathVariable Long id) {
        return ResponseEntity.ok(documentTypeService.getDocumentTypeById(id));
    }

    /**
     * GET-метод для получения всех существующих типов документов.
     *
     * @return Список всех типов документов.
     */
    @GetMapping
    public List<CreateDocumentTypeResponse> getAllDocumentTypes() {
        return documentTypeService.getAllDocumentTypes();
    }

    /**
     * PUT-метод для обновления существующего типа документа по его ID.
     *
     * @param id      Идентификатор типа документа, который нужно обновить.
     * @param request Объект запроса с новыми данными для обновления типа документа.
     * @return Ответ с обновленными данными типа документа.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CreateDocumentTypeResponse> updateDocumentType(@PathVariable Long id, @RequestBody CreateDocumentTypeRequest request) {
        return ResponseEntity.ok(documentTypeService.updateDocumentType(id, request));
    }

    /**
     * DELETE-метод для удаления типа документа по его ID.
     *
     * @param id Идентификатор типа документа, который нужно удалить.
     * @return Ответ без содержимого (No Content) после успешного удаления.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocumentTypeById(@PathVariable Long id) {
        documentTypeService.deleteDocumentType(id);
        return ResponseEntity.noContent().build();
    }
}
