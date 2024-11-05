package com.example.ecm.repository;

import com.example.ecm.dto.responses.IgnoredVotes;
import com.example.ecm.dto.responses.UserApproval;
import com.example.ecm.model.SignatureRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SignatureRequestRepository extends JpaRepository<SignatureRequest, Long> {
    List<SignatureRequest> findAllByUserToId(Long userIdTo);

    @Query(value = """
           SELECT
               sr.user_id_to AS userId,
               COUNT(sr.id) AS approvalCount,
               dv.title AS documentTitle,
               dv.version_id AS documentVersionId,
               sr.status AS approvalType
           FROM
               Signature_Requests sr
           JOIN
               Document_Version dv ON sr.document_version_id = dv.id
           WHERE
               sr.status = 'approved'
               AND sr.created_at BETWEEN :startDate AND :endDate
           GROUP BY
               sr.user_id_to, dv.title, dv.version_id, sr.status
           ORDER BY
               approvalCount DESC
           """, nativeQuery = true)
    List<UserApproval> findApprovalsByUsers(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );


    @Query(value = """
           SELECT
               sr.user_id_to AS userId,
               CONCAT(u.name, ' ', u.surname) AS userName,
               COUNT(sr.id) AS ignoredVoteCount
           FROM 
               Signature_Requests sr
           JOIN 
               Users u ON sr.user_id_to = u.id
           LEFT JOIN 
               Signatures s ON s.user_id = sr.user_id_to AND s.document_version_id = sr.document_version_id
           WHERE 
               sr.status IS NULL
               AND sr.created_at BETWEEN :startDate AND :endDate
           GROUP BY 
               sr.user_id_to, userName
           ORDER BY 
               ignoredVoteCount DESC
           """, nativeQuery = true)
    List<IgnoredVotes> findIgnoredVotes(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );


}
