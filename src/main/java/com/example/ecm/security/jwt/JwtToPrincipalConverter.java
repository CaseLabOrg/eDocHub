package com.example.ecm.security.jwt;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.ecm.security.UserPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JwtToPrincipalConverter {

    public UserPrincipal convert(DecodedJWT jwt) {
        return UserPrincipal.builder().login(jwt.getSubject())
                .authorities(extractAuth(jwt)).build();
    }

    private List<SimpleGrantedAuthority> extractAuth(DecodedJWT jwt) {
        return jwt.getClaim("a").asList(SimpleGrantedAuthority.class);
    }
}
