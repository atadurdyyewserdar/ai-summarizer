package com.aissummarizer.jennet.user.service;

import com.aissummarizer.jennet.common.exception.BadRequestException;
import com.aissummarizer.jennet.common.exception.NotFoundException;
import com.aissummarizer.jennet.user.dto.UserProfileResponse;
import com.aissummarizer.jennet.user.dto.UserSummarizationHistoryResponse;
import com.aissummarizer.jennet.user.entity.UserEntity;
import com.aissummarizer.jennet.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

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
    public UserProfileResponse getUserProfile(String username) {

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return new UserProfileResponse(
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getProfileImageUrl()
        );
    }


    @Override
    public UserEntity registerUser(String username,
                                   String rawPassword,
                                   String firstName,
                                   String lastName,
                                   String email) {
        userRepository.findByUsername(username).ifPresent(u -> {
            throw new BadRequestException("Username already exists");
        });

        UserEntity user = UserEntity.builder()
                // ID will be generated in @PrePersist
                .username(username)
                .password(encoder.encode(rawPassword))
                .role("ROLE_USER")
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .profileImageUrl(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
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