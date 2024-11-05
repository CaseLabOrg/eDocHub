package com.example.ecm.repository;

import com.example.ecm.model.Voting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface VotingRepository extends JpaRepository<Voting, Long> {
    List<Voting> findByStatus(String status);

    // Утверждения в течение определенного периода времени
    @Query("""
        SELECT new map(v.id as votingId, v.documentVersion.id as documentVersionId, d.title as documentTitle,
                       v.status as votingStatus, v.createdAt as votingStartDate, v.deadline as votingDeadline,
                       s.userTo.id as approverUserId, u.name as approverName, u.surname as approverSurname)
        FROM Voting v
        JOIN v.documentVersion d
        JOIN SignatureRequest s ON s.documentVersion.id = v.documentVersion.id
        JOIN User u ON u.id = s.userTo.id
        WHERE v.createdAt BETWEEN :startDate AND :endDate
        ORDER BY v.createdAt
    """)
    List<Map<String, Object>> findApprovalsWithinPeriod(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    //  Онлайн-голосования в течение определенного периода времени
    @Query("""
        SELECT new map(v.id as votingId, v.documentVersion.id as documentVersionId, d.title as documentTitle,
                       v.status as votingStatus, v.currentApprovalRate as currentApprovalRate,
                       v.approvalThreshold as approvalThreshold, v.createdAt as votingStartDate,
                       v.deadline as votingDeadline, s.userTo.id as participantUserId,
                       u.name as participantName, u.surname as participantSurname)
        FROM Voting v
        JOIN v.documentVersion d
        JOIN SignatureRequest s ON s.documentVersion.id = v.documentVersion.id
        JOIN User u ON u.id = s.userTo.id
        WHERE v.createdAt BETWEEN :startDate AND :endDate
        ORDER BY v.createdAt
    """)
    List<Map<String, Object>> findOnlineVotesWithinPeriod(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // Пользователи, игнорирующие голоса в течение определенного периода времени
    @Query("""
        SELECT new map(v.id as votingId, v.documentVersion.id as documentVersionId, d.title as documentTitle,
                       v.status as votingStatus, v.createdAt as votingStartDate, v.deadline as votingDeadline,
                       COUNT(DISTINCT u.id) as ignoredCount)
        FROM Voting v
        JOIN v.documentVersion d
        LEFT JOIN User u ON u.id NOT IN
            (SELECT s.userTo.id FROM SignatureRequest s WHERE s.documentVersion.id = v.documentVersion.id)
        WHERE v.createdAt BETWEEN :startDate AND :endDate
        GROUP BY v.id, d.title, v.status, v.createdAt, v.deadline
        ORDER BY v.createdAt
    """)
    List<Map<String, Object>> findUsersIgnoringVotesWithinPeriod(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
