package com.example.ecm.repository;

import com.example.ecm.model.SignatureRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SignatureRequestRepository extends JpaRepository<SignatureRequest, Long> {
    List<SignatureRequest> findAllByUserToId(Long userIdTo);
    boolean existsByUserToIdAndDocumentVersionId(Long userToId, Long documentVersionId);
}
