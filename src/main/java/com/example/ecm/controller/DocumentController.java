package com.example.ecm.controller;

import com.example.ecm.aop.Loggable;
import com.example.ecm.dto.patch_requests.PatchDocumentVersionRequest;
import com.example.ecm.dto.requests.AddCommentRequest;
import com.example.ecm.dto.requests.CreateDocumentRequest;
import com.example.ecm.dto.requests.CreateDocumentVersionRequest;
import com.example.ecm.dto.responses.AddCommentResponse;
import com.example.ecm.dto.responses.CreateDocumentResponse;
import com.example.ecm.dto.responses.CreateDocumentVersionResponse;
import com.example.ecm.model.enums.DocumentState;
import com.example.ecm.security.UserPrincipal;
import com.example.ecm.service.DocumentService;
import com.example.ecm.service.DocumentStateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.io.IOException;
import java.util.List;

@Tag(name = "Document Controller", description = "Контроллер для управления документами")
@RestController
@RequiredArgsConstructor
@RequestMapping("/documents")
@Loggable
public class DocumentController {

    private final DocumentService documentService;
    private final DocumentStateService documentStateService;

    /**
     * Создает новый документ.
     *
     * @param createDocumentRequest Запрос, содержащий данные для создания документа.
     * @return Ответ с информацией о созданном документе.
     */
    @Operation(summary = "Создание документа", description = "Создает новый документ с заданными данными")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Документ успешно создан"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос")
    })
    @PostMapping
    public ResponseEntity<CreateDocumentResponse> createFile(@Valid @RequestBody CreateDocumentRequest createDocumentRequest) {
        return ResponseEntity.ok(documentService.createDocument(createDocumentRequest));
    }

    /**
     * Получает документ по его ID.
     *
     * @param id Идентификатор документа.
     * @param isAlive Параметр для отображения только активных документов.
     * @param userPrincipal Аутентифицированный пользователь.
     * @return Ответ с данными документа.
     */
    @Operation(summary = "Получение документа", description = "Возвращает данные документа по его идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Документ успешно получен"),
            @ApiResponse(responseCode = "404", description = "Документ не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CreateDocumentResponse> getDocument(
            @PathVariable Long id,
            @RequestParam(defaultValue = "true") Boolean isAlive,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(documentService.getDocumentById(id, isAlive, userPrincipal));
    }

    @GetMapping("/{id}/transitions")
    public ResponseEntity<List<DocumentState>> getTransitions(
            @PathVariable Long id,
            @RequestParam(defaultValue = "true") Boolean isAlive,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(documentStateService.getTransitionsByDocumentId(id, isAlive, userPrincipal));
    }

    /**
     * Получает версию документа по ID.
     *
     * @param documentId Идентификатор документа.
     * @param versionId Идентификатор версии.
     * @param isAlive Параметр для отображения только активных версий.
     * @return Ответ с данными версии документа.
     */
    @Operation(summary = "Получение версии документа", description = "Возвращает данные определенной версии документа")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Версия документа успешно получена"),
            @ApiResponse(responseCode = "404", description = "Версия документа не найдена")
    })
    @GetMapping("/{documentId}/{versionId}")
    public ResponseEntity<CreateDocumentVersionResponse> getDocumentVersion(
            @PathVariable Long documentId,
            @PathVariable Long versionId,
            @RequestParam(defaultValue = "true") Boolean isAlive,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(documentService.getDocumentVersionById(documentId, versionId, isAlive, userPrincipal));
    }

    /**
     * Обновляет существующий документ.
     *
     * @param id Идентификатор документа, который нужно обновить.
     * @param document Объект запроса с новыми данными документа.
     * @return Ответ с обновленной версией документа.
     */
    @Operation(summary = "Обновление документа", description = "Обновляет данные существующего документа")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Документ успешно обновлен"),
            @ApiResponse(responseCode = "404", description = "Документ не найден"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CreateDocumentVersionResponse> updateDocument(
            @PathVariable Long id,
            @Valid @RequestBody CreateDocumentVersionRequest document) {
        return ResponseEntity.ok(documentService.updateDocumentVersion(id, document));
    }

    /**
     * Удаляет документ по его ID.
     *
     * @param id Идентификатор документа.
     * @return Ответ без содержимого.
     */
    @Operation(summary = "Удаление документа", description = "Удаляет документ с заданным идентификатором")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Документ успешно удален"),
            @ApiResponse(responseCode = "404", description = "Документ не найден")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) throws IOException {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Восстанавливает ранее удаленный документ.
     *
     * @param id Идентификатор документа.
     * @return Ответ без содержимого.
     */
    @Operation(summary = "Восстановление документа", description = "Восстанавливает ранее удаленный документ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Документ успешно восстановлен"),
            @ApiResponse(responseCode = "404", description = "Документ не найден")
    })
    @PatchMapping("/{id}/recover")
    public ResponseEntity<Void> recoverDocument(@PathVariable Long id) throws IOException {
        documentService.recoverDocument(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Возвращает список всех документов с возможностью пагинации и фильтрации.
     *
     * @return Страница с документами.
     */
    @Operation(summary = "Получение списка документов", description = "Возвращает список всех документов с пагинацией")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список документов успешно получен"),
            @ApiResponse(responseCode = "404", description = "Документы не найдены")
    })

    @GetMapping
    public ResponseEntity<List<CreateDocumentResponse>> getAllDocuments(
            @RequestParam(defaultValue = "true") Boolean isAlive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "false") boolean ascending,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(documentService.getAllDocuments(page, size, ascending, isAlive, userPrincipal));
    }

    @GetMapping("/countDocuments")
    public int getCountDocuments(
            @RequestParam(defaultValue = "true") Boolean isAlive,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return documentService.getCountDocuments(isAlive, userPrincipal);
    }


    /**
     * Частично обновляет документ.
     *
     * @param id Идентификатор документа.
     * @param request Объект запроса для частичного обновления.
     * @return Ответ с обновленной версией документа.
     */
    @Operation(summary = "Частичное обновление документа", description = "Обновляет отдельные поля документа")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Документ успешно обновлен"),
            @ApiResponse(responseCode = "404", description = "Документ не найден"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<CreateDocumentVersionResponse> patchDocumentType(
            @PathVariable Long id,
            @Valid @RequestBody PatchDocumentVersionRequest request) {
        return ResponseEntity.ok(documentService.patchDocument(id, request));
    }

    /**
     * Добавляет комментарий к документу.
     *
     * @param id Идентификатор документа.
     * @param createCommentRequest Объект запроса с данными комментария.
     * @param userPrincipal Аутентифицированный пользователь.
     * @return Ответ с данными добавленного комментария.
     */
    @Operation(summary = "Добавление комментария", description = "Добавляет новый комментарий к документу")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Комментарий успешно добавлен"),
            @ApiResponse(responseCode = "404", description = "Документ не найден"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос")
    })
    @PostMapping("/{id}/comment")
    public ResponseEntity<AddCommentResponse> addComment(
            @PathVariable Long id,
            @Valid @RequestBody AddCommentRequest createCommentRequest,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(documentService.addComment(id, createCommentRequest, userPrincipal));
    }
}
