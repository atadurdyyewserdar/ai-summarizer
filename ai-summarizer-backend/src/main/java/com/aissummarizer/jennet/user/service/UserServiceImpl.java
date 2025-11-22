package com.aissummarizer.jennet.user.service;

import com.aissummarizer.jennet.common.exception.BadRequestException;
import com.aissummarizer.jennet.common.exception.NotFoundException;
import com.aissummarizer.jennet.summarization.entity.SummarizationEntity;
import com.aissummarizer.jennet.summarization.repository.SummarizationRepository;
import com.aissummarizer.jennet.user.dto.UserProfileResponse;
import com.aissummarizer.jennet.user.dto.UserSummarizationHistoryResponse;
import com.aissummarizer.jennet.user.entity.UserEntity;
import com.aissummarizer.jennet.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

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
    private final SummarizationRepository summarizationRepository;

    /**
     * Returns the user's profile information including a summarized view of
     * their summarization history.
     *
     * @param userName username of the user whose profile is being fetched
     * @return a DTO containing basic user profile information + history stats
     */
    @Override
    public UserProfileResponse getUserProfile(String userName) {

        // Fetch user
        UserEntity user = getByUsername(userName);

        // Fetch summarization history belonging to this user
        List<SummarizationEntity> history =
                summarizationRepository.findByUserId(user.getId());

        // Convert each entity → DTO (never expose entities directly)
        List<UserSummarizationHistoryResponse> historyDtos = history.stream()
                .map(h -> new UserSummarizationHistoryResponse(
                        h.getId(),
                        h.getCreatedAt(),
                        h.getInputText(),
                        h.getResult().getSummary(),
                        h.getSummaryType().toString()
                ))
                .toList();

        // Build final profile response
        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getProfileImageUrl(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                historyDtos
        );
    }


    @Override
    public UserEntity registerUser(String username,
                                   String rawPassword,
                                   String email,
                                   String firstName,
                                   String lastName) {

        userRepository.findByUsername(username).ifPresent(u -> {
            throw new BadRequestException("Username already exists");
        });

        userRepository.findByEmail(email).ifPresent(u -> {
            throw new BadRequestException("Email already registered");
        });

        UserEntity user = UserEntity.builder()
                .username(username)
                .password(encoder.encode(rawPassword))
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .role("ROLE_USER")
                .profileImageUrl(null)
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

    @Override
    public UserEntity updateProfile(String userId,
                                    String firstName,
                                    String lastName,
                                    String email,
                                    String profileImageUrl) {

        UserEntity user = getById(userId);

        // prevent email conflict
        userRepository.findByEmail(email).ifPresent(existing -> {
            if (!existing.getId().equals(userId)) {
                throw new BadRequestException("Email already in use.");
            }
        });

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setProfileImageUrl(profileImageUrl);

        return userRepository.save(user);
    }
}