package com.aissummarizer.jennet.auth.service;


import com.aissummarizer.jennet.auth.dto.*;
import com.aissummarizer.jennet.user.dto.UserProfileDto;
import com.aissummarizer.jennet.user.entity.UserEntity;
import com.aissummarizer.jennet.security.JwtService;
import com.aissummarizer.jennet.user.service.UserService;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public AuthResponse login(LoginRequest request) {
        // AuthenticationManager triggers the full Spring Security authentication pipeline.
        // If credentials are invalid, an exception is thrown here automatically.
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        UserEntity user = userService.getByUserName(request.username());

        // Generate a signed JWT for the successfully authenticated user.
        String accessToken = jwtService.generateAccessToken(auth.getName());

        // Generate a signed JWT refresh token for the successfully authenticated user.
        String refreshToken = jwtService.generateRefreshToken(request.username());

        return new AuthResponse(
                accessToken,
                refreshToken,
                UserProfileDto.from(user, userService.userSummarizationHistory(user.getId()))
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void register(RegisterRequest request) {
        userService.registerUser(
                request
        );
    }

    @Override
    public void updatePassword(UpdatePasswordRequest request) {
        userService.changePassword(request.userName(), request.password());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AuthResponse refresh(String refreshToken) {
        // Example logic: validate refresh token and issue new tokens
        String username = jwtService.validateAndExtractUsernameFromRefreshToken(refreshToken);
        UserEntity user = userService.getByUserName(username);

        String newAccessToken = jwtService.generateAccessToken(username);
        String newRefreshToken = jwtService.generateRefreshToken(username);

        return new AuthResponse(
                newAccessToken,
                newRefreshToken,
                UserProfileDto.from(user, userService.userSummarizationHistory(user.getId()))
        );
    }
}