package com.aissummarizer.jennet.auth.dto;

import com.aissummarizer.jennet.user.dto.UserProfileDto;

/**
 * Response returned after successful authentication or token refresh.
 * <p>
 * Combines access token, refresh token, and user profile data.
 */
public record AuthResponse(
        String accessToken,
        String refreshToken,
        UserProfileDto profile
) { }