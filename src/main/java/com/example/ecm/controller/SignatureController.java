package com.example.ecm.controller;

import com.example.ecm.aop.Loggable;
import com.example.ecm.dto.requests.CreateSignatureRequest;
import com.example.ecm.dto.requests.CreateSignatureRequestRequest;
import com.example.ecm.dto.requests.StartVotingRequest;
import com.example.ecm.dto.responses.CreateSignatureRequestResponse;
import com.example.ecm.dto.responses.GetSignatureResponse;
import com.example.ecm.dto.responses.StartVotingResponse;
import com.example.ecm.security.UserPrincipal;
import com.example.ecm.service.SignatureService;
import com.example.ecm.service.VotingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sign")
@Loggable
public class SignatureController {

    private final VotingService votingService;
    private final SignatureService signatureService;

    @GetMapping("/{id}")
    public ResponseEntity<CreateSignatureRequestResponse> getSignatureRequestById(@PathVariable Long id) {
        return ResponseEntity.ok(signatureService.getSignatureRequestById(id));
    }

    @GetMapping
    public ResponseEntity<List<CreateSignatureRequestResponse>> getAllSignatureRequests() {
        return ResponseEntity.ok(signatureService.getAllSignatureRequests());
    }

    @PostMapping("/send")
    public ResponseEntity<CreateSignatureRequestResponse> sendToSign(@Valid @RequestBody CreateSignatureRequestRequest signatureRequest, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(signatureService.sendToSign(signatureRequest, userPrincipal));
    }

    @PostMapping("/{id}")
    public ResponseEntity<GetSignatureResponse> sendToSign(@PathVariable Long id, @Valid @RequestBody CreateSignatureRequest request, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(signatureService.sign(id, request, userPrincipal));
    }

    @PostMapping("/voting")
    public ResponseEntity<StartVotingResponse> startVoting(@Valid @RequestBody StartVotingRequest request) {
        return ResponseEntity.ok(votingService.startVoting(request));
    }

}
