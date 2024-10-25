package com.example.ecm.controller;

import com.example.ecm.aop.Loggable;
import com.example.ecm.dto.requests.CreateDocumentRequest;
import com.example.ecm.dto.requests.CreateDocumentVersionRequest;
import com.example.ecm.dto.responses.CreateDocumentResponse;
import com.example.ecm.dto.responses.CreateDocumentVersionResponse;
import com.example.ecm.service.DocumentService;
import com.example.ecm.service.SignatureService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

    @GetMapping("/{documentId}/{versionId}")
    private ResponseEntity<CreateDocumentVersionResponse> getDocumentVersion(@PathVariable Long documentId, @PathVariable Long versionId) {
        return ResponseEntity.ok(documentService.getDocumentVersionById(documentId, versionId));
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

    /**
     * GET-ALL-метод для удаления документа по его ID.
     *
     * @return List<CreateDocumentTypeResponse>.
     */

    @GetMapping("/all")
    public ResponseEntity<List<CreateDocumentResponse>> getAllDocument() {
        return ResponseEntity.ok(documentService.getAllDocuments());
    }

    /**
     * Получает постраничный список файлов с возможностью сортировки.
     * Метод принимает параметры запроса для указания страницы, размера страницы, направления и поля сортировки,
     * и возвращает страницу с объектами {@link CreateDocumentResponse}.
     *
     * @param page          номер страницы, который нужно получить, по умолчанию 0.
     * @param size          количество элементов на странице, по умолчанию 10.
     * @param sortDirection направление сортировки (например, "asc" для по возрастанию или "desc" для по убыванию), по умолчанию "desc".
     * @param sortBy        поле, по которому выполняется сортировка, по умолчанию "createdAt".
     * @return объект {@link Page} с документами в формате {@link CreateDocumentResponse}.
     */
    @GetMapping
    public Page<CreateDocumentResponse> getAllFiles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(defaultValue = "createdAt") String sortBy) {

        return documentService.getAllDocuments(page, size, sortDirection, sortBy);
    }
}
