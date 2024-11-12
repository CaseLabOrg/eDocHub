package com.example.ecm.repository;

import com.example.ecm.dto.responses.DocumentCountByTypeResponse;
import com.example.ecm.dto.responses.DocumentStatusCountResponse;
import com.example.ecm.model.Document;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для запросов аналитики
 */
@Repository
public interface AnalyticRepository extends CrudRepository<Document, Long>{
    /**
     * Подсчитывает количество документов по каждому типу.
     *
     * @return Список объектов DocumentCountByTypeResponse, содержащих DTO типа документа и количество документов.
     */
    @Query("SELECT new com.example.ecm.dto.responses.DocumentCountByTypeResponse(" +
            "   dt.name, " +
            "   COUNT(d.id)" +
            ") " +
            "FROM Document d JOIN DocumentType dt ON d.documentType = dt " +
            "GROUP BY dt.name")
    List<DocumentCountByTypeResponse> countDocumentsByType();


    /**
     * Подсчитывает количество активных и неактивных документов.
     *
     * @return Список объектов DocumentStatusCountResponse, содержащих статус документа и количество.
     */
    @Query("SELECT new com.example.ecm.dto.responses.DocumentStatusCountResponse(d.isAlive, COUNT(d.id)) " +
            "FROM Document d " +
            "GROUP BY d.isAlive")
    List<DocumentStatusCountResponse> countDocumentsByStatus();
}
