package com.aissummarizer.jennet.passwordreset.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity representing a password reset token.
 * <p>
 * In this SQLite setup we store only the userId as a String instead of a
 * true foreign key constraint, because Hibernate's schema update generates
 * ALTER TABLE ... ADD CONSTRAINT statements that SQLite does not support.
 */
@Entity
@Table(name = "password_reset_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class    PasswordResetTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true, length = 200)
    private String token;

    /**
     * ID of the user this token belongs to.
     * We keep it as a simple column (no DB-level FK) to avoid SQLite DDL issues.
     */
    @Column(name = "user_id", nullable = false, length = 100)
    private String userId;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private boolean used;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}