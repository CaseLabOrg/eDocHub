package com.example.ecm.service;

import com.example.ecm.dto.CreateDocumentTypeRequest;
import com.example.ecm.dto.CreateDocumentTypeResponse;
import com.example.ecm.mapper.DocumentTypeMapper;
import com.example.ecm.model.Attribute;
import com.example.ecm.model.DocumentType;
import com.example.ecm.repository.AttributeRepository;
import com.example.ecm.repository.DocumentTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentTypeService {
    private final DocumentTypeRepository documentTypeRepository;
    private final DocumentTypeMapper documentTypeMapper;
    private final AttributeRepository attributeRepository;

    /**
     * Создает новый тип документа.
     *
     * @param request запрос на создание типа документа
     * @return ответ с данными созданного типа документа
     */
    public CreateDocumentTypeResponse createDocumentType(CreateDocumentTypeRequest request) {
        DocumentType documentType =  documentTypeRepository.save(documentTypeMapper.toDocumentType(request));
        List<Attribute> attributes = request.getAttributesIds().stream()
                .map(id -> attributeRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Attribute not found")))
                .toList();
        documentType.setAttributes(attributes);
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
                .orElseThrow(() -> new RuntimeException("Document Type not found"));
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
     * @param id идентификатор типа документа
     * @param request запрос на обновление типа документа
     * @return ответ с данными обновленного типа документа
     */
    public CreateDocumentTypeResponse updateDocumentType(Long id, CreateDocumentTypeRequest request) {
        DocumentType documentType = documentTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document Type not found"));
        documentType.setId(id);
        documentType.setName(request.getName());
        List<Attribute> attributes = request.getAttributesIds().stream()
                .map(aId -> attributeRepository.findById(aId)
                        .orElseThrow(() -> new RuntimeException("Attribute not found")))
                .toList();
        documentType.setAttributes(attributes);
        return documentTypeMapper.toCreateDocumentTypeResponse(documentTypeRepository.save(documentType));
    }

    /**
     * Удаляет тип документа по идентификатору.
     *
     * @param id идентификатор типа документа
     */
    public void deleteDocumentType(Long id) {
        documentTypeRepository.deleteById(id);
    }
}
