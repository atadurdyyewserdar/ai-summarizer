package com.aissummarizer.jennet.user.dto;

import com.aissummarizer.jennet.common.enums.Role;
import com.aissummarizer.jennet.user.entity.UserEntity;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Lightweight representation of user profile data to expose via APIs.
 */
public record UserProfileDto(
        String id,
        String username,
        String firstName,
        String lastName,
        String email,
        String profileImageUrl,
        Role role,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<UserSummarizationHistoryResponse> summarizationHistoryList
) {

    /**
     * Builds a DTO from a {@link UserEntity}.
     */
    public static UserProfileDto from(UserEntity user, List<UserSummarizationHistoryResponse> summarizationHistoryList) {
        return new UserProfileDto(
                user.getId(),
                user.getUserName(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getProfileImageUrl(),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                summarizationHistoryList
        );
    }
}