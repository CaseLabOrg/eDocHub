package com.example.ecm.repository;

import com.example.ecm.dto.responses.ActiveUser;
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

    @Query(value = """
           SELECT 
               d.user_id AS userId,
               CONCAT(u.name, ' ', u.surname) AS userName,
               COUNT(d.id) AS documentsCreated
           FROM 
               Documents d
           JOIN 
               Users u ON d.user_id = u.id
           WHERE 
               d.is_alive = true 
               AND d.created_at BETWEEN :startDate AND :endDate
           GROUP BY 
               d.user_id, userName
           ORDER BY 
               documentsCreated DESC
           LIMIT 10
           """, nativeQuery = true)
    List<ActiveUser> findMostActiveUsers(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

}
