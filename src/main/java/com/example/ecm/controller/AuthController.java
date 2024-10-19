package com.example.ecm.controller;

import com.example.ecm.dto.requests.LoginRequest;
import com.example.ecm.dto.responses.LoginResponse;
import com.example.ecm.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService serviceAuth;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return serviceAuth.attemptLogin(request.getUsername(), request.getPassword());
    }
}