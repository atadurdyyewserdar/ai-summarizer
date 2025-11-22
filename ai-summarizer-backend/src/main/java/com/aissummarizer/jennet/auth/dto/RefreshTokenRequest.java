package com.aissummarizer.jennet.auth.dto;

/**
 * Request payload containing refresh token for token renewal.
 */
public record RefreshTokenRequest(String refreshToken) { }