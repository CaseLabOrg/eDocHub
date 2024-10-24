package com.example.ecm.repository;

import com.example.ecm.model.DocumentVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocumentVersionRepository extends JpaRepository<DocumentVersion, Long> {
    Optional<DocumentVersion> findByDocumentIdAndVersionId(Long documentId, Long versionId);
}
