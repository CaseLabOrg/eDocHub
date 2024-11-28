package com.example.ecm.security;

import com.example.ecm.exception.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.ecm.service.CustomUserDetailsService;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationManager implements AuthenticationManager {


    private final CustomUserDetailsService userDetailService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) {
        var user = userDetailService.loadUserByUsername(authentication.getName());
        if (!passwordEncoder.matches(authentication.getCredentials().toString(), user.getPassword())) {
            throw new AuthException("wrong password");
        }
        return new UserPrincipalAuthenticationToken(user);
    }

}
