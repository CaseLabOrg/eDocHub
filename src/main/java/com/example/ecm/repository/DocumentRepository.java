package com.example.ecm.repository;

import com.example.ecm.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для работы с сущностями Document.
 * Предоставляет стандартные методы для взаимодействия с базой данных,
 * такие как создание, чтение, обновление и удаление (CRUD) документов.
 */
@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    // Здесь могут быть добавлены дополнительные методы для специфичных запросов к документам, если потребуется
}
