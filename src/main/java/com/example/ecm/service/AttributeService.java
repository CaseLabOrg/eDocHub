package com.example.ecm.service;

import com.example.ecm.dto.AttributeRequest;
import com.example.ecm.dto.AttributeResponse;
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
    public AttributeResponse createAttribute(AttributeRequest request) {
        DocumentType documentType = documentTypeRepository.findByName(request.getDocumentTypeName())
                .orElseThrow(() -> new RuntimeException("Document Type not found"));

        return attributeMapper.toAttributeResponse(
                attributeRepository.save(attributeMapper.toAttribute(request, documentType))
        );
    }

    /**
     * Получает атрибут документа по идентификатору.
     *
     * @param id идентификатор атрибута документа
     * @return ответ с данными атрибута документа
     */
    public AttributeResponse getAttributeById(Long id) {
        return attributeRepository.findById(id)
                .map(attributeMapper::toAttributeResponse)
                .orElseThrow(() -> new RuntimeException("Attribute not found"));
    }

    /**
     * Получает все атрибуты документов.
     *
     * @return список ответов с данными всех атрибутов документов
     */
    public List<AttributeResponse> getAllAttributes() {
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
    public AttributeResponse updateAttribute(Long id, AttributeRequest request) {
        Attribute attribute = attributeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attribute not found"));
        DocumentType documentType = documentTypeRepository.findByName(request.getDocumentTypeName())
                .orElseThrow(() -> new RuntimeException("Document Type not found"));

        attribute.setDocumentType(documentType);
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
