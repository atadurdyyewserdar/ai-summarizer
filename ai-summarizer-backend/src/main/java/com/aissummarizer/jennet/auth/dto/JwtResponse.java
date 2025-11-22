package com.aissummarizer.jennet.auth.dto;

/**
 * Response payload containing a generated JWT token.
 *
 * @param accessToken the signed JSON Web Token
 * @param refreshToken the signed refresh JSON Web Token
 */
public record JwtResponse(String accessToken, String refreshToken) {}