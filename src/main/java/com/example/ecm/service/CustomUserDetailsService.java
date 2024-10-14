package com.example.ecm.service;

import com.example.ecm.repository.UserRepository;
import com.example.ecm.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByEmail(username).orElseThrow();
        return UserPrincipal.builder().id(user.getId())
                .login(user.getEmail())
                //.authorities(List.of(new SimpleGrantedAuthority(user.getRole())))
                .password(user.getPassword()).build();
    }
}
