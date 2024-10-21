package com.example.ecm.controller;

import com.example.ecm.dto.requests.StartVotingRequest;
import com.example.ecm.dto.requests.VoteRequest;
import com.example.ecm.security.UserPrincipal;
import com.example.ecm.service.VotingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
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
    public void startVoting(@Valid @RequestBody StartVotingRequest request) {
        votingService.startVoting(request);
    }


    @PostMapping ("/{id}/vote")
    public void vote(@PathVariable Long id, @Valid @RequestBody VoteRequest voteRequest, UserPrincipal userPrincipal) {
        votingService.vote(id, voteRequest, userPrincipal);
    }
}
