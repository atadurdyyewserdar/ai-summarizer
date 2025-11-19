package com.aissummarizer.jennet.service;

/**
 * Service for password reset operations.
 * <p>
 * Handles generating secure password reset tokens, validating them,
 * and resetting user passwords in a safe and consistent manner.
 */
public interface PasswordResetService {

    /**
     * Generates a secure password reset token for the specified user.
     *
     * @param username user's username requesting password reset
     * @return generated reset token (for development/demo)
     */
    String createResetToken(String username);

    /**
     * Resets the user's password based on a valid reset token.
     *
     * @param token the password reset token provided to user
     * @param newPassword raw new password chosen by user
     */
    void resetPassword(String token, String newPassword);
}