package com.aissummarizer.jennet.user.dto;

import com.aissummarizer.jennet.common.enums.Role;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO containing full user profile data.
 *
 * <p>Returned by:
 *  - GET /api/user/profile
 *
 * <p>Contains non-sensitive user fields that are safe to expose to the client.
 * Password is never included.
 */
public record UserProfileResponse(
        String id,
        String username,
        String email,
        String firstname,
        String lastname,
        String profileImageUrl,
        @JsonFormat(pattern = "MMM dd, yyyy 'on' HH:mm", locale = "en")
        LocalDateTime createdAt,
        @JsonFormat(pattern = "MMM dd, yyyy 'on' HH:mm", locale = "en")
        LocalDateTime updatedAt,
        Role role,
        List<UserSummarizationHistoryResponse> summarizationHistoryList
) {
}