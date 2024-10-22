package com.example.ecm.controller;

import com.example.ecm.dto.requests.StartVotingRequest;
import com.example.ecm.dto.responses.StartVotingResponse;
import com.example.ecm.service.VotingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/votings")
public class VotingController {

    private final VotingService votingService;

    @PostMapping
    public ResponseEntity<StartVotingResponse> startVoting(@Valid @RequestBody StartVotingRequest request) {
        return ResponseEntity.ok(votingService.startVoting(request));
    }

}
