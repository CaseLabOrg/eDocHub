package com.example.ecm.service;

import com.example.ecm.dto.patch_requests.PatchAttributeRequest;
import com.example.ecm.dto.requests.CreateAttributeRequest;
import com.example.ecm.dto.responses.CreateAttributeResponse;
import com.example.ecm.exception.NotFoundException;
import com.example.ecm.mapper.AttributeMapper;
import com.example.ecm.model.Attribute;
import com.example.ecm.model.DocumentType;
import com.example.ecm.repository.AttributeRepository;
import com.example.ecm.repository.DocumentTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.Collectors;

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
        List<DocumentType> documentTypes = documentTypeRepository.findDocumentTypesByIdIsIn(request.getDocumentTypesIds());

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
    public CreateAttributeResponse getAttributeById(Long id, Boolean showOnlyALive) {
        Optional<Attribute> attribute = attributeRepository.findById(id);

        if (showOnlyALive) {
            attribute = attribute.filter(Attribute::getIsAlive);
        }

        return attribute
                .map(attributeMapper::toAttributeResponse)
                .orElseThrow(() -> new NotFoundException("Attribute with id: " + id + " not found"));
    }

    /**
     * Получает все атрибуты документов.
     *
     * @return список ответов с данными всех атрибутов документов
     */
    public Page<CreateAttributeResponse> getAttributes(Pageable pageable) {
        Page<Attribute> attributePage = attributeRepository.findAll(pageable);


        return new PageImpl<>(
                attributePage.stream()
                        .map(attributeMapper::toAttributeResponse)
                        .collect(Collectors.toList()),
                pageable,
                attributePage.getTotalElements()
        );
    public List<CreateAttributeResponse> getAllAttributes(Boolean showOnlyALive) {
        Stream<Attribute> attributeStream = attributeRepository.findAll().stream();

        if (showOnlyALive) {
            attributeStream = attributeStream.filter(Attribute::getIsAlive);
        }

        return attributeStream
                .map(attributeMapper::toAttributeResponse)
                .toList();
    public Page<CreateAttributeResponse> getAttributes(Pageable pageable) {
        Page<Attribute> attributePage = attributeRepository.findAll(pageable);


        return new PageImpl<>(
                attributePage.stream()
                        .map(attributeMapper::toAttributeResponse)
                        .collect(Collectors.toList()),
                pageable,
                attributePage.getTotalElements()
        );
    }

    /**
     * Обновляет атрибут документа по идентификатору.
     *
     * @param id      идентификатор атрибута документа
     * @param request запрос на обновление атрибута документа
     * @return ответ с данными обновленного атрибута документа
     */
    public CreateAttributeResponse updateAttribute(Long id, CreateAttributeRequest request) {
        Attribute attribute = attributeRepository.findById(id)
                .filter(Attribute::getIsAlive)
                .orElseThrow(() -> new NotFoundException("Attribute with id: " + id + " not found"));
        List<DocumentType> documentTypes = documentTypeRepository.findDocumentTypesByIdIsIn(request.getDocumentTypesIds());

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
        Attribute attribute = attributeRepository.findById(id)
                .filter(Attribute::getIsAlive)
                .orElseThrow(() -> new NotFoundException("Attribute with id: " + id + " not found"));
        attribute.setIsAlive(false);
        attributeRepository.save(attribute);
    }

    public void recoverAttribute(Long id) {
        Attribute attribute = attributeRepository.findById(id)
                .filter(attr -> !attr.getIsAlive())
                .orElseThrow(() -> new NotFoundException("Deleted Attribute with id: " + id + " not found"));
        attribute.setIsAlive(true);
        attributeRepository.save(attribute);
    }

    /**
     * Частичное обновление атрибута документа по идентификатору.
     *
     * @param id      идентификатор атрибута документа
     * @param request запрос на частичное обновление атрибута документа
     * @return ответ с данными обновленного атрибута документа
     */

    public CreateAttributeResponse patchAttribute(Long id, PatchAttributeRequest request) {
        Attribute attribute = attributeRepository.findById(id)
                .filter(Attribute::getIsAlive)
                .orElseThrow(() -> new NotFoundException("Attribute with id: " + id + " not found"));
        if (request.getName() != null) {
            attribute.setName(request.getName());
        }

        if (request.getRequired() != null) {
            attribute.setRequired(request.getRequired());
        }

        if (request.getDocumentTypesIds() != null && !request.getDocumentTypesIds().isEmpty()) {
            List<DocumentType> documentTypes = documentTypeRepository.findDocumentTypesByIdIsIn(request.getDocumentTypesIds());
            attribute.setDocumentTypes(documentTypes);
        }


        return attributeMapper.toAttributeResponse(attributeRepository.save(attribute));
    }
}
