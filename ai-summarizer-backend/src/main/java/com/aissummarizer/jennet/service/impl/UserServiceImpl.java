package com.aissummarizer.jennet.service.impl;

import com.aissummarizer.jennet.exceptions.BadRequestException;
import com.aissummarizer.jennet.exceptions.NotFoundException;
import com.aissummarizer.jennet.model.user.UserEntity;
import com.aissummarizer.jennet.repository.UserRepository;
import com.aissummarizer.jennet.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Implementation of the UserService interface.
 * <p>
 * Handles high-level user operations such as registration and password updates.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    @Override
    public UserEntity registerUser(String username, String rawPassword) {
        userRepository.findByUsername(username).ifPresent(u -> {
            throw new BadRequestException("Username already exists");
        });

        UserEntity user = UserEntity.builder()
                .username(username)
                .password(encoder.encode(rawPassword))
                .role("ROLE_USER")
                .build();

        return userRepository.save(user);
    }

    @Override
    public UserEntity getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    public UserEntity getById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    public void changePassword(String userId, String rawNewPassword) {
        UserEntity user = getById(userId);
        user.setPassword(encoder.encode(rawNewPassword));
        userRepository.save(user);
    }
}