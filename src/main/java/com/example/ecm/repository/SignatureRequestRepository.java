package com.example.ecm.repository;

import com.example.ecm.dto.responses.*;
import com.example.ecm.model.SignatureRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface SignatureRequestRepository extends JpaRepository<SignatureRequest, Long> {
    List<SignatureRequest> findAllByUserToId(Long userIdTo);

    boolean existsByUserToIdAndDocumentVersionId(Long userToId, Long documentVersionId);

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
            AND dv.created_at BETWEEN :startDate AND :endDate
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
            EXTRACT(YEAR FROM dv.created_at) AS year,
            EXTRACT(WEEK FROM dv.created_at) AS week,
            COUNT(sr.id) AS approvalCount
        FROM
            Signature_Requests sr
        JOIN
            Document_Version dv ON sr.document_version_id = dv.id
        WHERE
            sr.status ILIKE 'approved'
            AND dv.created_at BETWEEN :startDate AND :endDate
        GROUP BY
            EXTRACT(YEAR FROM dv.created_at), EXTRACT(WEEK FROM dv.created_at)
        ORDER BY
            year, week
    """, nativeQuery = true)
    List<WeeklyApprovalStats> findWeeklyApprovals(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query(value = """
        WITH DailySignatures AS (
            SELECT
                EXTRACT(YEAR FROM dv.created_at) AS year,
                EXTRACT(MONTH FROM dv.created_at) AS month,
                EXTRACT(DAY FROM dv.created_at) AS day,
                COUNT(*) AS approvalsCount
            FROM
                Signature_Requests sr
            JOIN
                Document_Version dv ON sr.document_version_id = dv.id
            WHERE
                sr.status = 'Approved'
                AND dv.created_at BETWEEN :startDate AND :endDate
            GROUP BY
                EXTRACT(YEAR FROM dv.created_at), EXTRACT(MONTH FROM dv.created_at), EXTRACT(DAY FROM dv.created_at)
        )
        SELECT
            year,
            month,
            day,
            approvalsCount,
            SUM(approvalsCount) OVER (ORDER BY year, month, day) AS cumulativeCount,
            (approvalsCount - COALESCE(LAG(approvalsCount) OVER (ORDER BY year, month, day), 0)) AS dailyGrowth
        FROM
            DailySignatures
        ORDER BY
            year, month, day;
    """, nativeQuery = true)
    List<DailyApprovalStats> findDailyApprovals(
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
        JOIN 
            Document_Version dv ON sr.document_version_id = dv.id
        WHERE 
            sr.status IN ('Pending', 'Ignored')
            AND dv.created_at BETWEEN :startDate AND :endDate
        GROUP BY 
            sr.user_id_to, u.name, u.surname
        ORDER BY 
            ignoredVoteCount DESC
    """, nativeQuery = true)
    List<IgnoredVotes> findIgnoredVotes(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );



    @Query(value = """
            SELECT\s
                status,
                COUNT(*) AS requestCount
            FROM\s
                Signature_Requests
            GROUP BY\s
                status
            ORDER BY\s
                requestCount DESC
            """, nativeQuery = true)
    List<SignatureStatus> findCountSignatureRequestStatus();

    @Query(value = """
            SELECT dv.document_id AS documentId,
                   COUNT(sr) AS requestCount,
                   SUM(CASE WHEN sr.status ILIKE 'approved' THEN 1 ELSE 0 END) AS approvedCount,
                   SUM(CASE WHEN sr.status ILIKE 'rejected' THEN 1 ELSE 0 END) AS rejectedCount,
                   SUM(CASE WHEN sr.status ILIKE 'ignored' THEN 1 ELSE 0 END) AS ignoredCount,
                   SUM(CASE WHEN sr.status ILIKE 'pending' THEN 1 ELSE 0 END) AS pendingCount
            FROM signature_requests sr
            JOIN document_version dv ON sr.document_version_id = dv.id
            GROUP BY dv.document_id
    """, nativeQuery = true)
    List<DocumentSignatureRequestStatistics> findDocumentSignatureRequestStatistics();

    List<SignatureRequest> findAllByDelegatedToId(Long delegatedIdTo);
}
