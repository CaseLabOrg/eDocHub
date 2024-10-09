package com.example.ecm.pojo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name="document_types")
@Getter
@Setter
public class DocumentType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer typeId;
    private String name;
    @OneToMany(mappedBy = "documentType", fetch = FetchType.EAGER)
    private List<Attribute> attributes;
}
