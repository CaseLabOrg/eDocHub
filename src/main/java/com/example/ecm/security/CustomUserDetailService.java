package com.example.ecm.security;

import com.example.ecm.model.User;
import com.example.ecm.repository.UserRepository;
import com.example.ecm.saas.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username).orElseThrow();
        List<SimpleGrantedAuthority> roles = user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).toList();
        TenantContext.setCurrentTenant(user.getTenant().getId());
        return UserPrincipal.builder().id(user.getId())
                .login(user.getEmail())
                .authorities(roles)
                .password(user.getPassword()).build();
    }
}
