package com.example.ecm.repository;

import com.example.ecm.dto.responses.ActiveUserProjection;
import com.example.ecm.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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

    @Query("""
    SELECT u AS user, COUNT(dv.id) AS documentsCreated
    FROM Document d
    JOIN d.user u
    JOIN DocumentVersion dv ON dv.document = d
    WHERE d.isAlive = true
      AND dv.createdAt BETWEEN :startDate AND :endDate
    GROUP BY u
    ORDER BY documentsCreated DESC
    """)
    List<ActiveUserProjection> findMostActiveUsers(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

}
