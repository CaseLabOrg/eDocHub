package com.example.ecm.service;

import com.example.ecm.dto.responses.ActiveUser;
import com.example.ecm.dto.responses.IgnoredVotes;
import com.example.ecm.dto.responses.UserApproval;
import com.example.ecm.dto.responses.VotingSummary;
import com.example.ecm.repository.DocumentRepository;
import com.example.ecm.repository.SignatureRequestRepository;
import com.example.ecm.repository.VotingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticService {

    private final SignatureRequestRepository signatureRequestRepository;
    private final VotingRepository votingRepository;
    private final DocumentRepository documentRepository;


    public List<UserApproval> getApprovalsByUsers(LocalDateTime startDate, LocalDateTime endDate) {
        return signatureRequestRepository.findApprovalsByUsers(startDate, endDate);
    }

    public List<VotingSummary> getVotingSummaries(LocalDateTime startDate, LocalDateTime endDate) {
        return votingRepository.findVotingSummaries(startDate, endDate);
    }

    public List<ActiveUser> getMostActiveUsers(LocalDateTime startDate, LocalDateTime endDate) {
        return documentRepository.findMostActiveUsers(startDate, endDate);
    }

    public List<IgnoredVotes> getIgnoredVotes(LocalDateTime startDate, LocalDateTime endDate) {
        return signatureRequestRepository.findIgnoredVotes(startDate, endDate);
    }
}
