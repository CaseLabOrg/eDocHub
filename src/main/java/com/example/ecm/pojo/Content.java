package com.example.ecm.pojo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="document_content")
@Getter
@Setter
public class Content {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer documentId;
    private String content;
}
