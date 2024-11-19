package com.example.ecm.service;

import com.example.ecm.dto.responses.*;
import com.example.ecm.model.SignatureRequest;
import com.example.ecm.repository.AnalyticRepository;
import com.example.ecm.repository.DocumentRepository;
import com.example.ecm.repository.SignatureRequestRepository;
import com.example.ecm.repository.VotingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticService {

    private final SignatureRequestRepository signatureRequestRepository;
    private final VotingRepository votingRepository;
    private final DocumentRepository documentRepository;
    private final AnalyticRepository analyticRepository;


    public List<UserApproval> getApprovalsByUsers(LocalDateTime startDate, LocalDateTime endDate) {
        return signatureRequestRepository.findApprovalsByUsers(startDate, endDate);
    }

    public List<VotingSummary> getVotingSummaries(LocalDateTime startDate, LocalDateTime endDate) {
        return votingRepository.findVotingSummaries(startDate, endDate);
    }

    public List<ActiveUserProjection> getMostActiveUsers(LocalDateTime startDate, LocalDateTime endDate) {
        return documentRepository.findMostActiveUsers(startDate, endDate);
    }

    public List<IgnoredVotes> getIgnoredVotes(LocalDateTime startDate, LocalDateTime endDate) {
        return signatureRequestRepository.findIgnoredVotes(startDate, endDate);
    }

    public List<SignatureStatus> getCountSignatureRequestStatus() {
        return signatureRequestRepository.findCountSignatureRequestStatus();
    }

    public List<UserSignaturesSummary> getUsersSignaturesSummary() {
        List<SignatureRequest> signatureRequests = signatureRequestRepository.findAll();

        return signatureRequests.stream()
                .collect(Collectors.groupingBy(
                        signatureRequest -> signatureRequest.getUserTo().getId()
                ))
                .entrySet().stream()
                .map(entry -> {
                    Long userId = entry.getKey();
                    var signatureRequestId2status = entry.getValue().stream()
                            .collect(Collectors.toMap(
                                    SignatureRequest::getId,
                                    SignatureRequest::getStatus
                            ));

                    return new UserSignaturesSummary(userId, signatureRequestId2status.entrySet().size(), signatureRequestId2status);
                })
                .toList();
    }

    public List<DocumentSignatureRequestStatistics> getDocumentSignatureRequestStatistics() {
        return signatureRequestRepository.findDocumentSignatureRequestStatistics();
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
