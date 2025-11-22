package com.aissummarizer.jennet.user.dto;

import java.util.List;

/**
 * DTO returned to the frontend containing full user profile data.
 */
public record UserProfileResponse(
        String username,
        String name,
        String lastName,
        String email,
        String profileImageUrl
//        List<UserSummarizationHistoryResponse> history
) {}