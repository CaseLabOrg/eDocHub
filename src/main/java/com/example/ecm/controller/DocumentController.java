package com.example.ecm.controller;

import com.example.ecm.aop.Loggable;
import com.example.ecm.dto.requests.CreateDocumentRequest;
import com.example.ecm.dto.requests.CreateDocumentVersionRequest;
import com.example.ecm.dto.responses.CreateDocumentResponse;
import com.example.ecm.dto.responses.CreateDocumentVersionResponse;
import com.example.ecm.service.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    private ResponseEntity<CreateDocumentResponse> getDocument(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.getDocumentById(id));
    }

    /**
     * PUT-метод для обновления существующего документа по его ID.
     *
     * @param id Идентификатор документа, который нужно обновить.
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

    /**
     * GET-ALL-метод для удаления документа по его ID.
     *
     * @return List<CreateDocumentTypeResponse>.
     */

    @GetMapping
    public List<CreateDocumentResponse> getAllDocument() {
        return documentService.getAllDocuments();
    }

    /**
     * POST-метод для подписания документа по его ID.
     *
     * @param id Идентификатор документа, который нужно подписать.
     * @param signature Объект запроса, содержащий данные подписи.
     */
    /*
    @PostMapping("/{id}")
    public void signDocument(@PathVariable Long id, @RequestBody CreateSignatureRequest signature) {
        documentService.signDocument(id, signature);
    }
    */
}
