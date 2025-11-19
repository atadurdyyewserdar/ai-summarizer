package com.aissummarizer.jennet.repository;

import com.aissummarizer.jennet.model.user.PasswordResetTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for managing password reset tokens.
 * <p>
 * Provides methods to look up tokens by their value and
 * perform CRUD operations on token records.
 */
@Repository
public interface PasswordResetTokenRepository
        extends JpaRepository<PasswordResetTokenEntity, String> {

    /**
     * Retrieves a password reset token by its unique string value.
     *
     * @param token the plaintext token string sent to user
     * @return wrapper containing the token or empty if none found
     */
    Optional<PasswordResetTokenEntity> findByToken(String token);
}