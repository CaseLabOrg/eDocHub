package com.example.ecm.service;

import com.example.ecm.model.SignatureRequest;
import com.example.ecm.repository.SignatureRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SignatureRequestService {

    private final SignatureRequestRepository signatureRequestRepository;

    public List<SignatureRequest> findAllByUserIdTo(Long userIdTo) {
        return signatureRequestRepository.findAllByUserToId(userIdTo);
    }

}
