package com.example.ecm.repository;

import com.example.ecm.model.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для работы с сущностями DocumentType.
 * Обеспечивает стандартные операции CRUD для типов документов.
 */
@Repository
public interface DocumentTypeRepository extends JpaRepository<DocumentType, Long> {
    // Здесь могут быть добавлены дополнительные методы для специфичных запросов к типам документов, если потребуется
}
