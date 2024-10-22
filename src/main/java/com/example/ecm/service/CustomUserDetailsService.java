package com.example.ecm.service;

import com.example.ecm.repository.UserRepository;
import com.example.ecm.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByEmail(username).orElseThrow();
        return UserPrincipal.builder().id(user.getId())
                .login(user.getEmail())
                .authorities(user.getRoles().stream().map(r -> new SimpleGrantedAuthority(r.getName())).toList())
                .password(user.getPassword()).build();
    }
}
