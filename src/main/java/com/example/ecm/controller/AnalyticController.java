package com.example.ecm.controller;


import com.example.ecm.dto.responses.DocumentCountByTypeResponse;
import com.example.ecm.dto.responses.DocumentStatusCountResponse;
import com.example.ecm.dto.responses.DocumentTypePercentageResponse;
import com.example.ecm.service.AnalyticService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Контроллер для аналитики
 */
@RestController
@RequestMapping("/analytic")
public class AnalyticController {

    private final AnalyticService analyticService;

    public AnalyticController(AnalyticService analyticService) {
        this.analyticService = analyticService;
    }

    @GetMapping("/{id}")
    public void getAnalyticByUserId() {

    }

    /**
     * Получает количество документов по типам
     *
     * @return Список DocumentCountByTypeResponse c информацией о количестве документов каждого типа
     */
    @GetMapping("/documents/count-by-type")
    public List<DocumentCountByTypeResponse> getDocumentCountByType() {
        return analyticService.getDocumentCountByType();
    }

    /**
     * Возвращает количество активных и неактивных документов.
     *
     * @return Список DocumentStatusCount с информацией о количестве активных и неактивных документов
     */
    @GetMapping("/documents/status-count")
    public List<DocumentStatusCountResponse> getDocumentStatusCounts() {
        return analyticService.getDocumentStatusCounts();
    }

    /**
     * Возвращает процентное соотношение типов документов.
     *
     * @return Список DocumentTypePercentage с процентным соотношением документов по типам
     */
    @GetMapping("/documents/type-percentage")
    public List<DocumentTypePercentageResponse> getDocumentTypePercentages() {
        return analyticService.getDocumentTypePercentages();
    }
}
