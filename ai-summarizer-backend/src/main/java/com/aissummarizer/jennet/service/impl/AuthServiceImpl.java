package com.aissummarizer.jennet.service.impl;


import com.aissummarizer.jennet.dto.*;
import com.aissummarizer.jennet.security.JwtService;
import com.aissummarizer.jennet.service.AuthService;
import com.aissummarizer.jennet.service.PasswordResetService;
import com.aissummarizer.jennet.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * Concrete implementation of authentication operations.
 * <p>
 * Coordinates user login, registration, and password reset flows
 * by delegating to user, JWT, and password-reset services.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final PasswordResetService passwordResetService;

    /**
     * {@inheritDoc}
     */
    @Override
    public JwtResponse login(LoginRequest request) {

        // AuthenticationManager triggers the full Spring Security authentication pipeline.
        // If credentials are invalid, an exception is thrown here automatically.
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        // Generate a signed JWT for the successfully authenticated user.
        String token = jwtService.generate(auth.getName());
        return new JwtResponse(token);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void register(RegisterRequest request) {
        userService.registerUser(request.username(), request.password());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String initiatePasswordReset(ForgotPasswordRequest request) {
        return passwordResetService.createResetToken(request.username());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void completePasswordReset(ResetPasswordRequest request) {
        passwordResetService.resetPassword(request.token(), request.newPassword());
    }
}