package com.example.ecm.controller;

import com.example.ecm.aop.Loggable;
import com.example.ecm.dto.patch_requests.PatchDocumentVersionRequest;
import com.example.ecm.dto.requests.AddCommentRequest;
import com.example.ecm.dto.requests.CreateDocumentRequest;
import com.example.ecm.dto.requests.CreateDocumentVersionRequest;
import com.example.ecm.dto.responses.AddCommentResponse;
import com.example.ecm.dto.responses.CreateDocumentResponse;
import com.example.ecm.dto.responses.CreateDocumentVersionResponse;
import com.example.ecm.security.UserPrincipal;
import com.example.ecm.service.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер, отвечающий за обработку API-запросов, связанных с документами в системе ECM.
 * Предоставляет конечные точки для создания, получения, обновления и удаления документов.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/documents")
@Loggable
public class DocumentController {

    // Экземпляр DocumentService для обработки бизнес-логики, связанной с документами.
    private final DocumentService documentService;

    /**
     * POST-метод для создания нового документа.
     *
     * @param createDocumentRequest Запрос, содержащий данные для создания документа.
     * @return Ответ с информацией о созданном документе.
     */
    @PostMapping
    public ResponseEntity<CreateDocumentResponse> createFile(@Valid @RequestBody CreateDocumentRequest createDocumentRequest) {
        return ResponseEntity.ok(documentService.createDocument(createDocumentRequest));
    }

    /**
     * GET-метод для получения документа по его ID.
     *
     * @param id Идентификатор документа.
     * @return Ответ с данными документа.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CreateDocumentResponse> getDocument(@PathVariable Long id,
                                                              @RequestParam(defaultValue = "true") Boolean showOnlyAlive,
                                                              @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(documentService.getDocumentById(id, showOnlyAlive, userPrincipal));
    }

    @GetMapping("/{documentId}/{versionId}")
    public ResponseEntity<CreateDocumentVersionResponse> getDocumentVersion(@PathVariable Long documentId,
                                                                            @PathVariable Long versionId,
                                                                            @RequestParam(defaultValue = "true") Boolean showOnlyAlive) {
        return ResponseEntity.ok(documentService.getDocumentVersionById(documentId, versionId, showOnlyAlive));
    }

    /**
     * PUT-метод для обновления существующего документа по его ID.
     *
     * @param id       Идентификатор документа, который нужно обновить.
     * @param document Объект запроса, содержащий обновленные данные документа.
     * @return Ответ с обновленными данными документа.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CreateDocumentVersionResponse> updateDocument(@PathVariable Long id, @Valid @RequestBody CreateDocumentVersionRequest document) {
        return ResponseEntity.ok(documentService.updateDocumentVersion(id, document));
    }

    /**
     * DELETE-метод для удаления документа по его ID.
     *
     * @param id Идентификатор документа, который нужно удалить.
     * @return Ответ без содержимого (No Content) после успешного удаления.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/recover")
    public ResponseEntity<Void> recoverDocument(@PathVariable Long id) {
        documentService.recoverDocument(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET-ALL-метод для удаления документа по его ID.
     *
     * @return List<CreateDocumentTypeResponse>.
     */
    @GetMapping
    public ResponseEntity<List<CreateDocumentResponse>> getAllDocuments(
            @RequestParam(defaultValue = "true") Boolean showOnlyAlive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(documentService.getAllDocuments(page, size, showOnlyAlive, userPrincipal));
    }

    /**
     * Обновляет указанную версию документа с предоставленными изменениями.
     *
     * <p>Этот метод обрабатывает PATCH-запросы для обновления версии документа.
     * Принимает ID документа и тело запроса с полями для обновления,
     * и возвращает обновленную версию документа.</p>
     *
     * @param id      идентификатор документа, который требуется обновить
     * @param request объект {@link PatchDocumentVersionRequest}, содержащий данные для обновления версии документа
     * @return объект {@link ResponseEntity}, содержащий обновленную версию документа в ответе
     */

    @PatchMapping("/{id}")
    public ResponseEntity<CreateDocumentVersionResponse> patchDocumentType(@PathVariable Long id, @Valid @RequestBody PatchDocumentVersionRequest request) {
        return ResponseEntity.ok(documentService.patchDocument(id, request));
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<AddCommentResponse> addComment(@RequestParam Long id, @Valid @RequestBody AddCommentRequest createCommentRequest,
                                                         @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(documentService.addComment(id, createCommentRequest, userPrincipal));
    }
}
