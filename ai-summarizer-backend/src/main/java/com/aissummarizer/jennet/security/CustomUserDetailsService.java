package com.aissummarizer.jennet.security;

import com.aissummarizer.jennet.user.entity.UserEntity;
import com.aissummarizer.jennet.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

/**
 * Loads UserDetails for Spring Security authentication.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        UserEntity user = userRepository.findByUserName(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + username));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUserName())
                .password(user.getPassword())
                .roles(user.getRole().name().replace("ROLE_", ""))
                .build();
    }
}