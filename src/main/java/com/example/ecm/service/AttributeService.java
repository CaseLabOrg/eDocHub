package com.example.ecm.service;

import com.example.ecm.dto.CreateAttributeRequest;
import com.example.ecm.dto.CreateAttributeResponse;
import com.example.ecm.exception.NotFoundException;
import com.example.ecm.mapper.AttributeMapper;
import com.example.ecm.model.Attribute;
import com.example.ecm.model.DocumentType;
import com.example.ecm.repository.AttributeRepository;
import com.example.ecm.repository.DocumentTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для работы с атрибутами документов.
 * Обеспечивает создание, получение, обновление и удаление атрибутов документов.
 */
@Service
@RequiredArgsConstructor
public class AttributeService {

    private final AttributeRepository attributeRepository;
    private final DocumentTypeRepository documentTypeRepository;
    private final AttributeMapper attributeMapper;

    /**
     * Создает новый атрибут документа.
     *
     * @param request запрос на создание атрибута документа
     * @return ответ с данными созданного атрибута документа
     */
    public CreateAttributeResponse createAttribute(CreateAttributeRequest request) {
        List<DocumentType> documentTypes = documentTypeRepository.findDocumentTypesByNameIsIn(request.getDocumentTypesNames());
        if (documentTypes.isEmpty()) throw new NotFoundException("Document type not found");

        Attribute attribute = attributeMapper.toAttribute(request);
        attribute.getDocumentTypes().addAll(documentTypes);
        return attributeMapper.toAttributeResponse(
                attributeRepository.save(attribute)
        );
    }

    /**
     * Получает атрибут документа по идентификатору.
     *
     * @param id идентификатор атрибута документа
     * @return ответ с данными атрибута документа
     */
    public CreateAttributeResponse getAttributeById(Long id) {
        return attributeRepository.findById(id)
                .map(attributeMapper::toAttributeResponse)
                .orElseThrow(() -> new NotFoundException("Attribute not found"));
    }

    /**
     * Получает все атрибуты документов.
     *
     * @return список ответов с данными всех атрибутов документов
     */
    public List<CreateAttributeResponse> getAllAttributes() {
        return attributeRepository.findAll().stream()
                .map(attributeMapper::toAttributeResponse)
                .toList();
    }

    /**
     * Обновляет атрибут документа по идентификатору.
     *
     * @param id идентификатор атрибута документа
     * @param request запрос на обновление атрибута документа
     * @return ответ с данными обновленного атрибута документа
     */
    public CreateAttributeResponse updateAttribute(Long id, CreateAttributeRequest request) {
        Attribute attribute = attributeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attribute not found"));
        List<DocumentType> documentTypes = documentTypeRepository.findDocumentTypesByNameIsIn(request.getDocumentTypesNames());

        attribute.setDocumentTypes(documentTypes);
        attribute.setName(request.getName());
        attribute.setRequired(request.getRequired());
        return attributeMapper.toAttributeResponse(attributeRepository.save(attribute));
    }

    /**
     * Удаляет атрибут документа по идентификатору.
     *
     * @param id идентификатор атрибута документа
     */
    public void deleteAttribute(Long id) {
        attributeRepository.deleteById(id);
    }
}
