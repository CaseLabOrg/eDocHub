package com.example.ecm.service;

import com.example.ecm.dto.responses.DocumentCountByTypeResponse;
import com.example.ecm.dto.responses.DocumentStatusCountResponse;
import com.example.ecm.dto.responses.DocumentTypePercentageResponse;
import com.example.ecm.repository.AnalyticRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Аналитика документов и пользователей
 */
@Service
public class AnalyticService {
    private final AnalyticRepository analyticRepository;

    public AnalyticService(AnalyticRepository analyticRepository) {
        this.analyticRepository = analyticRepository;
    }

    /**
     * Количество документов по типам.
     * @return Список DocumentCountByTypeRequest с информацией о количестве документов по каждому типу
     */
    public List<DocumentCountByTypeResponse> getDocumentCountByType() {
        return analyticRepository.countDocumentsByType();
    }

    /**
     * Количество активных и неактивных документов.
     *
     * @return Список DocumentStatusCountResponse с информацией о количестве активных и неактивных документов
     */
    public List<DocumentStatusCountResponse> getDocumentStatusCounts() {
        return analyticRepository.countDocumentsByStatus();
    }

    /**
     * Процентное соотношение документов по типам.
     *
     * @return Список DocumentTypePercentageResponse с процентным соотношением документов по каждому типу
     */
    public List<DocumentTypePercentageResponse> getDocumentTypePercentages() {
        List<DocumentCountByTypeResponse> documentCountByType = analyticRepository.countDocumentsByType();
        List<DocumentTypePercentageResponse> documentTypePercentageResponses = new ArrayList<>();
        Long countType = 0L;
        for (DocumentCountByTypeResponse documentCountByTypeResponse : documentCountByType) {
            countType += documentCountByTypeResponse.getDocumentCount();
        }
        for (DocumentCountByTypeResponse documentCountByTypeResponse : documentCountByType) {
            DocumentTypePercentageResponse documentTypePercentageResponse = new DocumentTypePercentageResponse(documentCountByTypeResponse.getDocumentType(), documentCountByTypeResponse.getDocumentCount() * 100.0 / countType);
            documentTypePercentageResponses.add(documentTypePercentageResponse);
        }
        return documentTypePercentageResponses;
    }
}
