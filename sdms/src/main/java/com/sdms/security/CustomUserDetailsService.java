package com.sdms.security;

import com.sdms.model.User;
import com.sdms.repository.UserRepository;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                    new UsernameNotFoundException("User not found: " + username)
                );

        // 🔐 CRITICAL FIX — Normalize DB role to Spring format
        String roleFromDb = user.getRole().toString();   // admin / user
        String normalizedRole = roleFromDb.toUpperCase(); // ADMIN / USER

        SimpleGrantedAuthority authority =
                new SimpleGrantedAuthority("ROLE_" + normalizedRole);

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of(authority)
        );
    }
}
