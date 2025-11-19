package com.aissummarizer.jennet.service.impl;

import com.aissummarizer.jennet.exceptions.BadRequestException;
import com.aissummarizer.jennet.model.user.PasswordResetTokenEntity;
import com.aissummarizer.jennet.model.user.UserEntity;
import com.aissummarizer.jennet.repository.PasswordResetTokenRepository;
import com.aissummarizer.jennet.service.PasswordResetService;
import com.aissummarizer.jennet.service.UserService;
import com.aissummarizer.jennet.util.SecureTokenGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Implementation of the password reset service.
 * <p>
 * Handles token creation, expiration validation, token consumption,
 * and password updates using UserService.
 */
@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private static final long EXPIRATION_MINUTES = 15;

    private final PasswordResetTokenRepository tokenRepository;
    private final UserService userService;
    private final SecureTokenGenerator tokenGenerator;

    @Override
    public String createResetToken(String username) {
        UserEntity user = userService.getByUsername(username);

        String tokenValue = tokenGenerator.generateToken();

        PasswordResetTokenEntity token = PasswordResetTokenEntity.builder()
                .token(tokenValue)
                .userId(user.getId())                 // store just userId
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES))
                .used(false)
                .build();

        tokenRepository.save(token);

        // In production, send via email instead of returning
        return tokenValue;
    }

    @Override
    public void resetPassword(String tokenValue, String newPassword) {
        PasswordResetTokenEntity token = tokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new BadRequestException("Invalid reset token"));

        if (token.isUsed()) {
            throw new BadRequestException("Reset token already used");
        }

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Reset token has expired");
        }

        // Load user by ID (since we only store userId now)
        UserEntity user = userService.getById(token.getUserId());

        // Change password
        userService.changePassword(user.getId(), newPassword);

        // Mark token as used to prevent replay
        token.setUsed(true);
        tokenRepository.save(token);
    }
}