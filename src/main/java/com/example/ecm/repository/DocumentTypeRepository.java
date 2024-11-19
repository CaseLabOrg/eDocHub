package com.example.ecm.repository;

import com.example.ecm.model.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с сущностями DocumentType.
 * Обеспечивает стандартные операции CRUD для типов документов.
 */
@Repository
public interface DocumentTypeRepository extends JpaRepository<DocumentType, Long> {
    List<DocumentType> findDocumentTypesByIdIsIn(List<Long> ids);
}
