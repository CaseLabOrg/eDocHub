package com.example.ecm.controller;

import com.example.ecm.aop.Loggable;
import com.example.ecm.dto.patch_requests.PatchDocumentTypeRequest;
import com.example.ecm.dto.requests.CreateDocumentTypeRequest;
import com.example.ecm.dto.responses.CreateDocumentTypeResponse;
import com.example.ecm.service.DocumentTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    private final DocumentTypeService documentTypeService;

    /**
     * POST-метод для создания нового типа документа.
     *
     * @param request Объект запроса с данными для создания нового типа документа.
     * @return Ответ с данными созданного типа документа.
     */
    @Operation(summary = "Создать новый тип документа", description = "Создает новый тип документа на основе предоставленных данных")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешное создание типа документа"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос")
    })
    @PostMapping
    public ResponseEntity<CreateDocumentTypeResponse> createDocumentType(
            @Valid @RequestBody CreateDocumentTypeRequest request) {
        return ResponseEntity.ok(documentTypeService.createDocumentType(request));
    }

    /**
     * GET-метод для получения типа документа по его ID.
     *
     * @param id Идентификатор типа документа.
     * @return Ответ с данными типа документа.
     */
    @Operation(summary = "Получить тип документа по ID", description = "Возвращает данные типа документа по указанному ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Тип документа найден"),
            @ApiResponse(responseCode = "404", description = "Тип документа не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CreateDocumentTypeResponse> getDocumentTypeById(
            @PathVariable Long id,
            @RequestParam(defaultValue = "true") Boolean showOnlyAlive) {
        return ResponseEntity.ok(documentTypeService.getDocumentTypeById(id, showOnlyAlive));
    }

    /**
     * GET-метод для получения всех существующих типов документов.
     *
     * @return Список всех типов документов.
     */
    @Operation(summary = "Получить все типы документов", description = "Возвращает список всех типов документов")
    @ApiResponse(responseCode = "200", description = "Успешное получение списка типов документов")
    @GetMapping
    public List<CreateDocumentTypeResponse> getAllDocumentTypes(
            @RequestParam(defaultValue = "true") Boolean showOnlyAlive) {
        return documentTypeService.getAllDocumentTypes(showOnlyAlive);
    }

    /**
     * PUT-метод для обновления существующего типа документа по его ID.
     *
     * @param id      Идентификатор типа документа, который нужно обновить.
     * @param request Объект запроса с новыми данными для обновления типа документа.
     * @return Ответ с обновленными данными типа документа.
     */
    @Operation(summary = "Обновить тип документа", description = "Обновляет тип документа по указанному ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Тип документа успешно обновлен"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "404", description = "Тип документа не найден")
    })
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CreateDocumentTypeResponse> updateDocumentType(
            @PathVariable Long id,
            @Valid @RequestBody CreateDocumentTypeRequest request) {
        return ResponseEntity.ok(documentTypeService.updateDocumentType(id, request));
    }

    /**
     * DELETE-метод для удаления типа документа по его ID.
     *
     * @param id Идентификатор типа документа, который нужно удалить.
     * @return Ответ без содержимого (No Content) после успешного удаления.
     */
    @Operation(summary = "Удалить тип документа", description = "Удаляет тип документа по указанному ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Тип документа успешно удален"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "404", description = "Тип документа не найден")
    })
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocumentTypeById(@PathVariable Long id) {
        documentTypeService.deleteDocumentType(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * PATCH-метод для восстановления типа документа по его ID.
     *
     * @param id Идентификатор типа документа.
     * @return Ответ без содержимого (No Content) после успешного восстановления.
     */
    @Operation(summary = "Восстановить тип документа", description = "Восстанавливает удаленный тип документа по указанному ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Тип документа успешно восстановлен"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "404", description = "Тип документа не найден")
    })
    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/{id}/recover")
    public ResponseEntity<Void> recoverAttribute(@PathVariable Long id) {
        documentTypeService.recoverDocumentType(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * PATCH-метод для обновления типа документа.
     *
     * @param id      идентификатор типа документа.
     * @param request Объект запроса с новыми данными.
     * @return Обновленный тип документа.
     */
    @Operation(summary = "Обновить поля типа документа", description = "Обновляет поля типа документа по указанному ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Тип документа успешно обновлен"),
            @ApiResponse(responseCode = "404", description = "Тип документа не найден")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<CreateDocumentTypeResponse> patchDocumentType(
            @PathVariable Long id,
            @Valid @RequestBody PatchDocumentTypeRequest request) {
        return ResponseEntity.ok(documentTypeService.patchDocumentType(id, request));
    }
}
