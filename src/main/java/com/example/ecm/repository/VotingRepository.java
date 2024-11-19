package com.example.ecm.repository;

import com.example.ecm.dto.responses.VotingSummary;
import com.example.ecm.model.Voting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VotingRepository extends JpaRepository<Voting, Long> {
    List<Voting> findByStatus(String status);

    // Утверждения в течение определенного периода времени
    @Query(value = """
        SELECT
            v.document_version_id AS documentVersionId,
            dv.title AS documentTitle,
            COUNT(DISTINCT sr.user_id_to) AS participantCount,
            STRING_AGG(DISTINCT CONCAT(u.name, ' ', u.surname), ', ') AS participants,
            v.status AS votingStatus,
            v.approval_threshold AS approvalThreshold,
            v.current_approval_rate AS currentApprovalRate
        FROM
            Votings v
        JOIN
            Document_Version dv ON v.document_version_id = dv.id
        JOIN 
            Signature_Requests sr ON sr.document_version_id = dv.id
        JOIN 
            Users u ON sr.user_id_to = u.id
        WHERE 
            dv.created_at BETWEEN :startDate AND :endDate
        GROUP BY 
            v.document_version_id, dv.title, v.status, v.approval_threshold, v.current_approval_rate
        ORDER BY 
            participantCount DESC
        """, nativeQuery = true)
    List<VotingSummary> findVotingSummaries(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );



}
