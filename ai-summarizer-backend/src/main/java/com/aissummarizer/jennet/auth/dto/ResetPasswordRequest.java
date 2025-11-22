package com.aissummarizer.jennet.auth.dto;

/**
 * Request object for completing a password reset.
 * <p>
 * Contains the reset token and the new password chosen by the user.
 */
public record ResetPasswordRequest(String token, String newPassword) { }