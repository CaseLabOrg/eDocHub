package com.example.ecm.model;

import com.example.ecm.model.enums.SignatureRequestState;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "signature_requests")
@Getter
@Setter
public class SignatureRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id_to")
    private User userTo;

    @ManyToOne
    @JoinColumn(name = "delegated_id_to")
    private User delegatedTo;

    @ManyToOne
    @JoinColumn(name = "voting_id")
    private Voting voting;

    @ManyToOne
    @JoinColumn(name = "document_version_id")
    private DocumentVersion documentVersion;

    @Enumerated(EnumType.STRING)
    private SignatureRequestState status = SignatureRequestState.PENDING;
}
