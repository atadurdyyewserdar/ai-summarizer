package com.aissummarizer.jennet.dto;

/**
 * Response payload containing a generated JWT token.
 *
 * @param token the signed JSON Web Token
 */
public record JwtResponse(String token) {}