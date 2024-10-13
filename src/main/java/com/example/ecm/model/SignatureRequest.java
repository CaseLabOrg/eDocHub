package com.example.ecm.model;

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
    @JoinColumn(name = "document_id")
    private Document document;
    private Boolean approved;
}
