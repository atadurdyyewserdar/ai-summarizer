package com.aissummarizer.jennet.auth.controller;

import com.aissummarizer.jennet.auth.dto.*;
import com.aissummarizer.jennet.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller exposing authentication endpoints such as login,
 * registration, password reset initiation, and password update.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Authenticates a user and returns a JWT token.
     *
     * @param request username + password
     * @return JWT response
     */
    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestParam String refreshToken) {
        return authService.refresh(refreshToken);
    }

    /**
     * Registers a new user account.
     *
     * @param request registration payload
     * @return confirmation message
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        System.out.println("============== request user");
        System.out.println(request);
        authService.register(request);
        return ResponseEntity.ok("User registered");
    }

    /**
     * Initiates a password reset request by generating a reset token.
     * <p>
     * NOTE: In production, the token would be emailed.
     * Returning it in the response is intended only for development.
     *
     * @param request payload containing the username
     * @return the generated reset token
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        String token = authService.initiatePasswordReset(request);
        return ResponseEntity.ok(java.util.Map.of("resetToken", token));
    }

    /**
     * Completes the password reset by validating the token
     * and updating the user's password.
     *
     * @param request token + new password
     * @return confirmation message
     */
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.completePasswordReset(request);
        return ResponseEntity.ok("Password updated successfully");
    }
}