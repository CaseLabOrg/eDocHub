package com.example.ecm.controller;

import com.example.ecm.aop.Loggable;
import com.example.ecm.dto.patch_requests.PatchDocumentTypeRequest;
import com.example.ecm.dto.requests.CreateDocumentTypeRequest;
import com.example.ecm.dto.responses.CreateDocumentTypeResponse;
import com.example.ecm.service.DocumentTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@Loggable
public class DocumentTypeController {

    // Экземпляр DocumentTypeService для выполнения бизнес-логики, связанной с типами документов.
    private final DocumentTypeService documentTypeService;


    /**
     * POST-метод для создания нового типа документа.
     *
     * @param request Объект запроса с данными для создания нового типа документа.
     * @return Ответ с данными созданного типа документа.
     */
    @PostMapping
    public ResponseEntity<CreateDocumentTypeResponse> createDocumentType(@Valid @RequestBody CreateDocumentTypeRequest request) {
        return ResponseEntity.ok(documentTypeService.createDocumentType(request));
    }

    /**
     * GET-метод для получения типа документа по его ID.
     *
     * @param id Идентификатор типа документа.
     * @return Ответ с данными типа документа.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CreateDocumentTypeResponse> getDocumentTypeById(@PathVariable Long id,
                                                                          @RequestParam(defaultValue = "true") Boolean showOnlyAlive) {
        return ResponseEntity.ok(documentTypeService.getDocumentTypeById(id, showOnlyAlive));
    }

    /**
     * GET-метод для получения всех существующих типов документов.
     *
     * @return Список всех типов документов.
     */
    @GetMapping
    public List<CreateDocumentTypeResponse> getAllDocumentTypes(@RequestParam(defaultValue = "true") Boolean showOnlyAlive) {
        return documentTypeService.getAllDocumentTypes(showOnlyAlive);
    }

    /**
     * PUT-метод для обновления существующего типа документа по его ID.
     *
     * @param id      Идентификатор типа документа, который нужно обновить.
     * @param request Объект запроса с новыми данными для обновления типа документа.
     * @return Ответ с обновленными данными типа документа.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CreateDocumentTypeResponse> updateDocumentType(@PathVariable Long id, @Valid @RequestBody CreateDocumentTypeRequest request) {
        return ResponseEntity.ok(documentTypeService.updateDocumentType(id, request));
    }

    /**
     * DELETE-метод для удаления типа документа по его ID.
     *
     * @param id Идентификатор типа документа, который нужно удалить.
     * @return Ответ без содержимого (No Content) после успешного удаления.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocumentTypeById(@PathVariable Long id) {
        documentTypeService.deleteDocumentType(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/{id}/recover")
    public ResponseEntity<Void> recoverAttribute(@PathVariable Long id) {
        documentTypeService.recoverDocumentType(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Обновляет указанный тип документа с предоставленными изменениями.
     *
     * <p>Этот метод обрабатывает PATCH-запросы для обновления типа документа.
     * Принимает ID типа документа и тело запроса с полями для обновления,
     * и возвращает обновленный тип документа.</p>
     *
     * @param id      идентификатор типа документа, который требуется обновить
     * @param request объект {@link PatchDocumentTypeRequest}, содержащий данные для обновления типа документа
     * @return объект {@link ResponseEntity}, содержащий обновленный тип документа в ответе
     */

    @PatchMapping("/{id}")
    public ResponseEntity<CreateDocumentTypeResponse> patchDocumentType(@PathVariable Long id, @Valid @RequestBody PatchDocumentTypeRequest request) {
        return ResponseEntity.ok(documentTypeService.patchDocumentType(id, request));
    }
}
