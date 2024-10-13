package com.example.ecm.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtIssuer {
    private final JwtProperties properties;

    public String issue(Long id, String login, List<String> roles) {
        return JWT.create()
                .withSubject(login)
                .withClaim("id", id)
                .withClaim("a", roles)
                .withExpiresAt(Instant.now().plus(Duration.of(1, ChronoUnit.MINUTES)))
                .sign(Algorithm.HMAC256(properties.getSecretKey()));
    }
}
