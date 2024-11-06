package com.example.ecm.controller;

import com.example.ecm.dto.responses.*;
import com.example.ecm.service.AnalyticService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;


/**
 * Контроллер для аналитики
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/analytics")
public class AnalyticController {

    private final AnalyticService analyticService;

    @GetMapping("/approvals")
    public ResponseEntity<List<UserApproval>> getApprovalsByUsers(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<UserApproval> approvals = analyticService.getApprovalsByUsers(startDate, endDate);
        return ResponseEntity.ok(approvals);
    }

    @GetMapping("/votings")
    public ResponseEntity<List<VotingSummary>> getVotingSummaries(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<VotingSummary> summaries = analyticService.getVotingSummaries(startDate, endDate);
        return ResponseEntity.ok(summaries);
    }

    @GetMapping("/active-users")
    public ResponseEntity<List<ActiveUserProjection>> getMostActiveUsers(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<ActiveUserProjection> activeUsers = analyticService.getMostActiveUsers(startDate, endDate);
        return ResponseEntity.ok(activeUsers);
    }

    @GetMapping("/ignored-votes")
    public ResponseEntity<List<IgnoredVotes>> getIgnoredVotes(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<IgnoredVotes> ignoredVotes = analyticService.getIgnoredVotes(startDate, endDate);
        return ResponseEntity.ok(ignoredVotes);
    }

    @GetMapping("/signature")
    public ResponseEntity<List<SignatureStatus>> getSignatures(){
        return ResponseEntity.ok(analyticService.getCountSignatureRequestStatus());
    }

    @GetMapping("/users-signatures")
    public ResponseEntity<List<UserSignaturesSummary>> getUsersSignaturesSummary() {
        return ResponseEntity.ok(analyticService.getUsersSignaturesSummary());
    }

    @GetMapping("/document-signatures-stats")
    public ResponseEntity<List<DocumentSignatureRequestStatistics>> getDocumentSignaturesStatistics() {
        return ResponseEntity.ok(analyticService.getDocumentSignatureRequestStatistics());
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