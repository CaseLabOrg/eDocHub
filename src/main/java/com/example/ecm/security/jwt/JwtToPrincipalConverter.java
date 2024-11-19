package com.example.ecm.security.jwt;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.ecm.security.UserPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JwtToPrincipalConverter {

    public UserPrincipal convert(DecodedJWT jwt) {

        Long userId = jwt.getClaim("id").asLong();
        String login = String.valueOf(jwt.getClaim("sub"));

        List<SimpleGrantedAuthority> authorities = jwt.getClaim("a")
                .asList(String.class)
                .stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        return UserPrincipal.builder()
                .id(userId)
                .login(login)
                .authorities(authorities)
                .build();
    }

    private List<SimpleGrantedAuthority> extractAuth(DecodedJWT jwt) {
        return jwt.getClaim("a").asList(SimpleGrantedAuthority.class);
    }
}
