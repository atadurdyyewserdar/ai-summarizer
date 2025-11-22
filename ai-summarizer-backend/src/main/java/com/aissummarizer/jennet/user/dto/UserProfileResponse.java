package com.aissummarizer.jennet.user.dto;

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
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<UserSummarizationHistoryResponse> summarizationHistoryList
) {
}