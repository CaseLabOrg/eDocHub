package com.example.ecm.model;

import com.example.ecm.model.enums.VotingState;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "votings")
public class Voting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "document_version_id")
    private DocumentVersion documentVersion;

    @OneToMany(mappedBy = "voting")
    private List<SignatureRequest> signatureRequests;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private VotingState status = VotingState.ACTIVE;

    @Column(nullable = false)
    private Float approvalThreshold;

    private Float currentApprovalRate;

    @Column(nullable = false)
    private LocalDate createdAt;

    @Column(nullable = false)
    private LocalDate deadline;

}
