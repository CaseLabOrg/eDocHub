package com.example.ecm.repository;

import com.example.ecm.model.DocumentVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentVersionRepository extends JpaRepository<DocumentVersion, Long> {
    Optional<DocumentVersion> findByDocumentIdAndVersionId(Long documentId, Long versionId);

    /**
     * Находит конкретную версию документа по идентификатору документа и идентификатору версии.
     *
     * @param documentId идентификатор документа.
     * @param versionId  идентификатор версии документа.
     * @return объект {@link Optional}, содержащий найденную версию документа, если она существует.
     */
    @Query("SELECT dv FROM DocumentVersion dv WHERE dv.id IN (SELECT MAX(dv2.id) FROM DocumentVersion dv2 GROUP BY dv2.document.id)")
    List<DocumentVersion> findLatestDocumentVersions();

}
