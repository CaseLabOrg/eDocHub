package com.example.ecm.service;

import com.example.ecm.dto.patch_requests.PatchDocumentTypeRequest;
import com.example.ecm.dto.requests.CreateDocumentTypeRequest;
import com.example.ecm.dto.responses.CreateDocumentTypeResponse;
import com.example.ecm.exception.NotFoundException;
import com.example.ecm.mapper.DocumentTypeMapper;
import com.example.ecm.model.DocumentType;
import com.example.ecm.repository.DocumentTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentTypeService {
    private final DocumentTypeRepository documentTypeRepository;
    private final DocumentTypeMapper documentTypeMapper;

    /**
     * Создает новый тип документа.
     *
     * @param request запрос на создание типа документа
     * @return ответ с данными созданного типа документа
     */
    public CreateDocumentTypeResponse createDocumentType(CreateDocumentTypeRequest request) {
        DocumentType documentType = documentTypeRepository.save(documentTypeMapper.toDocumentType(request));
        return documentTypeMapper.toCreateDocumentTypeResponse(documentType);
    }

    /**
     * Получает тип документа по идентификатору.
     *
     * @param id идентификатор типа документа
     * @return ответ с данными типа документа
     */
    public CreateDocumentTypeResponse getDocumentTypeById(Long id) {
        return documentTypeRepository.findById(id)
                .map(documentTypeMapper::toCreateDocumentTypeResponse)
                .orElseThrow(() -> new NotFoundException("Document with id: " + id + " not found"));
    }

    /**
     * Получает все типы документов.
     *
     * @return список ответов с данными всех типов документов
     */
    public List<CreateDocumentTypeResponse> getAllDocumentTypes() {
        return documentTypeRepository.findAll().stream()
                .map(documentTypeMapper::toCreateDocumentTypeResponse)
                .toList();
    }

    /**
     * Обновляет тип документа по идентификатору.
     *
     * @param id      идентификатор типа документа
     * @param request запрос на обновление типа документа
     * @return ответ с данными обновленного типа документа
     */
    public CreateDocumentTypeResponse updateDocumentType(Long id, CreateDocumentTypeRequest request) {
        DocumentType documentType = documentTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Document with id: " + id + " not found"));
        documentType.setId(id);
        documentType.setName(request.getName());

        return documentTypeMapper.toCreateDocumentTypeResponse(documentTypeRepository.save(documentType));
    }

    /**
     * Удаляет тип документа по идентификатору.
     *
     * @param id идентификатор типа документа
     */
    public void deleteDocumentType(Long id) {
        DocumentType documentType = documentTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Document with id: " + id + " not found"));
        documentTypeRepository.delete(documentType);
    }

    /**
     * Частично обновляет существующий тип документа на основе переданных изменений.
     *
     * <p>Метод находит тип документа по указанному ID и обновляет только те поля,
     * которые переданы в объекте {@link PatchDocumentTypeRequest}. Если в запросе
     * указано новое имя, оно обновляется, а затем тип документа сохраняется в базе данных.</p>
     *
     * @param id      идентификатор типа документа, который требуется обновить
     * @param request объект {@link PatchDocumentTypeRequest}, содержащий данные для частичного обновления типа документа
     * @return объект {@link CreateDocumentTypeResponse}, содержащий обновленные данные о типе документа
     * @throws NotFoundException если тип документа с указанным ID не найден
     */
    public CreateDocumentTypeResponse patchDocumentType(Long id, PatchDocumentTypeRequest request) {
        DocumentType documentType = documentTypeRepository.findById(id).orElseThrow(() -> new NotFoundException("Document with id: " + id + " not found"));

        if (request.getName() != null) {
            documentType.setName(request.getName());
        }

        return documentTypeMapper.toCreateDocumentTypeResponse(documentTypeRepository.save(documentType));
    }
}
