package com.example.ecm.repository;

import com.example.ecm.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для работы с сущностями Document.
 * Предоставляет стандартные методы для взаимодействия с базой данных,
 * такие как создание, чтение, обновление и удаление (CRUD) документов.
 */
@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findAllByUserId(Long userId);

    //@Query("SELECT d FROM Document d LEFT JOIN SignatureRequest sr ON d.id = sr.document.id WHERE d.user.id = ?1 OR sr.userTo.id = ?1")
    //List<Document> findDocumentsBySignature(Long userId);
}
