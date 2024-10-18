package com.example.ecm.repository;

import com.example.ecm.model.Attribute;
import com.example.ecm.model.DocumentVersion;
import com.example.ecm.model.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ValueRepository extends JpaRepository<Value, Long> {
    Optional<Value> findByAttributeAndDocumentVersion(Attribute attribute, DocumentVersion documentVersion);
}
