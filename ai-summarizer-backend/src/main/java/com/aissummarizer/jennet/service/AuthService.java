package com.aissummarizer.jennet.service;


import com.aissummarizer.jennet.dto.*;

/**
 * Defines the authentication use-cases available in the system.
 * <p>
 * Includes user login, registration, password reset initiation,
 * and password reset completion.
 */
public interface AuthService {

    /**
     * Authenticates a user and returns a JWT token upon success.
     *
     * @param request login information: username + password
     * @return response containing a signed JWT
     */
    JwtResponse login(LoginRequest request);

    /**
     * Registers a new user account.
     *
     * @param request registration information
     */
    void register(RegisterRequest request);

    /**
     * Creates a password reset token and (normally) emails it to the user.
     *
     * @param request contains the username requesting the reset
     * @return the generated reset token (returned only for development/testing)
     */
    String initiatePasswordReset(ForgotPasswordRequest request);

    /**
     * Completes a password reset by validating the token and updating the password.
     *
     * @param request contains the token and new password
     */
    void completePasswordReset(ResetPasswordRequest request);
}