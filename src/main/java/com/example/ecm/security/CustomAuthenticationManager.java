package com.example.ecm.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationManager implements AuthenticationManager {

    private PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder();}

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        /*
        var user = userDetailService.loadUserByUsername(authentication.getName());

        if (!passwordEncoder().matches(authentication.getCredentials().toString(), user.getPassword())) {
            throw new BadCredentialsException("wrong password");
        }
        return new UserPrincipalAuthenticationToken(user);
         */
        return null;
    }

}
