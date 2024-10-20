package com.example.ecm.service;

import com.example.ecm.dto.responses.LoginResponse;
import com.example.ecm.security.UserPrincipal;
import com.example.ecm.security.jwt.JwtIssuer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtIssuer issuer;
    private final AuthenticationManager manager;

    public LoginResponse attemptLogin(String login, String password) {
        log.info("Attempting to login using login: " + login);

        var auth = manager.authenticate(new UsernamePasswordAuthenticationToken(login, password));

        SecurityContextHolder.getContext().setAuthentication(auth);

        var principle = (UserPrincipal)auth.getPrincipal();

        var roles = principle.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        var token = issuer.issue(principle.getId(), principle.getLogin(), roles);
        return LoginResponse.builder().token(token).build();
    }
}