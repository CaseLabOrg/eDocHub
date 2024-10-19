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
    @JoinColumn(name = "document_version_id")
    private DocumentVersion documentVersion;

    /**
     * Флаг, который обозначает была ли поставлена подпись.
     * null - заявка пока не была обработана
     * false - документ был отклонен
     * true - документ был подписан
     */
    private String status;
}
