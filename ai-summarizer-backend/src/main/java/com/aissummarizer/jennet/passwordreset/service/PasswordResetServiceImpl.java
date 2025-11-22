package com.aissummarizer.jennet.passwordreset.service;

import com.aissummarizer.jennet.common.exception.BadRequestException;
import com.aissummarizer.jennet.passwordreset.entity.PasswordResetTokenEntity;
import com.aissummarizer.jennet.user.entity.UserEntity;
import com.aissummarizer.jennet.passwordreset.repository.PasswordResetTokenRepository;
import com.aissummarizer.jennet.user.service.UserService;
import com.aissummarizer.jennet.security.SecureTokenGenerator;
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

        // TODO Send via email instead of returning
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

        // Load user by ID (since we only store userId)
        UserEntity user = userService.getById(token.getUserId());

        // Change password
        userService.changePassword(user.getId(), newPassword);

        // Mark token as used to prevent replay
        token.setUsed(true);
        tokenRepository.save(token);
    }
}