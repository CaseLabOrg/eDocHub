package com.example.ecm.service;

import com.example.ecm.dto.patch_requests.PatchDocumentTypeRequest;
import com.example.ecm.dto.requests.CreateDocumentTypeRequest;
import com.example.ecm.dto.responses.CreateDocumentTypeResponse;
import com.example.ecm.exception.NotFoundException;
import com.example.ecm.mapper.DocumentTypeMapper;
import com.example.ecm.model.Attribute;
import com.example.ecm.model.DocumentType;
import com.example.ecm.repository.AttributeRepository;
import com.example.ecm.repository.DocumentTypeRepository;
import com.example.ecm.repository.TenantRepository;
import com.example.ecm.saas.TenantContext;
import com.example.ecm.saas.annotation.TenantRestrictedForDocumentType;
import com.example.ecm.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DocumentTypeService {
    private final DocumentTypeRepository documentTypeRepository;
    private final AttributeRepository attributeRepository;
    private final DocumentTypeMapper documentTypeMapper;
    private final TenantRepository tenantRepository;
    private final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    /**
     * Создает новый тип документа.
     *
     * @param request запрос на создание типа документа
     * @return ответ с данными созданного типа документа
     */
    public CreateDocumentTypeResponse createDocumentType(CreateDocumentTypeRequest request) {
        DocumentType documentType = documentTypeRepository.save(documentTypeMapper.toDocumentType(request));
        List<Attribute> attributes = attributeRepository.findAttributesByIdIsIn(request.getAttributeIds());
        documentType.setAttributes(attributes);
        documentType.setTenant(tenantRepository.findById(TenantContext.getCurrentTenantId()).orElseThrow( () -> new NotFoundException("Tenant not found")));
        return documentTypeMapper.toCreateDocumentTypeResponse(documentType);
    }

    /**
     * Получает тип документа по идентификатору.
     *
     * @param id идентификатор типа документа
     * @return ответ с данными типа документа
     */
    @TenantRestrictedForDocumentType
    public CreateDocumentTypeResponse getDocumentTypeById(Long id, Boolean isAlive) {
        Optional<DocumentType> documentType = documentTypeRepository.findById(id);

        documentType = documentType.filter(t -> t.getIsAlive().equals(isAlive));

        return documentType
                .map(documentTypeMapper::toCreateDocumentTypeResponse)
                .orElseThrow(() -> new NotFoundException("DocumentType with id: " + id + " not found"));
    }

    /**
     * Получает все типы документов.
     *
     * @return список ответов с данными всех типов документов
     */

    public List<CreateDocumentTypeResponse> getAllDocumentTypes(Boolean isAlive) {
        Stream<DocumentType> documentTypeStream = documentTypeRepository.findAll().stream();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        documentTypeStream = documentTypeStream.filter(t -> t.getIsAlive().equals(isAlive));

        if(!userPrincipal.isAdmin()) {
            documentTypeStream = documentTypeStream.filter(d -> d.getTenant().getId().equals(TenantContext.getCurrentTenantId()));
        }

        return documentTypeStream
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
    @TenantRestrictedForDocumentType
    public CreateDocumentTypeResponse updateDocumentType(Long id, CreateDocumentTypeRequest request) {
        DocumentType documentType = documentTypeRepository.findById(id)
                .filter(DocumentType::getIsAlive)
                .orElseThrow(() -> new NotFoundException("DocumentType with id: " + id + " not found"));
        List<Attribute> attributes = attributeRepository.findAttributesByIdIsIn(request.getAttributeIds());
        documentType.setAttributes(attributes);
        documentType.setId(id);
        documentType.setName(request.getName());

        return documentTypeMapper.toCreateDocumentTypeResponse(documentTypeRepository.save(documentType));
    }

    /**
     * Удаляет тип документа по идентификатору.
     *
     * @param id идентификатор типа документа
     */
    @TenantRestrictedForDocumentType
    public void deleteDocumentType(Long id) {
        DocumentType documentType = documentTypeRepository.findById(id)
                .filter(DocumentType::getIsAlive)
                .orElseThrow(() -> new NotFoundException("DocumentType with id: " + id + " not found"));
        documentType.setIsAlive(false);
        documentTypeRepository.save(documentType);
    }

    @TenantRestrictedForDocumentType
    public void recoverDocumentType(Long id) {
        DocumentType documentType = documentTypeRepository.findById(id)
                .filter(t -> !t.getIsAlive())
                .orElseThrow(() -> new NotFoundException("Deleted DocumentType with id: " + id + " not found"));
        documentType.setIsAlive(true);
        documentTypeRepository.save(documentType);
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
    @TenantRestrictedForDocumentType
    public CreateDocumentTypeResponse patchDocumentType(Long id, PatchDocumentTypeRequest request) {
        DocumentType documentType = documentTypeRepository.findById(id)
                .filter(DocumentType::getIsAlive)
                .orElseThrow(() -> new NotFoundException("DocumentType with id: " + id + " not found"));

        if (request.getName() != null) {
            documentType.setName(request.getName());
        }

        return documentTypeMapper.toCreateDocumentTypeResponse(documentTypeRepository.save(documentType));
    }



}
