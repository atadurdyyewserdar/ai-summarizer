package com.aissummarizer.jennet.dto;

/**
 * Registration request payload.
 * <p>
 * Extends basic username/password input with profile fields.
 */
public record RegisterRequest(
        String username,
        String password,
        String firstName,
        String lastName,
        String email
) { }